package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.service.ActionService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

public abstract class ActionController<T extends ActionCreateReq,V extends ActionPageReq> extends BaseController {
    public abstract ActionService getActionService();

    @Authorization
    @RequestMapping(value="del")
    public ResponseData del(@RequestBody @Valid DelReq req, BindingResult result) throws ValidException {
        return getActionService().del(req);
    }

    @Authorization
    @RequestMapping(value="copy")
    public ResponseData copy(@RequestBody @Valid MoveReq req, BindingResult result) throws ValidException {
        return getActionService().copy(req);
    }

//    @Authorization
//    @RequestMapping(value="batchCopy")
//    public ResponseData batchCopy(@RequestBody @Valid BatchCopyReq req, BindingResult result) throws ValidException {
//        return getActionService().batchCopy(req);
//    }

    @Authorization
    @RequestMapping(value="list")
    public ResponseData list(@RequestBody @Valid V req, BindingResult result) throws ValidException {
        return getActionService().list(req);
    }

    @Authorization
    @RequestMapping(value="cancel")
    public ResponseData cancel(@RequestBody @Valid CancelReq req, BindingResult result) throws ValidException {
        return getActionService().cancel(req);
    }

    @Authorization
    @RequestMapping(value="create")
    public ResponseData create(@RequestBody @Valid T req, BindingResult result) throws ValidException {
        return getActionService().create(req);
    }

    @Authorization
    @RequestMapping(value="update")
    public ResponseData update(@RequestBody @Valid T req, BindingResult result) throws ValidException {
        return getActionService().update(req);
    }

    @Authorization
    @RequestMapping(value="move")
    public ResponseData move(@RequestBody @Valid MoveReq req, BindingResult result) throws ValidException {
        return getActionService().move(req);
    }

//    @Authorization
//    @RequestMapping(value="bathcMove")
//    public ResponseData bathcMove(@RequestBody @Valid BatchMoveReq req, BindingResult result) throws ValidException {
//        return getActionService().bathcMove(req);
//    }

    @Authorization
    @RequestMapping(value="finish")
    public ResponseData finish(@RequestBody @Valid FinishReq req, BindingResult result) throws ValidException {
        return getActionService().finish(req);
    }
//
//    @Authorization
//    @RequestMapping(value="batchDel")
//    public ResponseData batchDel(@RequestBody @Valid BatchDelReq req, BindingResult result) throws ValidException {
//        return getActionService().batchDel(req);
//    }

    @Authorization
    @RequestMapping(value="start")
    public ResponseData start(@RequestBody @Valid FinishReq req, BindingResult result) throws ValidException {
        return getActionService().start(req);
    }

    @Authorization
    @RequestMapping(value="end")
    public ResponseData end(@RequestBody @Valid FinishReq req, BindingResult result) throws ValidException {
        return getActionService().end(req);
    }

    /**
     * 详情
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @Authorization
    @RequestMapping(value="info")
    public ResponseData info(@RequestBody @Valid InfoReq req, BindingResult result) throws ValidException {
        return getActionService().info(req);
    }

//    @Authorization
//    @RequestMapping(value="items")
//    public ResponseData items(@RequestBody @Valid V req, BindingResult result) throws ValidException {
//        return getActionService().items(req);
//    }
}
