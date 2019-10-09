package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.common.utils.QueryUtil;
import com.cyssxt.tomato.constant.TaskStatusConstant;
import com.cyssxt.tomato.controller.request.FinishReq;
import com.cyssxt.tomato.controller.request.TodoItemReq;
import com.cyssxt.tomato.controller.request.UpdateActionStatusReq;
import com.cyssxt.tomato.dao.TodoActionRepository;
import com.cyssxt.tomato.dto.ActionDto;
import com.cyssxt.tomato.dto.TodoActionDto;
import com.cyssxt.tomato.entity.ToDoActionsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoActionService {

    private final static Logger logger = LoggerFactory.getLogger(TodoActionService.class);

    @Resource
    TodoActionRepository todoActionRepository;

    @Transactional
    public void updateTodoActions(List<TodoItemReq> actions, String todoId) throws ValidException {
        logger.info("updateTodoActions={}",todoId);
        if (!CollectionUtils.isEmpty(actions)){
            List<String> actionIds = new ArrayList<>();
            int length = actions.size();
            for(int i=0;i<length;i++){
                TodoItemReq item = actions.get(i);
                ToDoActionsEntity toDoActionsEntity = null;
                String actionId = item.getActionId();
                if(!StringUtils.isEmpty(actionId)){
                    toDoActionsEntity = JpaUtil.check(actionId,todoActionRepository,true);
                    toDoActionsEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
                }else{
                    toDoActionsEntity = new ToDoActionsEntity();
                }
                toDoActionsEntity.setContent(item.getContent());
                item.parse(toDoActionsEntity);
                toDoActionsEntity.setToDoId(todoId);
                toDoActionsEntity.setStatus(item.getStatus());
                toDoActionsEntity.setSort(i);
                toDoActionsEntity.setType(item.getItemType());
                todoActionRepository.save(toDoActionsEntity);
                actionIds.add(actionId);
            }
        }
    }

    /**
     * 更新动作状态
     * @param req
     * @return
     */
    public ResponseData updateActionStatus(UpdateActionStatusReq req) {
        List<String> contentIds = req.getContentIds();
        int length = 0;
        if(!CollectionUtils.isEmpty(contentIds)){
            length = todoActionRepository.updateStatus(contentIds,req.getStatus());
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(length);
        return responseData;
    }

    public ResponseData finish(FinishReq req) throws ValidException {
        String contentId = req.getContentId();
        ToDoActionsEntity toDoActionsEntity = JpaUtil.check(contentId,todoActionRepository,true);
        toDoActionsEntity.setStatus(TaskStatusConstant.FINISH.getValue());
        toDoActionsEntity.setFinishTime(DateUtils.getCurrentTimestamp());
        todoActionRepository.save(toDoActionsEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData cancel(FinishReq req) throws ValidException {
        String contentId = req.getContentId();
        ToDoActionsEntity toDoActionsEntity = JpaUtil.check(contentId,todoActionRepository,true);
        toDoActionsEntity.setStatus(TaskStatusConstant.WAIT.getValue());
        toDoActionsEntity.setFinishTime(null);
        todoActionRepository.save(toDoActionsEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public List<TodoActionDto> actions(List<String> todoIds) {
        return todoActionRepository.actions(todoIds);
    }

    @PersistenceContext
    EntityManager entityManager;

    public List<ActionDto> findByTodoId(String todoId) throws ValidException {
        String sql = "select row_id as actionId,content,status from to_do_actions where to_do_id=:todoId order by sort asc";
        List<ActionDto> actions = QueryUtil.applyNativeListWithIct(sql, entityManager, new QueryUtil.ReqParameter() {
            @Override
            public void initParam(Query query, BaseReq req) throws ValidException {
                query.setParameter("todoId",todoId);
            }
        }, ActionDto.class);
        return actions;
    }

    public void copyActions(String oldId,String newId){
        List<ToDoActionsEntity> actions = todoActionRepository.findByToDoIdAndDelFlagFalse(oldId);
        List<String> actionIds = new ArrayList<>();
        for(ToDoActionsEntity toDoActionsEntity:actions){
            String content = toDoActionsEntity.getContent();
            Integer sort = toDoActionsEntity.getSort();
            ToDoActionsEntity action =todoActionRepository.findFirstByToDoIdAndContentAndSort(newId,content,sort);
            String actionId = null;
            if(action==null){
                action = (ToDoActionsEntity)toDoActionsEntity.clone();
                String rowId = CommonUtils.generatorKey();
                action.setRowId(rowId);
                action.setSort(sort);
                action.setContent(content);
                action.setToDoId(newId);
            }
            actionId = action.getRowId();
            action.setUpdateTime(DateUtils.getCurrentTimestamp());
            todoActionRepository.save(action);
            actionIds.add(actionId);
        }
        if(!CollectionUtils.isEmpty(actionIds)) {
            todoActionRepository.delActions(newId, actionIds);
        }else{
            todoActionRepository.delActions(newId);
        }
    }


}
