package com.cyssxt.tomato.service;

import com.alibaba.fastjson.JSON;
import com.cyssxt.common.bean.CountBean;
import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.common.constant.PageTypeConstant;
import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.DataTransformer;
import com.cyssxt.common.hibernate.transformer.IgnoreCaseResultTransformer;
import com.cyssxt.common.hibernate.transformer.KeyTransformer;
import com.cyssxt.common.hibernate.transformer.LongTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.PageResponse;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.*;
import com.cyssxt.tomato.constant.*;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dao.TodosRepository;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.entity.*;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import com.cyssxt.tomato.util.RepeatUtil;
import com.cyssxt.tomato.util.TimeUtil;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TodoService extends ActionService<ToDosEntity, TodoCreateReq, TodoPageReq, ToDoDto, TodoInfoDto> {

    private final static Logger logger = LoggerFactory.getLogger(TodoService.class);
    public static final String WORK_DAYS = "2,3,4,5,6";

    @Resource
    TodosRepository todosRepository;

//    @Resource
//    TodoDayRepository todoDayRepository;

    @Resource
    TagService tagService;

    @Resource
    TodoActionService todoActionService;

    @Resource
    SectionService sectionService;

    @Resource
    ProjectService projectService;

    @Override
    public BaseRepository getRepository() {
        return todosRepository;
    }

    @Override
    public Byte getContentType() {
        return ContentTypeConstant.TODO.getValue();
    }

    private void createTodoDayInfo(ToDosEntity toDosEntity, Calendar start, Integer generator){
        String dataStr = null;
        Timestamp executeTime = null;
        if (start != null) {
            dataStr = DateUtils.getDataFormatString(start.getTime(), DateUtils.YYYYMMDD);
            executeTime = new Timestamp(start.getTimeInMillis());
        }
        try {
            createTodoDayInfo(toDosEntity, dataStr, executeTime, generator,toDosEntity.getConcentrationDegree());
        } catch (ValidException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInsert(ToDosEntity toDosEntity, TodoCreateReq todoCreateReq) throws ValidException {
//        String contentId = toDosEntity.getRowId();
//        Byte parentType = toDosEntity.getParentType();
//        String parentId = toDosEntity.getParentId();
//        Boolean showFlag = toDosEntity.getShowFlag();
        delTodo(toDosEntity, DateUtils.getCurrentDataFormatInteger());
//        if(!StringUtils.isEmpty(parentId) && CommonUtils.isTrue(showFlag)) {
//            if (ContentTypeConstant.PROJECT.compare(parentType)) {
//                //更新所属待办信息
//                sectionService.updateTodoSection(parentId, contentId);
//            } else {
//                //更新责任信息
//                sectionService.updateDutySection(contentId, ContentTypeConstant.TODO.getValue(), parentId);
//            }
//        }
    }

    @Override
    public void onUpdate(ToDosEntity toDosEntity, ToDosEntity old,TodoCreateReq req) throws ValidException {
        String oldParentId = old.getParentId();
        Byte oldType = old.getParentType();
        String parentId = toDosEntity.getParentId();
        Byte type = old.getParentType();
        String rowId = toDosEntity.getRowId();
        move(oldParentId,oldType,parentId,type,rowId);
        tagService.updateReTags(req.getTagIds(),rowId,ContentTypeConstant.TODO);
        if(CommonUtils.isTrue(req.getAllFlag()) && CommonUtils.isTrue(req.getRepeatFlag())) {
            logger.info("onUpdate={}",JSON.toJSONString(req));
            delRepeat(toDosEntity,req);
        }
    }

    @Override
    public String orderBy(TodoPageReq req) {
        return " create_time desc,execute_time asc,row_id desc";
    }

    void updateHourAndMinute(ToDosEntity toDosEntity,Timestamp executeTime){
        Timestamp realTime = toDosEntity.getExecuteTime();
        if(executeTime==null || realTime==null){
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(executeTime);
        Calendar real = Calendar.getInstance();
        real.setTime(realTime);
        real.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
        real.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
        real.set(Calendar.SECOND,calendar.get(Calendar.SECOND));
        logger.info("updateHourAndMinute={}",realTime);
        toDosEntity.setExecuteTime(new Timestamp(real.getTimeInMillis()));
    }

    void delRepeat(ToDosEntity toDosEntity,TodoCreateReq req) throws ValidException {
        String repeatId = toDosEntity.getRepeatId();
        if(StringUtils.isEmpty(repeatId) && !CommonUtils.isTrue(toDosEntity.getShowFlag())){
            repeatId = toDosEntity.getRowId();
        }
        if(StringUtils.isEmpty(repeatId)){
            return;
        }
        Timestamp executeTime = req.getExecuteTime();
        String rowId = toDosEntity.getRowId();
        Integer today = DateUtils.getCurrentDataFormatInteger();
        ToDosEntity parent =JpaUtil.check(repeatId,todosRepository,true);
        String oldId = parent.getParentId();
        Byte type = parent.getParentType();
        req.parse(parent,(key, o) -> !"showFlag".equals(key) &&!"rowId".equals(key) && !"executeTime".equals(executeTime));
        parent.setShowFlag(false);
        updateHourAndMinute(parent,executeTime);
        todosRepository.save(parent);
        tagService.copyTags(rowId,parent.getRowId());
        todoActionService.copyActions(rowId,parent.getRowId());
        move(toDosEntity.getRowId(),type,oldId,parent.getParentType(),parent.getParentId());
        List<ToDosEntity> toDosEntities = todosRepository.queryTodoByRepeatId(today,repeatId);
        for(ToDosEntity item:toDosEntities){
            if(rowId.equals(item.getRowId())){
                continue;
            }
            List<String> filterKeys = Arrays.asList(new String[]{"createTime","executeTime","updateTime","dayDate","rowId","dateNo","concentration_degree","consume_time","finish_degree","finish_time"});
            String parentId = item.getParentId();
            Byte parentType = item.getParentType();
            req.parse(item, (key, o) -> {
                if(filterKeys.contains(key)){
                    return false;
                }
                return true;
            });
            item.setUpdateTime(DateUtils.getCurrentTimestamp());
            logger.info("result={}", JSON.toJSONString(item));
            logger.info("updateHourAndMinute={}",executeTime);
            updateHourAndMinute(item,executeTime);
            logger.info("updateHourAndMinute={}",item.getExecuteTime());
            todosRepository.save(item);
            logger.info("copyTags");
            tagService.copyTags(rowId,item.getRowId());
            logger.info("copyActions");
            todoActionService.copyActions(rowId,item.getRowId());
            logger.info("move");
            move(item.getRowId(),parentType,parentId,item.getParentType(),item.getParentId());

        }
    }

    private void createTodoDayInfo(ToDosEntity toDosEntity, String dateStr, Timestamp execTime, Integer generator, Integer degree) throws ValidException {
        String todoId = toDosEntity.getRowId();
        Integer dateNo = null;
        if(!StringUtils.isEmpty(dateStr)) {
            dateNo = Integer.valueOf(dateStr);
        }
        ToDosEntity child = todosRepository.findFirstByDateNoAndRepeatId(dateNo,todoId);
        if(child==null){
            child = (ToDosEntity)toDosEntity.clone();
            child.setRowId(CommonUtils.generatorKey());
        }
        String childId = child.getRowId();
        child.setShowFlag(true);
        child.setDelFlag(false);
        child.setUpdateTime(DateUtils.getCurrentTimestamp());
        child.setExecuteTime(execTime);
        child.setDateNo(dateNo);
        child.setGenerator(generator);
        child.setConcentrationDegree(degree);
        child.setRepeatId(todoId);
        tagService.copyTags(todoId,childId);
        logger.info("copyActions");
        todoActionService.copyActions(todoId,childId);
        move(child.getParentId(),child.getParentType(),toDosEntity.getParentId(),toDosEntity.getParentType(),child
                .getRowId());
        child.setParentId(toDosEntity.getParentId());
        child.setParentType(toDosEntity.getParentType());
        todosRepository.save(child);
    }

    @Override
    public void onMove(String oldVal, Byte oldType, String newVal, Byte newType,String contentId) throws ValidException {
        move(oldVal,oldType,newVal,newType,contentId);
    }

    /**
     * 在保存之前
     *
     * @param toDosEntity
     * @param todoCreateReq
     * @param userId
     * @throws ValidException
     */
    @Override
    protected void beforeSave(ToDosEntity toDosEntity, TodoCreateReq todoCreateReq, String userId) throws ValidException {
        String contentId = toDosEntity.getRowId();
        List<String> tagIds = todoCreateReq.getTagIds();
        boolean actionFlag = !CollectionUtils.isEmpty(todoCreateReq.getItems());
        boolean tagFlag = !CollectionUtils.isEmpty(tagIds);
        toDosEntity.setActionFlag(actionFlag);
        toDosEntity.setTagFlag(tagFlag);
        Timestamp executeTime = todoCreateReq.getExecuteTime();
        if(executeTime!=null){
            Integer dateNo = DateUtils.getDataFormatInteger(executeTime);
            toDosEntity.setDateNo(dateNo);
        }
        Byte status = toDosEntity.getStatus();
        if(toDosEntity.getRepeatFlag()==null){
            toDosEntity.setRepeatFlag(false);
        }
        if(toDosEntity.getEndFlag()==null){
            toDosEntity.setEndFlag(false);
        }
        if(toDosEntity.getShowFlag()==null){
            toDosEntity.setShowFlag(true);
        }
        if(status==null) {
            toDosEntity.setStatus(TaskStatusConstant.WAIT.getValue());
        }
        tagService.updateReTags(todoCreateReq.getTagIds(), contentId, ContentTypeConstant.TODO);
        todoActionService.updateTodoActions(todoCreateReq.getItems(), contentId);
        Boolean showFlag = toDosEntity.getShowFlag();
        if(CommonUtils.isTrue(showFlag)) {
            move(toDosEntity.getParentId(), toDosEntity.getParentType(), todoCreateReq.getParentId(), todoCreateReq.getParentType(), contentId);
        }
        delParent(toDosEntity);
    }

    void delParent(ToDosEntity toDosEntity){
        String parentId = toDosEntity.getParentId();
        Timestamp timestamp = toDosEntity.getExecuteTime();
        if ((StringUtils.isEmpty(parentId) && timestamp==null) && !CommonUtils.isTrue(toDosEntity.getTimeFlag())){
            toDosEntity.setParentId(ContentTypeConstant.INBOX.getValue()+"");
        }
        if(toDosEntity.getParentType()==null || toDosEntity.getParentType().byteValue()==0){
            toDosEntity.setParentType(ContentTypeConstant.NOPARENT.getValue());
        }
        if(StringUtils.isEmpty(toDosEntity.getParentId())){
            toDosEntity.setParentId(ContentTypeConstant.INBOX.getValue()+"");
        }
        if(toDosEntity.getParentType()==null || toDosEntity.getParentType().byteValue()==0){
            toDosEntity.setParentType(ContentTypeConstant.NOPARENT.getValue());
        }
    }

    void move(String oldParentId,Byte oldParentType,String newParentId,Byte newParentType,String todoId) throws ValidException {
        if(ContentTypeConstant.PROJECT.compare(oldParentType) && !StringUtils.isEmpty(oldParentId) && !oldParentId.equals(newParentId)){
            sectionService.delTodoSection(oldParentId,todoId);
        }
        if(ContentTypeConstant.PROJECT.compare(newParentType) && !StringUtils.isEmpty(newParentId)){
            sectionService.updateTodoSection(newParentId,todoId);
        }

        if(ContentTypeConstant.DUTY.compare(oldParentType) && !StringUtils.isEmpty(oldParentId) && !oldParentId.equals(newParentId)){
            sectionService.delDutySection(oldParentId,todoId,ContentTypeConstant.TODO.getValue());
        }
        if(ContentTypeConstant.PROJECT.compare(newParentType) && !StringUtils.isEmpty(newParentId)){
            sectionService.updateDutySection(todoId,ContentTypeConstant.TODO.getValue(),newParentId);
        }
    }

    @Override
    protected void afterSave(ToDosEntity toDosEntity, String userId) {
//        delTodo(toDosEntity, DateUtils.getCurrentDataFormatInteger());
    }

    public void delTodo(ToDosEntity toDosEntity, Integer generator) {
        String todoId = toDosEntity.getRowId();
        Timestamp executeTime = toDosEntity.getExecuteTime();
        if (executeTime != null) {
            Calendar now = Calendar.getInstance();
            Calendar start = Calendar.getInstance();
            start.setTime(executeTime);
            Byte repeatType = toDosEntity.getRepeatType();
            Byte repeatUnit = null;toDosEntity.getRepeatUnit();
            Integer repeatUnitValue = null;toDosEntity.getRepeatUnitValue();
            String repeatExecDay = null;toDosEntity.getRepeatExecDay();
            //转化时间
            if(RepeatTypeConstant.EVERY_WEEK.compare(repeatType)){
                repeatUnit = RepeatUnitConstant.WEEK.getValue();
                repeatUnitValue = 1;
                repeatExecDay = Calendar.MONDAY+"";
            }else if(RepeatTypeConstant.EVERY_DAY.compare(repeatType)){
                repeatUnit = RepeatUnitConstant.DAY.getValue();
                repeatUnitValue = 1;
            }else if(RepeatTypeConstant.EVERY_WORK_DAY.compare(repeatType)){
                repeatUnit = RepeatUnitConstant.WEEK.getValue();
                repeatUnitValue = 1;
                repeatExecDay = WORK_DAYS;
            }else if(RepeatTypeConstant.EVERY_MONTH.compare(repeatType)){
                repeatUnit = RepeatUnitConstant.MONTH.getValue();
                repeatUnitValue = 1;
                repeatExecDay = "3";
            }else if(RepeatTypeConstant.USER_DEFINED.compare(repeatType)){
                repeatUnit = toDosEntity.getRepeatUnit();
                repeatUnitValue = toDosEntity.getRepeatUnitValue();
                repeatExecDay = toDosEntity.getRepeatExecDay();
            }
            Calendar end = null;
            Byte status = toDosEntity.getStatus();
            String repeatId = toDosEntity.getRepeatId();
            boolean endFlag = CommonUtils.isTrue(toDosEntity.getEndFlag());
            boolean repeatFlag = CommonUtils.isTrue(toDosEntity.getRepeatFlag());
            //开始调度任务
            if (!endFlag && repeatFlag && repeatUnitValue!=null && repeatUnitValue!=0 && repeatUnit!=null && !TaskStatusConstant.FINISH.compare(status) && StringUtils.isEmpty(repeatId)) {
                Timestamp endTime = RepeatTypeConstant.USER_DEFINED.compare(repeatType)?toDosEntity.getRepeatEndTime():toDosEntity.getEndTime();
                Byte repeatEndType = toDosEntity.getRepeatEndType();
                //到期结束则提示
                if(RepeatTypeConstant.USER_DEFINED.compare(repeatType) && RepeatEndTypeConstant.NONE.compare(repeatEndType)&&(endTime==null || endTime.getTime()<new Date().getTime())||(!RepeatTypeConstant.USER_DEFINED.compare(repeatType) && endTime!=null && endTime.getTime()<new Date().getTime())){//如果超过重复截止时间则不执行
                    return;
                }
                //删除今天之后的天数
                todosRepository.delAfterToday(DateUtils.getCurrentDataFormatInteger(),todoId);
                RepeatUtil.RepeatCallback repeatCallback = new RepeatUtil.RepeatCallback(){
                    @Override
                    public void onItem(Calendar start) {
                        createTodoDayInfo(toDosEntity, start, generator);
                    }
                };
                if (RepeatUnitConstant.DAY.compare(repeatUnit)) {
                    end = RepeatUtil.nextDay(start, now, repeatUnitValue, repeatCallback,endTime);
                } else if (RepeatUnitConstant.WEEK.compare(repeatUnit)) {
                    end = RepeatUtil.weekNextDay(start, now, repeatUnitValue, repeatExecDay, repeatCallback,endTime);
                } else if (RepeatUnitConstant.MONTH.compare(repeatUnit)) {
                    end = RepeatUtil.monthNextDay(start, now, repeatUnitValue, repeatExecDay, repeatCallback,endTime);
                }
                if(end!=null) {
                    todosRepository.updateNextExecTime(todoId, new Timestamp(end.getTimeInMillis()));
                }
            }
        }
    }

    @Override
    public ToDosEntity createEntity(TodoCreateReq todoCreateReq) throws ValidException {
        logger.info("create entity");
        ToDosEntity toDosEntity = new ToDosEntity();
        if(CommonUtils.isTrue(todoCreateReq.getRepeatFlag())){
            toDosEntity.setShowFlag(false);
        }else {
            toDosEntity.setShowFlag(true);
        }
        todoCreateReq.parse(toDosEntity);
        Byte[] repeatExecDays = todoCreateReq.getRepeatExecDays();
        if (repeatExecDays != null && repeatExecDays.length > 0) {
            String repeatDays = ArrayUtil.join(repeatExecDays, CharConstant.COMMA);
            toDosEntity.setRepeatExecDay(repeatDays);//执行天数
        }
        toDosEntity.setStatus(TaskStatusConstant.WAIT.getValue());
        return toDosEntity;
    }

    @Override
    public void where(TodoPageReq req, List<String> list) {
        list.add(" A.show_flag=1 ");
        String dateNo = req.getDateNo();
        if (!StringUtils.isEmpty(dateNo)) {
            list.add(" DATE_FORMAT(A.execute_time,'%Y%m%d')=:dateNo ");
        }
        Integer  endExecuteDateNo = req.getEndExecuteDateNo();
        if(endExecuteDateNo!=null){
            list.add("DATE_FORMAT(A.end_time,'%Y%m%d')=:endExecuteDateNo ");
        }
        Integer endDateNo = req.getEndDateNo();
        if (!StringUtils.isEmpty(endDateNo)) { //截止时间
            list.add("A.row_id in (select row_id from to_dos U where (DATE_FORMAT(U.end_time,'%Y%m%d')=:endDateNo and repeat_flag=1) or (DATE_FORMAT(U.execute_time,'%Y%m%d')=:endDateNo and (repeat_flag=0 or repeat_flag is null)");
        }

        Byte pageType = req.getPageType();
        if (PageTypeConstant.INBOX.compare(pageType)) {
            list.add(" A.parent_id='-1'");
        } else if (PageTypeConstant.TODAY.compare(pageType)) {
            //今天
            list.add("A.date_no=:today");
        }
        String projectId = req.getProjectId();
        if (!StringUtils.isEmpty(projectId)) {
            list.add(" A.row_id in (select to_do_id from project_section where project_id=:projectId and ifnull(to_do_id,'')!='' group by to_do_id)");
            list.add(" A.parent_type=1");
        }
        List<String> parentIds = req.getParentIds();
        if(!CollectionUtils.isEmpty(parentIds)){
            list.add(" A.parent_id in :parentIds");
        }
        Byte parentType = req.getParentType();
        if(parentType!=null){
            list.add(" A.parent_type=:parentType");
        }
        Byte notStatus = req.getNotStatus();
        if(notStatus!=null){
            list.add("A.status!=:notStatus");
        }
    }

    @Override
    protected Class getDtoClass() {
        return ToDoDto.class;
    }

    @Override
    protected ActionPageParameter<TodoPageReq> getPageParameter() {
        return new ActionPageParameter<TodoPageReq>() {
            @Override
            public void append(Query query, TodoPageReq req) {
                String dateNo = req.getDateNo();
                Byte pageType = req.getPageType();
                String projectId = req.getProjectId();
                if (!StringUtils.isEmpty(dateNo)) {
                    query.setParameter("dateNo", dateNo);
                }
                Integer endExecuteDateNo = req.getEndExecuteDateNo();
                if(endExecuteDateNo!=null){
                    query.setParameter("endExecuteDateNo", endExecuteDateNo);
                }
                if (PageTypeConstant.TODAY.compare(pageType)) {
                    query.setParameter("today", DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMMDD));
                }
                if (!StringUtils.isEmpty(projectId)) {
                    query.setParameter("projectId", projectId);
                }
                Integer endDateNo = req.getEndDateNo();
                if (!StringUtils.isEmpty(endDateNo)) {
                    query.setParameter("endDateNo", endDateNo);
                }
                List<String> parentIds = req.getParentIds();
                if(!CollectionUtils.isEmpty(parentIds)){
                    query.setParameter("parentIds",parentIds);
                }
                Byte parentType = req.getParentType();
                if(parentType!=null){
                    query.setParameter("parentType",parentType);
                }
                Byte notStatus = req.getNotStatus();
                if(notStatus!=null){
                    query.setParameter("notStatus",notStatus);
                }
            }
        };
    }

    @Override
    public boolean filterTagId() {
        return true;
    }

    @Override
    public void afterList(KeyTransformer transformer, List<ToDoDto> list, TodoPageReq req, ResponseData responseData) {

        queryTags(transformer, list);
        String sql = getFullSql(req);
        sql = "select sum(B.plan_time) as total_num from (" + sql + ") B";
        try {
            List<Long> totals = QueryUtil.applyNativeList(sql, req, entityManager, getPageParameter(), new LongTransformer());
            logger.info("responseData={}",responseData);
            responseData.setExtra(CollectionUtils.isEmpty(totals) || totals.get(0)==null ? 0 : totals.get(0));
        } catch (ValidException e) {
            e.printStackTrace();
        }
    }

    @Override
    public KeyTransformer getTransformer(Class alias) {
        return new IgnoreCaseResultTransformer(alias, "rowid");
    }

    @Override
    public String getListSql(TodoPageReq req) {
        return "select A.repeat_end_time,A.repeat_flag,A.parent_id,A.parent_type,A.plan_time,A.title,A.row_id as row_id," +   //DATE_FORMAT(execute_time,'%Y%m%d') as
                "  execute_time,if(V.action_count>0,true,false) as actionFlag,if(W.tag_count>0,true,false) as tagFlag,A.status," +
                " A.end_time as end_time from to_dos A " +
                " left join (select count(T.row_id) action_count,T.to_do_id  from to_do_actions T where T.del_flag=0 group by T.to_do_id) V on V.to_do_id=A.row_id " +
                " left join (select count(R.tag_id) as tag_count,R.content_id from re_tag R where R.del_flag=0 and R.content_type=0 group by R.content_id) W on W.content_id=A.row_id ";
//                " left join re_tag B on A.row_id=B.content_id and B.content_type="+getContentType();
    }

    @Override
    public TodoInfoDto detail(InfoReq req) throws ValidException {
        String contentId = req.getContentId();
        ToDosEntity entity = JpaUtil.check(contentId, getRepository(), true);
        List<TagDto> tags = tagService.tags(contentId, ContentTypeConstant.TODO);
        List<ActionDto> actions = todoActionService.findByTodoId(contentId);
        TodoInfoDto tagInfoDto = new TodoInfoDto(entity, tags, actions);
        return tagInfoDto;
    }

    /**
     * 如果是重复并且标记为今天待办 则父待办不处理
     * @param toDosEntity
     * @param req
     * @return
     */
    @Override
    public boolean finishFilter(ToDosEntity toDosEntity, FinishReq req) {
        boolean repeatFlag = toDosEntity.getRepeatFlag()!=null && toDosEntity.getRepeatFlag();
        boolean todayFlag = req.getTodayFlag()!=null && req.getTodayFlag();
        if(repeatFlag && todayFlag){
            return false;
        }
        toDosEntity.setFinishDegree(req.getDegree());
        return true;
    }

    /**
     * 获取收件箱中的总待办数
     *
     * @param userId
     * @return
     */
    public Long totalInbox(String userId) {
        CountBean total = todosRepository.countInbox(userId);
        return total.getCount();
    }

    /**
     * 今天的待办数
     *
     * @param userId
     * @return
     */
    public Long totalTotay(String userId) throws ValidException {
        TodoPageReq todoPageReq = new TodoPageReq();
        todoPageReq.setPageType(PageTypeConstant.TODAY.getValue());
        String sql = getFullSql(todoPageReq);
        Long total = QueryUtil.applyTotal(sql, entityManager, todoPageReq, (QueryUtil.PageParameter<TodoPageReq>) (query, todoPageReq1) -> {
            query.setParameter("userId",userId);
            query.setParameter("notStatus",2);
            query.setParameter("today",DateUtils.getCurrentDataFormatInteger());
        });
        return total;
    }
    public ResponseData updateActionStatus(UpdateActionStatusReq req) {
        return todoActionService.updateActionStatus(req);
    }

    public ResponseData inboxs(InboxPageReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        Page<ToDoDto> page = todosRepository.inbox(userId, req.toPageable());
        PageResponse<ToDoDto> pageResponse = new PageResponse(page);
        List<ToDoDto> toDoDtoList = pageResponse.getItems();
        List<String> todoIds = toDoDtoList.stream().map(u -> u.getRowId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(todoIds)) {
            List<TagDto> tagDtos = tagService.tags(todoIds, ContentTypeConstant.TODO);
            List<TodoActionDto> actions = todoActionService.actions(todoIds);
            Map<String, List<TagDto>> tagMaps = tagDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
            Map<String, List<TodoActionDto>> todoActionMap = actions.stream().collect(Collectors.groupingBy(TodoActionDto::getParentId));
            for (ToDoDto toDoDto : toDoDtoList) {
                String rowId = toDoDto.getRowId();
                toDoDto.setActions(todoActionMap.get(rowId));
                toDoDto.setTags(tagMaps.get(rowId));
            }
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        return responseData;
    }

    public ResponseData todays(TodayPageReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String dateNo = DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMMDD);
        PageResponse<Map> pageResponse = QueryUtil.applyNativePageByFile(SQL_NAME_CONSTANT.TODO_TODAY, entityManager, new QueryUtil.PageParameter<PageReq>() {

            @Override
            public void initParam(Query query, PageReq pageReq) throws ValidException {
                query.setParameter("userId", userId);
                query.setParameter("dateNo", dateNo);
            }
        }, Map.class);
        responseData.setData(pageResponse);
        return responseData;
    }

    public ResponseData time(TimeListReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String dateNo = DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMMDD);
        CountBean countBean = todosRepository.countPlanTime(userId, Integer.valueOf(dateNo));
        responseData.setData(countBean);
        return responseData;
    }

    public ResponseData updateDegree(UpdateDegreeReq req) throws ValidException {
        String todoId = req.getTodoId();
        ToDosEntity toDosEntity = JpaUtil.check(todoId, todosRepository, true);
        checkUserAuth(toDosEntity);
        Integer degree = req.getDegree();
//        Integer dateNo = DateUtils.getCurrentDataFormatInteger();
//        ToDoDayInfoEntity toDoDayInfoEntity = todoDayRepository.findFirstByDateNoAndToDoId(dateNo, todoId);
//        if (toDoDayInfoEntity != null) {
//            toDoDayInfoEntity.setConcentrationDegree(degree);
//            todoDayRepository.save(toDoDayInfoEntity);
//        }else{
//            toDoDayInfoEntity = todoDayRepository.findFirstByToDoId(todoId);
//            if(toDoDayInfoEntity==null){
//                createTodoDayInfo(toDosEntity, null, DateUtils.getCurrentDataFormatInteger());
//                toDoDayInfoEntity = todoDayRepository.findFirstByToDoId(todoId);
//            }
//            toDoDayInfoEntity.setConcentrationDegree(degree);
//            todoDayRepository.save(toDoDayInfoEntity);
//        }
        toDosEntity.setConcentrationDegree(degree);
//        toDosEntity.setStatus(TaskStatusConstant.FINISH.getValue());
        todosRepository.save(toDosEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    /**
     * 重复待办
     *
     * @return
     */
    public List<ToDosEntity> repeats() {
        List<ToDosEntity> repeats = todosRepository.findAll((Specification<ToDosEntity>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get(ToDosEntity_.repeatFlag), true));
            predicates.add(criteriaBuilder.equal(root.get(ToDosEntity_.delFlag), false));
            predicates.add(criteriaBuilder.equal(root.get(ToDosEntity_.showFlag), false));
            predicates.add(criteriaBuilder.equal(root.get(ToDosEntity_.endFlag), false));
            predicates.add(criteriaBuilder.greaterThan(root.get(ToDosEntity_.nextExecuteTime), DateUtils.getCurrentTimestamp()));
            Predicate[] tmp = new Predicate[predicates.size()];
            query.where(predicates.toArray(tmp));
            return query.getRestriction();
        });
        return repeats;
    }

    /**
     * 标记项目完成度
     *
     * @param req
     * @return
     */
    public ResponseData updateFinishDegree(FinishDegreeReq req) throws ValidException {
//        String contentId = req.getContentId();
//        Integer dateNo = req.getDateNo();
////        if (!StringUtils.isEmpty(dateNo)) {
////            dateNo = DateUtils.getCurrentDataFormatInteger();
////        }
//        ToDosEntity toDosEntity = JpaUtil.check(contentId,todosRepository,true);
//        toDosEntity.setFinishDegree(req.getDegree());
//        toDosEntity.set
//        ToDoDayInfoEntity toDoDayInfoEntity = todoDayRepository.findFirstByDateNoAndToDoId(dateNo, contentId);
//        if (toDoDayInfoEntity == null) {
//            throw new ValidException(MessageCode.TODO_DAY_INFO_ERROR);
//        }
//        toDoDayInfoEntity.setFinishDegree(req.getDegree());
//        todoDayRepository.save(toDoDayInfoEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    /**
     * 待办的时间分布
     *
     * @param start
     * @param end
     */
    public TimeDto timeDistribute(Integer start, Integer end, String userId) throws ValidException {
        TimeDto timeDto = QueryUtil.applyFirstByFile(SQL_NAME_CONSTANT.TIME_DISTRIBUTE, entityManager, (query, req) -> {
            query.setParameter("userId",userId);
            query.setParameter("start",start);
            query.setParameter("end",end);
        },TimeDto.class);
//        TimeDto timeDto = todosRepository.timeDistribute(start, end, userId, TaskStatusConstant.FINISH.getValue());
        return timeDto;
    }

    public TodoInfo getinfo(String userId) throws ValidException {
        TodoInfo todoInfo = QueryUtil.applyFirstByFile(SQL_NAME_CONSTANT.USER_TODO_INFO, entityManager, (query, req) -> query.setParameter("userId",userId),TodoInfo.class);
        return todoInfo;
    }

    /**
     * 责任耗时统计
     *
     * @param start
     * @param end
     * @param userId
     * @return
     */
    public List<DutyDistributeDto> dutyDistribute(Integer start, Integer end, String userId) throws ValidException {
        List<DutyDistributeDto> dutyDistributeDtos = QueryUtil.applyNativeListWithIctByFile( SQL_NAME_CONSTANT.DUTY_DISTRIBUTE, entityManager, (query, req) -> {
            query.setParameter("userId", userId);
            query.setParameter("status", TaskStatusConstant.FINISH.getValue());
            query.setParameter("start", start);
            query.setParameter("end", end);
        }, DutyDistributeDto.class);
        return dutyDistributeDtos;
    }

    /**
     * 过去的预测
     *
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData endTimes(PageReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        String sql = "select * from (select * from (SELECT DATE_FORMAT(end_time,'%Y%m%d') AS end_time,DATE_FORMAT(end_time,'%Y-%m-%d') AS end_time_str,count(row_id) as num FROM to_dos WHERE del_flag=0 AND ifnull(end_time,'') !='' and status!=2 AND user_id=:userId group by end_time) B WHERE B.end_time<:endTime GROUP BY end_time) C ORDER BY end_time DESC";
        String endTime = DateUtils.getCurrentDateFormatStr(DateUtils.YYYYMMDD);
        PageResponse<EndTimeResult> results = QueryUtil.applyNativePageWithIct(sql, entityManager, req, (QueryUtil.PageParameter<PageReq>) (query, pageReq) -> {
            query.setParameter("endTime", endTime);
            query.setParameter("userId",userId);
        }, EndTimeResult.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(results);
        return responseData;
    }

    @Override
    public void beforeFinish(FinishReq req, ToDosEntity toDosEntity) {
        toDosEntity.setConsumeTime(req.getConsumeTime());
    }

    @Override
    public void onFinish(FinishReq req, ToDosEntity toDosEntity) throws ValidException {
        String repeatId = toDosEntity.getRepeatId();
        if(CommonUtils.isTrue(req.getAllFlag()) && !StringUtils.isEmpty(repeatId)) {
            todosRepository.updateTodoChild(repeatId);
        }
    }

    @Override
    public void onCancel(ToDosEntity toDosEntity) {
        super.onCancel(toDosEntity);
        String parentId = toDosEntity.getParentId();
        Byte parentType = toDosEntity.getParentType();
        if(ContentTypeConstant.PROJECT.compare(parentType) && !StringUtils.isEmpty(parentId)) {
            CancelReq cancelReq = new CancelReq();
            cancelReq.setContentId(parentId);
            try {
                projectService.cancel(cancelReq);
            } catch (ValidException e) {
                e.printStackTrace();
            }
        }
    }

    public static String filterTags(){
        return " ((  " +
                "SELECT '-1' AS tag_id,row_id AS content_id,0 AS content_type,0 as del_flag FROM (  " +
                "SELECT row_id,'-1' FROM to_dos A WHERE A.user_id=:userId AND A.del_flag =0 AND A.row_id NOT IN (  " +
                "SELECT content_id FROM re_tag A WHERE A.del_flag=0 AND A.content_type=0)) B) UNION ALL (  " +
                "SELECT tag_id,content_id,content_type,0 as del_flag FROM re_tag A WHERE A.del_flag=0) UNION ALL ((  " +
                "SELECT '-1' AS tag_id,row_id AS content_id,1 AS content_type,0 as del_flag FROM (  " +
                "SELECT row_id,'-1' FROM project_info A WHERE A.user_id=:userId AND A.del_flag =0 AND A.row_id NOT IN (  " +
                "SELECT content_id FROM re_tag A WHERE A.del_flag=0 AND A.content_type=1)) B))) ";
    }

    public ResponseData waitTask(TodoPageReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        StringBuffer stringBuffer = new StringBuffer("select * from ((select A.status,A.create_time,A.execute_time,A.repeat_end_time,A.title,A.row_id," +
                "if(V.action_count>0,true,false) as action_flag,if(W.tag_count>0,true,false) as tag_flag, " +
//                "case when A.parent_type=1 then B.title WHEN A.parent_type=2 THEN C.title ElSE A.title END as title," +
//                "case when A.parent_type=1 then B.row_id WHEN A.parent_type=2 THEN C.row_id ElSE '' END as parent_id," +
                "if(A.repeat_flag=1,-1,0) as content_type,A.end_time from to_dos A " +
//                "left join project_info B on A.parent_id=B.row_id and A.parent_type=1" +
//                "left join duty_info C on A.parent_id=C.row_id and A.parent_type=2" +
                "left join (select count(T.row_id) action_count,T.to_do_id  from to_do_actions T where T.del_flag=0 group by T.to_do_id) V on V.to_do_id=A.row_id " +
                "left join (select count(R.tag_id) as tag_count,R.content_id from re_tag R where R.del_flag=0 and R.content_type=0 group by R.content_id) W on W.content_id=A.row_id " +
                "where A.del_flag=0 and (A.`status`!=2 or A.status is null) and A.user_id=:userId and A.parent_type=-2 order by A.repeat_flag desc) " +
                "union all " +
                "(select * from (\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\tB.STATUS,\n" +
                "\t\t\t\tB.create_time,\n" +
                "\t\t\t\t'' AS execute_time,\n" +
                "\t\t\t\t'' AS repeat_end_time,\n" +
                "\t\t\t\tB.title,\n" +
                "\t\t\t\tB.row_id,\n" +
                "\t\t\t\tFALSE AS action_fag,\n" +
                "\t\t\t\tFALSE AS tag_flag,\n" +
                "\t\t\t\t1 AS content_type,\n" +
                "\t\t\t\tB.end_time \n" +
                "\t\t\tFROM\n" +
                "\t\t\t\tproject_info B\n" +
                "\t\t\tWHERE\n" +
                "\t\t\t\t( B.`status` != 2 OR B.STATUS IS NULL ) \n" +
                "\t\t\t\tAND B.user_id =:userId\n" +
                "\t\t\t\tAND B.del_flag = 0 \n" +
                "\t\t\tORDER BY\n" +
                "\t\t\t\tB.create_time DESC \n" +
                "\t\t\t) A GROUP BY A.row_id)" +
                "union all " +
                "(select * from (select A.status,A.create_time,'' as execute_time,'' as repeat_end_time,B.title,B.row_id,false as action_fag" +
                ",false as tag_flag,2 as content_type,A.end_time from to_dos A,duty_info B,duty_section C where " +
                "C.duty_id=B.row_id and A.row_id=C.content_id and C.content_type=0 and C.del_flag=0 and B.del_flag=0 and (A.`status`!=2 or A.status is null) and A.user_id=:userId and A.del_flag=0 and A.parent_type=2 order by A.create_time desc) A ");
        stringBuffer.append(" GROUP BY A.row_id)) A ");
        List<String> tagIds = req.getTagIds();
        if(!CollectionUtils.isEmpty(tagIds)){
            stringBuffer.append(" where A.row_id in (select T.content_id from "+filterTags()+" T where tag_id in :tagIds and T.del_flag=0 and T.content_type= if(A.content_type=-1,0,A.content_type)) ");
        }
        stringBuffer.append(" order by A.content_type asc,A.execute_time asc,A.create_time desc ");
        List<String> projectIds = new ArrayList<>();
        List<String> dutyIds = new ArrayList<>();
        PageResponse pageResponse = QueryUtil.applyNativePage(stringBuffer.toString(), entityManager, req, (QueryUtil.PageParameter<PageReq>) (query, pageReq) -> {
            query.setParameter("userId",userId);
            if(!CollectionUtils.isEmpty(tagIds)){
                query.setParameter("tagIds",tagIds);
            }
        },new DataTransformer(TodoWaitDto.class, result -> {
            if(result instanceof TodoWaitDto) {
                TodoWaitDto todoWaitDto = (TodoWaitDto)result;
                Byte type = todoWaitDto.getContentType();
                if (ContentTypeConstant.PROJECT.compare(type)){
                    projectIds.add(todoWaitDto.getRowId());
                }else if(ContentTypeConstant.DUTY.compare(type)){
                    dutyIds.add(todoWaitDto.getRowId());
                }
            }
        }));
        logger.info("waitTask==>>projectIds={}",projectIds);
        Map<String,List<ToDoDto>> projectTodos =  listNotFinishTodoId(projectIds,ContentTypeConstant.PROJECT);
        Map<String,List<ToDoDto>> dutyTodos =  listNotFinishTodoId(dutyIds,ContentTypeConstant.DUTY);
        List<TodoWaitDto> todoWaitDtos = pageResponse.getItems();
        for(TodoWaitDto todoWaitDto:todoWaitDtos){
            Byte type = todoWaitDto.getContentType();
            String rowId = todoWaitDto.getRowId();
            List<ToDoDto> items = null;
            if (ContentTypeConstant.PROJECT.compare(type)){
                items = projectTodos.get(rowId);
            }else if(ContentTypeConstant.DUTY.compare(type)){
                items = dutyTodos.get(rowId);
            }
            todoWaitDto.setChilds(items);
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        return responseData;
    }

    public Map<String,List<ToDoDto>> listNotFinishTodoId(List<String> parentIds,ContentTypeConstant contentTypeConstant) throws ValidException {
        if(CollectionUtils.isEmpty(parentIds)){
            return new HashMap<>();
        }
        TodoPageReq todoPageReq = new TodoPageReq();
        todoPageReq.setParentIds(parentIds);
        todoPageReq.setParentType(contentTypeConstant.getValue());
        todoPageReq.setNotStatus(TaskStatusConstant.FINISH.getValue());
        String sql =  getFullSql(todoPageReq);
        KeyTransformer transformer = getTransformer(ToDoDto.class);
        List<ToDoDto> todos = QueryUtil.applyNativeList(sql,todoPageReq,entityManager,getPageParameter(),transformer);
        queryTags(transformer, todos);
        Map<String,List<ToDoDto>> result = todos.stream().collect(Collectors.groupingBy(ToDoDto::getParentId));
        return result;
    }



    public Map<String,List<ToDoDto>> planByProjectId(List<String> parentIds,Integer dateNo,ContentTypeConstant contentTypeConstant) throws ValidException {
        if(CollectionUtils.isEmpty(parentIds)){
            return new HashMap<>();
        }
        TodoPageReq todoPageReq = new TodoPageReq();
        todoPageReq.setParentIds(parentIds);
        todoPageReq.setParentType(contentTypeConstant.getValue());
        todoPageReq.setEndExecuteDateNo(dateNo);
        String sql =  getFullSql(todoPageReq);
        KeyTransformer transformer = getTransformer(ToDoDto.class);
        List<ToDoDto> todos = QueryUtil.applyNativeList(sql,todoPageReq,entityManager,getPageParameter(),transformer);
        queryTags(transformer, todos);
        Map<String,List<ToDoDto>> result = todos.stream().collect(Collectors.groupingBy(ToDoDto::getParentId));
        return result;
    }

    private void queryTags(KeyTransformer transformer, List<ToDoDto> todos) {
        List<String> keys = transformer.getKeys();
        if (!CollectionUtils.isEmpty(keys) && !CollectionUtils.isEmpty(todos)) {
            List<TagDto> tagDtos = tagService.tags(keys, ContentTypeConstant.TODO);
            Map<String, List<TagDto>> cache = tagDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
            for (ToDoDto toDoDto : todos) {
                String todoId = toDoDto.getRowId();
                toDoDto.setTags(cache.get(todoId));
            }
        }
    }
    public ResponseData plans(TagFilterReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        StringBuffer stringBuffer =new StringBuffer(FileUtil.getContent(SQL_NAME_CONSTANT.PLAN_PARENT));
        List<String> tagIds = req.getTagIds();
        if(!CollectionUtils.isEmpty(tagIds)){
            stringBuffer.append(" and E.row_id in (select T.content_id from "+filterTags()+" T where tag_id in :tagIds and T.del_flag=0 and T.content_type= if(C.content_type=2,0,C.content_type)) ");
        }
        stringBuffer.append(" order by E.date_no,E.create_time desc");
        KeyTransformer transformer = new IgnoreCaseResultTransformer(TodoPlanDto.class);

//        select T.row_id from re_tag T where tag_id in (2) and T.del_flag=0 and T.content_type=0
        List<TodoPlanDto> items = QueryUtil.applyNativeList(stringBuffer.toString(), null, entityManager, (query, req1) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,7);
            if(!CollectionUtils.isEmpty(tagIds)){
                query.setParameter("tagIds",tagIds);
            }
            query.setParameter("userId",userId);
//            query.setParameter("lastDay",DateUtils.getDataFormatInteger(calendar));
            },transformer);
        List<String> keys = transformer.getKeys();
        if(!CollectionUtils.isEmpty(keys)){
            Map<String,List<ToDoDto>> toDoDtos = planByProjectId(keys,DateUtils.getCurrentDataFormatInteger(),ContentTypeConstant.PROJECT);
            for(TodoPlanDto todoPlanDto:items){
                todoPlanDto.setTodos(toDoDtos.get(todoPlanDto.getParentId()));
            }
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(items);
        return  responseData;
    }


    @Override
    public void onDel(ToDosEntity toDosEntity) {
        String todoId = toDosEntity.getRowId();
        delByTodoId(todoId);
    }

    private void delByTodoId(String todoId) {
//        todoDayRepository.delByTodoId(todoId);
    }

    @Override
    public void onCopy(ToDosEntity toDosEntity, String oldId, String parentId, Byte parentType) throws ValidException {
        super.onCopy(toDosEntity, oldId, parentId, parentType);
        toDosEntity.setParentId(parentId);
        toDosEntity.setParentType(parentType);
        delParent(toDosEntity);
        onInsert(toDosEntity,null);
        afterSave(toDosEntity,TomatoUserLoginListener.getUserId());
    }

    public ResponseData transfer(TransferReq req) throws ValidException {
        String contentId = req.getContentId();
        ToDosEntity toDosEntity = JpaUtil.check(contentId,getRepository(),true);
        toDosEntity.setDelFlag(true);
        todosRepository.save(toDosEntity);
        String title = toDosEntity.getTitle();
        String remark = toDosEntity.getContent();
        Timestamp executeTime = toDosEntity.getExecuteTime();
        //创建项目
        ProjectCreateReq projectCreateReq = new ProjectCreateReq();
        projectCreateReq.setTitle(title);
        projectCreateReq.setRemark(remark);
        ResponseData<String> responseData = projectService.create(projectCreateReq);
        String projectId = responseData.getData();
        List<ActionDto> actions = todoActionService.findByTodoId(contentId);
        List<ProjectItem> projectItems = new ArrayList<>();
        //动作转待办
        if(!CollectionUtils.isEmpty(actions)){
            for(ActionDto actionDto:actions){
                TodoCreateReq todoCreateReq = new TodoCreateReq();
                todoCreateReq.setTitle(actionDto.getContent());
                todoCreateReq.setStatus(actionDto.getStatus());
                todoCreateReq.setParentId(projectId);
                todoCreateReq.setParentType(ContentTypeConstant.PROJECT.getValue());
                todoCreateReq.setExecuteTime(executeTime);
                ResponseData<String> todoResp= create(todoCreateReq);
                String todoId = todoResp.getData();
                projectItems.add(new ProjectItem(todoId));
            }
        }
        //项目保存
        ProjectCreateReq projectSaveReq = new ProjectCreateReq();
        //1.标记框，项目显示未标记2.标题一致，3.笔记转化为备注，4.动作升级为待办，5.执行时间不变，6.不再重复
        projectSaveReq.setTitle(toDosEntity.getTitle());
        projectSaveReq.setExecuteTime(toDosEntity.getExecuteTime());
        projectSaveReq.setStatus(TaskStatusConstant.WAIT.getValue());
        projectSaveReq.setRemark(remark);
        projectSaveReq.setContentId(projectId);
        projectSaveReq.setProjectItems(projectItems);
        projectSaveReq.setExecuteTime(executeTime);
        projectService.save(projectSaveReq);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData finishs(FinishPageReq req) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String userId = TomatoUserLoginListener.getUserId();
        KeyTransformer keyTransformer = new IgnoreCaseResultTransformer(FinishDto.class, "fullmonth");
        PageResponse<FinishDto> pageResponse = QueryUtil.applyNativePageWithIctByFile(SQL_NAME_CONSTANT.FINISH_TIME, entityManager, req, (QueryUtil.PageParameter<FinishPageReq>) (query, finishPageReq) -> query.setParameter("userId", userId), keyTransformer);
        List<String> keys = keyTransformer.getKeys();
        List<String> todoIds = new ArrayList<>();
        StringBuffer listSql = new StringBuffer(FileUtil.getContent(SQL_NAME_CONSTANT.FINISH_LIST_ITEM));
        List<String> tagIds = req.getTagIds();
        if (!CollectionUtils.isEmpty(tagIds)) {
            listSql.append(" and C.row_id in (select T.content_id from " + filterTags() + " T where tag_id in :tagIds and T.del_flag=0 and T.content_type= C.type) ");
        }
        listSql.append(" ORDER BY full_month DESC,finish_time desc,day_no desc ");
//        List<String> projectIds = new ArrayList<>();
        ResultTransformer transformer = new DataTransformer(FinishItemDto.class, (DataTransformer.Filter<FinishItemDto>) result -> {
            Byte type = result.getType();
            if (ContentTypeConstant.TODO.compare(type)) {
                todoIds.add(result.getRowId());
            }
//            if(ContentTypeConstant.PROJECT.compare(type)){
//                projectIds.add(result.getRowId());
//            }
        });
        if (!CollectionUtils.isEmpty(keys)) {
            List<FinishItemDto> list = QueryUtil.applyNativeList(listSql.toString(), entityManager, (query, req1) -> {
                query.setParameter("userId", userId);
                query.setParameter("fullMonths", keys);
                if (!CollectionUtils.isEmpty(tagIds)) {
                    query.setParameter("tagIds", tagIds);
                }
            }, transformer);
            List<TagDto> tagDtos = tagService.tags(todoIds, ContentTypeConstant.TODO);
//        Map<String,List<FinishItemDto>> childs = getChilds(projectIds,userId);
            Map<String, List<TagDto>> tags = tagDtos.stream().collect(Collectors.groupingBy(TagDto::getContentId));
            for (FinishItemDto finishItemDto : list) {
                if (ContentTypeConstant.TODO.compare(finishItemDto.getType())) {
                    finishItemDto.setTags(tags.get(finishItemDto.getRowId()));
                }
//            if(ContentTypeConstant.PROJECT.compare(finishItemDto.getType())){
//                finishItemDto.setChilds(childs.get(finishItemDto.getRowId()));
//            }
            }
            Map<Integer, List<FinishItemDto>> items = list.stream().collect(Collectors.groupingBy(FinishItemDto::getFullMonth));
            List<FinishDto> result = pageResponse.getItems();
            for (FinishDto finishDto : result) {
                Integer fullMonth = finishDto.getFullMonth();
                finishDto.setItems(items.get(fullMonth));
            }
        }
        responseData.setData(pageResponse);
        return responseData;
    }

    public QueryUtil.PageParameter getPageParameter(FilesPageReq req) throws ValidException {
        Integer startDateNo ;
        Integer endDateNo;
        Integer monthNo = req.getMonthNo();
        if(monthNo!=null && monthNo!=0){
            TimeArea timeArea = TimeUtil.getTimArea(monthNo);
            startDateNo = timeArea.getStartDateNo();
            endDateNo = timeArea.getEndDateNo();
        }else{
            startDateNo = req.getStartDateNo();
            endDateNo = req.getEndDateNo();
            if(startDateNo==null || endDateNo==null){
                throw new ValidException(MessageCode.MONTHNO_OR_AREA_TIME_MUST_EXIST_ONE);
            }
        }
        String userId = TomatoUserLoginListener.getUserId();
        return (QueryUtil.PageParameter<FilesPageReq>) (query, req1) -> {
            query.setParameter("startDateNo",startDateNo);
            query.setParameter("endDateNo",endDateNo);
            query.setParameter("userId",userId);
            if(!CollectionUtils.isEmpty(req.getTagIds())){
                query.setParameter("tagIds",req.getTagIds());
            }
        };
    }

    public ResponseData files(FilesPageReq req) throws ValidException {
        List<String> tagIds = req.getTagIds();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        StringBuffer sb = new StringBuffer(FileUtil.getContent(SQL_NAME_CONSTANT.FILE_PARENT));
        if(!CollectionUtils.isEmpty(tagIds)) {
            sb.append(" and B.row_id in (select T.content_id from "+filterTags()+" T where tag_id in :tagIds and T.del_flag=0 and T.content_type= 1)  ");
        }
        sb.append(" and COALESCE(D.total,0) = 0  and B.`status`=2 and B.user_id=:userId) E where E.date_no>=:startDateNo and E.date_no<=:endDateNo ");
        PageResponse<FinishPageDto> pageDtoPageResponse = QueryUtil.applyNativePageWithIct(sb.toString(), entityManager, req, getPageParameter(req),FinishPageDto.class);
        StringBuffer todoCount = new StringBuffer("select count(E.row_id) from (");
        todoCount.append("select DATE_FORMAT(A.finish_time,'%Y%m%d') as date_no,A.row_id from to_dos A where A.del_flag=0 and ifnull(A.parent_type,0)!=1 and A.`status`=2 and A.user_id=:userId");
        if(!CollectionUtils.isEmpty(tagIds)){
            todoCount.append(" and A.row_id in (select T.content_id from "+filterTags()+" T where tag_id in :tagIds and T.del_flag=0 and T.content_type= 0) ");
        }
        todoCount.append(" ) E where E.date_no>=:startDateNo and E.date_no<=:endDateNo ");

        List<Long> totalCount = QueryUtil.applyNativeList(todoCount.toString(),req, entityManager,getPageParameter(req),new LongTransformer());

        Long total = 0L;
        if(!CollectionUtils.isEmpty(totalCount)){
            total = totalCount.get(0);
        }
        responseData.setData(pageDtoPageResponse);
        responseData.setExtra(total);
        return responseData;
    }


    public ResponseData fileTodos(FilesPageReq req) throws ValidException {

        StringBuffer stringBuffer = new StringBuffer("select * from (select A.execute_time,A.title,A.finish_time,A.row_id,Date_format(A.finish_time,'%Y%m%d') as date_no from to_dos A Where A.`status`=2 and A.del_flag=0 and  " +
                "ifnull(A.parent_type,0)!=1 and A.user_id=:userId ) E where E.date_no>=:startDateNo and E.date_no<=:endDateNo");
        List<String> tagIds = req.getTagIds();
        if(!CollectionUtils.isEmpty(tagIds)){
            stringBuffer.append(" and E.row_id in (select T.content_id from "+filterTags()+" T where tag_id in :tagIds and T.del_flag=0 and T.content_type= 0) ");
        }
        stringBuffer.append(" order by E.finish_time desc");
        PageResponse<FileTodoDto> pageResponse = QueryUtil.applyNativePageWithIct(stringBuffer.toString(), entityManager, req, getPageParameter(req),FileTodoDto.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(pageResponse);
        return responseData;
    }

    /**
     * 获取截止待办
     * @return
     */
    public Map<Object,List<String>> getAllEnd(Integer dateNo) throws ValidException {
        String sql = "SELECT E.getui_id as client_id,D.total as value FROM (SELECT A.user_id,count(A.row_id) total FROM to_dos A,user_info B WHERE A.row_id IN (SELECT row_id FROM to_dos U WHERE (DATE_FORMAT(U.repeat_end_time,'%Y%m%d')=:dateNo " +
                "AND repeat_flag=1) OR (DATE_FORMAT(U.execute_time,'%Y%m%d')=:dateNo " +
                "AND (repeat_flag=0 or repeat_flag is null))) AND A.user_id=B.row_id GROUP BY user_id) D,user_push_relation E WHERE D.user_id=E.user_id AND E.del_flag=0 ";
        List<PushClientDto> result = QueryUtil.applyNativeListWithIct(sql, entityManager, new QueryUtil.ReqParameter() {
            @Override
            public void initParam(Query query, BaseReq req) throws ValidException {
                query.setParameter("dateNo",dateNo);
            }
        }, PushClientDto.class);
        Map<Object,List<String>> clientsGroup = result.stream().collect(Collectors.groupingBy(PushClientDto::getValue,Collectors.mapping(PushClientDto::getClientId, Collectors.toList())));
        return clientsGroup;
    }

    public List<WillStartDto> getWillStartDto() throws ValidException {
//        String sql = "applyNativeListWithIctselect 1 as timeLabel from to_dos";
        List<WillStartDto> willStartDtos = QueryUtil.applyNativeListWithIctByFile(SQL_NAME_CONSTANT.WILL_START,entityManager,WillStartDto.class);
        return willStartDtos;
    }

    public int updatePushFlag(List<String> rowIds){
        if(CollectionUtils.isEmpty(rowIds)){
            return 0;
        }
        return todosRepository.updatePushFlag(rowIds);
    }

    public Map<String,List<FinishItemDto>> getChilds(List<String> projectIds,String userId) throws ValidException {
        logger.info("getChilds={}",projectIds);
        if(CollectionUtils.isEmpty(projectIds)){
            return new HashMap<>();
        }
        String sql = "SELECT DATE_FORMAT(A.finish_time,'%Y%m') AS full_month,DATE_FORMAT(A.finish_time,'%d') AS day_no,A.title,A.status,A.finish_time,A.row_id,0 as type,A.parent_id FROM to_dos A WHERE  A.del_flag=0 and A.parent_id in :projectIds and A.parent_type=1 AND A.finish_time IS NOT NULL AND A.status=2 AND A.user_id=:userId";
        List<String> todoIds = new ArrayList<>();
        ResultTransformer resultTransformer = new DataTransformer(FinishItemDto.class, result -> {
            if(result instanceof FinishItemDto){
                todoIds.add(((FinishItemDto) result).getRowId());
            }
        });
        List<FinishItemDto> result = QueryUtil.applyNativeList(sql, entityManager, (query, req) -> {
            query.setParameter("userId",userId);
            query.setParameter("projectIds",projectIds);
        },resultTransformer );
        Map<String,List<TagDto>> tagDtoMap = tagService.tagToMap(todoIds,ContentTypeConstant.TODO);
        for (FinishItemDto finishItemDto:result){
            finishItemDto.setTags(tagDtoMap.get(finishItemDto.getRowId()));
        }
        return result.stream().collect(Collectors.groupingBy(FinishItemDto::getParentId));
    }

    public ResponseData stop(TodoStopReq req) throws ValidException {
        String contentId = req.getContentId();
        ToDosEntity toDosEntity = JpaUtil.check(contentId,todosRepository,true);
        String repeatId = toDosEntity.getRepeatId();
        if(StringUtils.isEmpty(repeatId)){
            throw new ValidException(MessageCode.NOT_REPEAT_TODO);
        }
        ToDosEntity parent = JpaUtil.check(repeatId,todosRepository,true);
        parent.setEndFlag(true);
        todosRepository.save(parent);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public void stopRepeat(String contentId) {
        todosRepository.stopRepeatByParentId(contentId);
    }
}