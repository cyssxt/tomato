package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.service.ActionService;
import com.cyssxt.tomato.service.TodoActionService;
import com.cyssxt.tomato.service.TodoService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiVersion
@RequestMapping(value="/{version}/todo/",method = RequestMethod.POST)
@RestController
public class TodoController extends ActionController<TodoCreateReq, TodoPageReq> {

    @Resource
    TodoService todoService;

    @Resource
    TodoActionService todoActionService;

    @Override
    public ActionService getActionService() {
        return todoService;
    }

    @Authorization
    @RequestMapping(value="updateActionStatus")
    public ResponseData updateActionStatus(@RequestBody UpdateActionStatusReq req){
        return todoService.updateActionStatus(req);
    }

    @Authorization
    @RequestMapping(value="updateDegree")
    public ResponseData updateDegree(@RequestBody UpdateDegreeReq req) throws ValidException {
        return todoService.updateDegree(req);
    }

    @Authorization
    @RequestMapping(value="inboxs")
    public ResponseData inboxs(@RequestBody @Valid InboxPageReq req, BindingResult result) throws ValidException {
        return todoService.inboxs(req);
    }

    @Authorization
    @RequestMapping(value="todays")
    public ResponseData todays(@RequestBody @Valid TodayPageReq req,BindingResult result) throws ValidException {
        return todoService.todays(req);
    }

    @Authorization
    @RequestMapping(value="time")
    public ResponseData time(@RequestBody @Valid TimeListReq req, BindingResult result) throws ValidException {
        return todoService.time(req);
    }

    @Authorization
    @RequestMapping(value="endTimes")
    public ResponseData endTimes(@RequestBody PageReq req) throws ValidException {
        return todoService.endTimes(req);
    }

    @Authorization
    @RequestMapping(value="updateFinishDegree")
    public ResponseData updateFinishDegree(@RequestBody @Valid FinishDegreeReq req,BindingResult result) throws ValidException {
        return todoService.updateFinishDegree(req);
    }

    /**
     * 待完成任务
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @Authorization
    @RequestMapping(value="waitTask")
    public ResponseData waitTask(@RequestBody @Valid TodoPageReq req,BindingResult result) throws ValidException {
        return todoService.waitTask(req);
    }

    @Authorization
    @RequestMapping(value="finishAction")
    public ResponseData finishAction(@RequestBody FinishReq req,BindingResult result) throws ValidException {
        return todoActionService.finish(req);
    }

    @Authorization
    @RequestMapping(value="cancelAction")
    public ResponseData cancelAction(@RequestBody FinishReq req,BindingResult result) throws ValidException {
        return todoActionService.cancel(req);
    }

    /**
     * 计划
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @Authorization
    @RequestMapping(value="plans")
    public ResponseData plans(@RequestBody TagFilterReq req,BindingResult result) throws ValidException {
        return todoService.plans(req);
    }

    @Authorization
    @RequestMapping(value="transfer")
    public ResponseData transfer(@RequestBody TransferReq req,BindingResult result) throws ValidException {
        return todoService.transfer(req);
    }

    /**
     * @param req
     * @return
     * @throws ValidException
     */
    @Authorization
    @RequestMapping(value="finishs")
    public ResponseData finishs(@RequestBody @Valid FinishPageReq req) throws ValidException {
        return todoService.finishs(req);
    }

    @Authorization
    @RequestMapping(value="stop")
    public ResponseData stop(@RequestBody @Valid TodoStopReq req,BindingResult result) throws ValidException {
        return todoService.stop(req);
    }

    @Authorization
    @RequestMapping(value="files")
    public ResponseData files(@RequestBody @Valid FilesPageReq req) throws ValidException {
        return todoService.files(req);
    }

    @Authorization
    @RequestMapping(value="fileTodos")
    public ResponseData fileTodos(@RequestBody FilesPageReq req) throws ValidException {
        return todoService.fileTodos(req);
    }

    @RequestMapping(value="willStart")
    public List willStart() throws ValidException {
        return todoService.getWillStartDto();
    }


}
