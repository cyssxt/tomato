package com.cyssxt.tomato.service;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.hibernate.transformer.IgnoreCaseResultTransformer;
import com.cyssxt.common.hibernate.transformer.KeyTransformer;
import com.cyssxt.common.hibernate.transformer.StringTransformer;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.constant.TaskStatusConstant;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dao.ProjectRepository;
import com.cyssxt.tomato.dao.ProjectSectionRepository;
import com.cyssxt.tomato.dao.TodosRepository;
import com.cyssxt.tomato.dto.*;
import com.cyssxt.tomato.entity.ProjectInfoEntity;
import com.cyssxt.tomato.entity.ToDosEntity;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.swing.text.AbstractDocument;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectService extends ActionService<ProjectInfoEntity,ProjectCreateReq, ProjectPageReq, ProjectDto, ProjectInfoDto>{

    @Resource
    ProjectRepository projectRepository;

    @Resource
    ProjectSectionRepository projectSectionRepository;

    @Resource
    TodosRepository todosRepository;

    @Resource
    TagService tagService;

    @Resource
    TodoService todoService;

    @Resource
    SectionService sectionService;

    @Override
    public BaseRepository getRepository() {
        return projectRepository;
    }

    @Override
    public Byte getContentType() {
        return ContentTypeConstant.PROJECT.getValue();
    }

    @Override
    public ProjectInfoEntity createEntity(ProjectCreateReq projectCreateReq) {
        return new ProjectInfoEntity();
    }

    @Override
    protected void beforeSave(ProjectInfoEntity projectInfoEntity,ProjectCreateReq req, String userId) throws ValidException {
        Timestamp timestamp = projectInfoEntity.getCreateTime();
        String dateNo = DateUtils.getDataFormatString(timestamp,DateUtils.YYYYMMDD);
        projectInfoEntity.setDateNo(Integer.valueOf(dateNo));
        projectInfoEntity.setStatus(TaskStatusConstant.WAIT.getValue());
        projectInfoEntity.setParentId(req.getDutyId());
        projectInfoEntity.setParentType(ContentTypeConstant.DUTY.getValue());
    }


    @Override
    protected void afterSave(ProjectInfoEntity projectInfoEntity, String userId) {
        Timestamp executeTime = projectInfoEntity.getExecuteTime();
        if(executeTime!=null) {
            String dateStr = DateUtils.getDataFormatString(executeTime, DateUtils.YYYYMMDD);
            //更新帐期
            projectInfoEntity.setDateNo(Integer.valueOf(dateStr));
        }
        Byte status = projectInfoEntity.getStatus();
        if(TaskStatusConstant.FINISH.compare(status)){
            projectRepository.finishTodo(projectInfoEntity.getRowId());
        }
    }

    @Override
    public String orderBy(ProjectPageReq req) {
        return "A.sort asc,A.create_time asc,A.row_id asc ";
    }

    @Override
    public void where(ProjectPageReq req, List<String> list) {
        super.where(req, list);
        list.add(" ifnull(A.status,0)!=2 ");
    }

    @Override
    protected Class getDtoClass() {
        return ProjectDto.class;
    }

    @Override
    public String getListSql(ProjectPageReq req) {
        return "select A.row_id,A.title,A.remark,A.status,A.sort,A.parent_id from project_info A" +
        " left join duty_info B on A.parent_id= B.row_id ";
    }

    @Override
    public ProjectInfoDto detail(InfoReq req) throws ValidException {
        String contentId = req.getContentId();
        ProjectInfoEntity projectInfoEntity = JpaUtil.check(contentId,projectRepository,true);
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        projectInfoEntity.parse(projectInfoDto);
        List<ProjectItemDto> sections = items(contentId);
        projectInfoDto.setSections(sections);
        TodoPageReq todoPageReq = new TodoPageReq();
        todoPageReq.setProjectId(contentId);
        return projectInfoDto;
    }

    //插入时更新关系表
    @Override
    public void onInsert(ProjectInfoEntity projectInfoEntity, ProjectCreateReq req) throws ValidException {
        String parentId = projectInfoEntity.getParentId();
        if(!StringUtils.isEmpty(parentId)) {
            sectionService.updateDutySection(projectInfoEntity.getRowId(), ContentTypeConstant.PROJECT.getValue(), parentId);
        }
    }

    public void updateItems(ProjectCreateReq req,String projectId) throws ValidException {
        List<String> sectionIds = new ArrayList<>();
        List<ProjectItem> list = req.getProjectItems();
        tagService.updateReTags(req.getTagIds(),projectId,ContentTypeConstant.PROJECT);
        if(!CollectionUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                ProjectItem item = list.get(i);
                String itemId = item.getItemId();
                String title = item.getTitle();
                String todoId = item.getTodoId();
                if (!StringUtils.isEmpty(todoId)) {
                    //创建一个章节
                    sectionService.createSection(sectionIds, itemId, title, projectId, i, todoId);
                    ToDosEntity toDosEntity = JpaUtil.check(todoId, todosRepository, true);
                    toDosEntity.setParentId(projectId);
                    toDosEntity.setParentType(ContentTypeConstant.PROJECT.getValue());
                    toDosEntity.setSort(i);
                    toDosEntity.setSmallId(itemId);
                    toDosEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
                    todosRepository.save(toDosEntity);
                } else {
                    if (!StringUtils.isEmpty(title)) {
                        sectionService.createSection(sectionIds, itemId, title, projectId, i, null);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(sectionIds)) {
                projectSectionRepository.delByProjectId(sectionIds,projectId);
            }
        }
    }
    //更新时更新关系表
    @Override
    public void onUpdate(ProjectInfoEntity projectInfoEntity, ProjectInfoEntity old,ProjectCreateReq req) throws ValidException {
        onMove(old.getParentId(),old.getParentType(),projectInfoEntity.getParentId(),projectInfoEntity.getParentType(),projectInfoEntity.getRowId());
        String projectId = projectInfoEntity.getRowId();
        updateItems(req,projectId);
    }

    @Override
    public void onMove(String oldVal, Byte oldType, String newVal, Byte newType, String contentId) throws ValidException {
        super.onMove(oldVal,oldType,newVal,newType,contentId);
        if(!ContentTypeConstant.DUTY.compare(newType) || (oldVal!=null && oldVal.equals(newVal))){
            return;
        }
        if(!StringUtils.isEmpty(oldVal)){
            sectionService.delDutySection(oldVal,contentId,getContentType());
        }
        if(!StringUtils.isEmpty(newVal)) {
            sectionService.updateDutySection(contentId, getContentType(), newVal);
        }
    }

    @Transactional
    public ResponseData save(ProjectCreateReq req) throws ValidException {
        String projectId = req.getContentId();
        List<ProjectItem> list = req.getProjectItems();
        ProjectInfoEntity projectInfoEntity;
        if (!StringUtils.isEmpty(projectId)) {
            projectInfoEntity = JpaUtil.check(projectId,projectRepository,true);
        }else{
            projectInfoEntity = new ProjectInfoEntity();
        }
        projectInfoEntity.setTitle(req.getTitle());
        projectInfoEntity.setRemark(req.getRemark());
        projectInfoEntity.setEndTime(req.getEndTime());
        projectInfoEntity.setExecuteTime(req.getExecuteTime());
        projectInfoEntity.setStatus(req.getStatus());
        List<String> sectionIds = new ArrayList<>();
        updateItems(req,projectId);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(projectId);
        return responseData;
    }
    
    private List<ProjectItemDto> items(String projectId) throws ValidException {
        String sql = "select A.row_id as itemId,B.row_id as todoId,B.status,COALESCE(B.title,A.title) as title,B.execute_time,B.end_time,if(V.action_count>0,true,false) as actionFlag,if(W.tag_count>\n" +
                "0,true,false) as tagFlag,B.repeat_flag from project_section A \n" +
                "left join (select count(T.row_id) action_count,T.to_do_id  from to_do_actions T where T.del_flag=0 group by T.to_do_id) V on V.to_do_id=A.to_do_id  \n" +
                "left join (select count(R.tag_id) as tag_count,R.content_id from re_tag R where R.del_flag=0 and R.content_type=0 group by R.content_id) W on W.content_id=A.to_do_id\n" +
                "left join to_dos B on A.to_do_id=B.row_id\n" +
                "where  A.project_id=:projectId AND B.STATUS!=2 and A.del_flag=0 and if(ifnull(A.to_do_id,'')='',1=1,B.del_flag=0)  order by A.sort asc";
        KeyTransformer keyTransformer = new IgnoreCaseResultTransformer(ProjectItemDto.class,"todoid");
        List<ProjectItemDto> items = QueryUtil.applyNativeList(sql, null,entityManager, (query, req) -> query.setParameter("projectId",projectId),keyTransformer);
        List<String> keys = keyTransformer.getKeys();
        if(!CollectionUtils.isEmpty(keys)) {
            List<TagDto> tags = tagService.tags(keys, ContentTypeConstant.TODO);
            Map<String, List<TagDto>> map = tags.stream().collect(Collectors.groupingBy(TagDto::getContentId));
            for (ProjectItemDto itemDto:items){
                String todoId = itemDto.getTodoId();
                List<TagDto> itemTags = map.get(todoId);
                if(itemTags==null){
                    itemTags = new ArrayList<>();
                    itemTags.add(TagService.NO_TAG);
                }
                itemDto.setTags(itemTags);
            }
        }
        return items;
    }

    public List<String> todoIds(String projectId) throws ValidException {
        String sql = "select A.to_do_id from project_section A" +
                " where A.project_id=:projectId and A.del_flag=0 and ifnull(A.to_do_id,'')!='' ";
        List<String> items = QueryUtil.applyNativeList(sql, null,entityManager, (query, req) -> query.setParameter("projectId",projectId),new StringTransformer());
        return items;
    }

    /**
     * 统计项目数量
     * @param start
     * @param end
     * @param userId
     * @return
     */
    public Long totals(Timestamp start, Timestamp end, String userId){
        return projectRepository.count(start,end,userId, TaskStatusConstant.FINISH.getValue());
    }

    @Override
    public void onFinish(FinishReq req, ProjectInfoEntity projectInfoEntity) throws ValidException {
        projectRepository.finishTodo(req.getContentId());
        todoService.stopRepeat(req.getContentId());
    }

    @Override
    public void onCopy(ProjectInfoEntity projectInfoEntity, String oldId, String parentId, Byte parentType) throws ValidException {
        super.onCopy(projectInfoEntity, oldId, parentId, parentType);
        String projectId = projectInfoEntity.getRowId();
        projectInfoEntity.setParentId(parentId);
        List<String> todoIds = todoIds(projectId);
        if(!StringUtils.isEmpty(parentId)) {
            sectionService.updateDutySection(projectId, ContentTypeConstant.PROJECT.getValue(), parentId);
        }
        for(String todoId:todoIds){
            todoService.copy(new MoveReq(todoId,parentType,projectId));
        }
    }

    public Map<String, List<ProjectDto>> items(List<String> parentIds) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        Map<String,List<ProjectDto>> projects = new HashMap<>();
        if(StringUtils.isEmpty(parentIds)){
            return projects;
        }
        String sql = getFullSqlOfWhere(new ProjectPageReq()," and A.parent_id in :parentIds ");
        List<ProjectDto> projectDtos = QueryUtil.applyNativeListWithIct(sql, entityManager, new QueryUtil.PageParameter<ProjectPageReq>() {

            @Override
            public void initParam(Query query, ProjectPageReq projectPageReq) throws ValidException {
                query.setParameter("userId",userId);
                query.setParameter("parentIds",parentIds);
            }
        }, ProjectDto.class);

        if(!CollectionUtils.isEmpty(projectDtos)){
            projects = projectDtos.stream().collect(Collectors.groupingBy(ProjectDto::getParentId));
        }
        return projects;
    }


//    @Override
//    public void onCopy(ProjectInfoEntity projectInfoEntity,String oldId) throws ValidException {
//        super.onCopy(projectInfoEntity,oldId);
//
//    }
}
