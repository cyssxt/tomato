package com.cyssxt.tomato.dao;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.LongTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.*;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.constant.SQL_NAME_CONSTANT;
import com.cyssxt.tomato.constant.TimeActionTypeConstant;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.entity.TimeActionEntity;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import com.cyssxt.tomato.service.TodoService;
import com.cyssxt.tomato.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class TimeService {
    @Resource
    TimeRepository timeRepository;

    @Resource
    TodoService todoService;

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserService userService;

    public final static String DEFAULT_COLOR="#62656c";



    public ResponseData start(TimeStartReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        Timestamp startTime = Optional.ofNullable(req.getStartTime()).orElse(DateUtils.getCurrentTimestamp());
        String todoId = req.getTodoId();
        String childId = req.getChildId();
        String parentId = req.getTimeId();
        TimeActionEntity timeActionEntity = new TimeActionEntity();
        timeActionEntity.setStartTime(startTime);
        timeActionEntity.setDateNo(DateUtils.getDataFormatInteger(startTime,DateUtils.YYYYMMDD));
        if(!StringUtils.isEmpty(todoId)){
            timeActionEntity.setToDoId(todoId);
        }
        if(!StringUtils.isEmpty(childId)){
            timeActionEntity.setToDoId(childId);
        }
        Long timeValue = userService.getUserTimeSetting(userId);
        Long totalTime = timeValue * 1000;
        if(!StringUtils.isEmpty(parentId)){
            TimeActionEntity parent = JpaUtil.check(parentId,timeRepository,true);
            if(parent.getTotalTime()==null){
                throw new ValidException(MessageCode.TIME_CANNOT_REPEAT_START);
            }
            parent.setLastFlag(false);
            parent.setUpdateTime(DateUtils.getCurrentTimestamp());
            timeRepository.save(parent);
            String pTodoId = parent.getToDoId();
            String parentChildId = parent.getChildId();
            if(!StringUtils.isEmpty(pTodoId)){
                timeActionEntity.setToDoId(pTodoId);
            }
            if(!StringUtils.isEmpty(parentChildId)){
                timeActionEntity.setChildId(parentChildId);
            }
            timeActionEntity.setParentId(parentId);
            timeActionEntity.setLastFlag(true);
            timeActionEntity.setActionId(parent.getActionId());
            timeValue = timeActionEntity.getConfigTime();
            timeValue = Optional.ofNullable(timeValue).orElse(25*60L);
            totalTime = timeValue * 1000;
            totalTime = totalTime - parent.getTotalTime();
        }else{
            timeActionEntity.setActionId(CommonUtils.generatorKey());
        }
        timeActionEntity.setConfigTime(timeValue);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND,totalTime.intValue());
        timeActionEntity.setExpireTime(new Timestamp(calendar.getTimeInMillis()));
        timeActionEntity.setUserId(userId);
        timeActionEntity.setType(TimeActionTypeConstant.RUNNING);
        timeRepository.save(timeActionEntity);
        String timeId = timeActionEntity.getRowId();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        TimeDto timeDto = new TimeDto(timeId);
        responseData.setData(timeDto);
        return responseData;
    }

    public void checkTimeAction(TimeActionEntity timeActionEntity,Byte currentType) throws ValidException {
        Byte type = timeActionEntity.getType();
        if(type!=null && currentType!=null && currentType<=type){
            throw new ValidException(MessageCode.CAN_NOT_BE_MODIFY);
        }
    }

    public void checkTimeAction(TimeActionEntity timeActionEntity) throws ValidException {
         checkTimeAction(timeActionEntity,TimeActionTypeConstant.PAUSE);
    }

    public ResponseData stop(TimeStopReq req) throws ValidException {
        String timeId = req.getTimeId();
        Timestamp endTime = Optional.ofNullable(req.getEndTime()).orElse(DateUtils.getCurrentTimestamp());
        Long totalTime = req.getTotalTime();
//        Integer degree = Optional.ofNullable(req.getDegree()).orElseThrow(()->new ValidException(MessageCode.TIME_DEGREE_NOT_NULL));
        TimeActionEntity timeActionEntity = JpaUtil.check(timeId,timeRepository,true);
        checkTimeAction(timeActionEntity,TimeActionTypeConstant.FINISH);
        timeActionEntity.setType(TimeActionTypeConstant.FINISH);
        endTime = Optional.ofNullable(endTime).orElse(DateUtils.getCurrentTimestamp());
//        if(timeActionEntity.getExpireTime()!=null && timeActionEntity.getExpireTime().getTime()<endTime.getTime()){
//            endTime = timeActionEntity.getExpireTime();
//        }
        timeActionEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
        timeActionEntity.setEndTime(endTime);
        if(totalTime==null || totalTime==0){
            Timestamp startTime = timeActionEntity.getStartTime();
            startTime = Optional.ofNullable(startTime).orElse(DateUtils.getCurrentTimestamp());
            totalTime = (endTime.getTime()-startTime.getTime());
        }
        timeActionEntity.setTotalTime(totalTime);
        timeRepository.save(timeActionEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData pause(TimePauseReq req) throws ValidException {
        TimeActionEntity timeActionEntity = JpaUtil.check(req.getTimeId(),timeRepository,true);
        checkTimeAction(timeActionEntity);
        Timestamp endTime = Optional.ofNullable(req.getEndTime()).orElse(DateUtils.getCurrentTimestamp());
        timeActionEntity.setEndTime(endTime);
        Long totalTime = req.getTotalTime();
        if(totalTime==null){
            Timestamp startTime = Optional.ofNullable(timeActionEntity.getStartTime()).orElse(DateUtils.getCurrentTimestamp());
            endTime = Optional.ofNullable(timeActionEntity.getEndTime()).orElse(DateUtils.getCurrentTimestamp());
            totalTime = (endTime.getTime()-startTime.getTime());
            timeActionEntity.setTotalTime(totalTime);
        }
        timeActionEntity.setType(TimeActionTypeConstant.PAUSE);
        timeActionEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
        timeRepository.save(timeActionEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData list(TimeListReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
//        String sql = "select B.title,A.start_time,A.end_time,A.total_time,B.concentration_degree as degree from time_action A, to_dos B,duty_info C where C.row_id=B.duty_id and A.to_do_id=B.row_id and A.date_no=:dateNo order by A.start_time asc";
        String sql = FileUtil.getContent(SQL_NAME_CONSTANT.TIME_LIST);
        List<TimeListDto> times = QueryUtil.applyNativeListWithIct(sql, entityManager, (query, req1) ->
        {
            query.setParameter("userId",userId);
            query.setParameter("dateNo",req.getDateNo());
        }, TimeListDto.class);
        String countSql = FileUtil.getContent(SQL_NAME_CONSTANT.TIME_LIST_TOTAL);
        List<TimeCountDto> countBeans = QueryUtil.applyNativeListWithIct(countSql, entityManager, (query, req1) ->
        {
            query.setParameter("dateNo",req.getDateNo());
            query.setParameter("userId",userId);
        }, TimeCountDto.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(times);
        if(!CollectionUtils.isEmpty(countBeans)){
            responseData.setExtra(countBeans.get(0));
        }
        return responseData;
    }

    public ResponseData degrees(TimeListReq req) throws ValidException {
        Integer dateNo = req.getDateNo();
        String userId = TomatoUserLoginListener.getUserId();
        String sql = "select A.color,D.start_time,D.end_time,C.concentration_degree as degree from duty_info A," +
                "duty_section B,to_dos C,time_action D " +
                "where D.del_flag=0 and C.del_flag=0 and A.row_id=B.duty_id and C.row_id=B.content_id and B.content_type=0 " +
                "and D.to_do_id=C.row_id and (DATE_FORMAT('%Y-%m-%d',D.start_time)=:dateNo or DATE_FORMAT('%Y-%m-%d',D.end_time)=:dateNo) " +
                "and C.user_id=:userId";
        List<DegreeDto> degreeDtos = QueryUtil.applyNativeListWithIct(sql, entityManager, (query, req1) -> {
            query.setParameter("userId",userId);
            query.setParameter("dateNo",dateNo);
        }, DegreeDto.class);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(degreeDtos);
        return responseData;
    }

    public ResponseData update(TimeCreateTodoReq req) throws ValidException {
        String timeId = req.getTimeId();
        Integer degree = Optional.ofNullable(req.getDegree()).orElseThrow(()->new ValidException(MessageCode.TIME_DEGREE_NOT_NULL));
        TimeActionEntity timeActionEntity = JpaUtil.check(timeId,timeRepository,true);
        String todoId = timeActionEntity.getToDoId();
        if(StringUtils.isEmpty(todoId)){
            String title = Optional.ofNullable(req.getTitle()).orElseThrow(()->new ValidException(MessageCode.TIME_TITLE_NOT_NULL));
            String dutyId = Optional.ofNullable(req.getDutyId()).orElseThrow(()->new ValidException(MessageCode.TIME_DUTY_NOT_NULL));
            TodoCreateReq todoCreateReq = new TodoCreateReq();
            if(!StringUtils.isEmpty(dutyId)) {
                todoCreateReq.setParentId(dutyId);
                todoCreateReq.setParentType(ContentTypeConstant.DUTY.getValue());
            }
            todoCreateReq.setTitle(title);
            todoCreateReq.setConcentrationDegree(degree);
            todoCreateReq.setTimeFlag(true);
            ResponseData<String> responseData = todoService.create(todoCreateReq);
            todoId = responseData.getData();
            if(!StringUtils.isEmpty(todoId)) {
                UpdateDegreeReq updateDegreeReq = new UpdateDegreeReq();
                updateDegreeReq.setDegree(degree);
                updateDegreeReq.setTodoId(todoId);
                todoService.updateDegree(updateDegreeReq);
            }
            timeActionEntity.setToDoId(todoId);
            String actionId = timeActionEntity.getActionId();
            if(!StringUtils.isEmpty(actionId)){
                timeRepository.updateByActionId(actionId,todoId);
            }
            timeRepository.save(timeActionEntity);
        }else{
//        如果待办已经存在则更新专注度
            UpdateDegreeReq updateDegreeReq = new UpdateDegreeReq();
            updateDegreeReq.setDegree(degree);
            updateDegreeReq.setTodoId(todoId);
            todoService.updateDegree(updateDegreeReq);
        }
        return ResponseData.getDefaultSuccessResponse(req);
    }
    
    public List<TimePushDto> getEndTime() throws ValidException {
        String sql = "select getui_id as client_id,A.row_id as timeId,A.config_time from time_action A,user_push_relation B " +
                "where A.last_flag=1 and A.expire_time>now() and .A.push_flag=0 " +
                "and A.user_id = B.user_id group by B.getui_id";
        List<TimePushDto> timePushDtos = QueryUtil.applyNativeListWithIct(sql,entityManager,TimePushDto.class);
        return timePushDtos;
    }

    public int updatePushFlag(List<String> timeIds){
        if(CollectionUtils.isEmpty(timeIds)){
            return 0;
        }
        return timeRepository.updatePushFlag(timeIds);
    }

    public ResponseData total(TimeTotalReq req) throws ValidException {
        String sql = FileUtil.getContent(SQL_NAME_CONSTANT.TODO_TIME_TOTAL);
        String todoId = req.getTodoId();
        Long total = QueryUtil.applyFirst(sql, entityManager, (query, req1) -> query.setParameter("todoId",todoId), new LongTransformer());
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(total==null?0:total);
        return responseData;
    }
}
