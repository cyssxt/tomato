package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.constant.ContentTypeConstant;
import com.cyssxt.tomato.controller.request.CommonBatchDelReq;
import com.cyssxt.tomato.controller.request.CommonBatchMoveReq;
import com.cyssxt.tomato.controller.request.DelReq;
import com.cyssxt.tomato.controller.request.MoveReq;
import com.cyssxt.tomato.dao.ActiveRepository;
import com.cyssxt.tomato.dao.UserPushRepository;
import com.cyssxt.tomato.dto.BatchContentItem;
import com.cyssxt.tomato.entity.ActiveLogEntity;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Service
public class CommonService {

    @Resource
    DutyService dutyService;

    @Resource
    ProjectService projectService;

    @Resource
    TodoService todoService;

    @Resource
    ActiveRepository activeRepository;

    @Resource
    TagService tagService;


    public ResponseData batchDel(CommonBatchDelReq req) throws ValidException {
        List<BatchContentItem> items = req.getItems();
        for(BatchContentItem item:items){
            Byte contentType = item.getContentType();
            String contentId = item.getContentId();
            getService(contentType).del(new DelReq(contentId));
        }
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ActionService getService(Byte contentType){
        ActionService actionService = null;
        if(ContentTypeConstant.TODO.compare(contentType)){
            actionService = todoService;
        }else if(ContentTypeConstant.PROJECT.compare(contentType)){
            actionService = projectService;
        }else if(ContentTypeConstant.DUTY.compare(contentType)){
            actionService = dutyService;
        }else if(ContentTypeConstant.TAG.compare(contentType)){
            actionService = tagService;
        }
        return actionService;
    }

    public ResponseData batchMove(CommonBatchMoveReq req) throws ValidException {
        List<BatchContentItem> items = req.getItems();
        String parentId = req.getParentId();
        Byte parentType = req.getParentType();
        for(BatchContentItem item:items){
            Byte contentType = item.getContentType();
            String contentId = item.getContentId();
            getService(contentType).move(new MoveReq(contentId,parentType,parentId));
        }
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData batchCopy(CommonBatchMoveReq req) throws ValidException {
        List<BatchContentItem> items = req.getItems();
        String parentId = req.getParentId();
        Byte parentType = req.getParentType();
        for(BatchContentItem item:items){
            Byte contentType = item.getContentType();
            String contentId = item.getContentId();
            getService(contentType).copy(new MoveReq(contentId,parentType,parentId));
        }
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public ResponseData active(BaseReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        ActiveLogEntity activeLogEntity = new ActiveLogEntity();
        activeLogEntity.setUserId(userId);
        activeLogEntity.setClientType(req.getClientType());
        activeRepository.save(activeLogEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    public List<String> getClientIds(Integer days){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,days);
        return activeRepository.getAllClientIdOverDays(new Timestamp(calendar.getTimeInMillis()));
    }
}
