package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.CommonBatchDelReq;
import com.cyssxt.tomato.controller.request.CommonBatchMoveReq;
import com.cyssxt.tomato.service.CommonService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@ApiVersion
@RequestMapping(value="/{version}/common/",method = RequestMethod.POST)
public class CommonController extends BaseController {

    @Resource
    CommonService commonService;

    @Authorization
    @RequestMapping(value="del")
    public ResponseData batchDel(@RequestBody @Valid CommonBatchDelReq req, BindingResult result) throws ValidException {
        return commonService.batchDel(req);
    }

    @Authorization
    @RequestMapping(value="move")
    public ResponseData batchMove(@RequestBody @Valid CommonBatchMoveReq req, BindingResult result) throws ValidException {
        return commonService.batchMove(req);
    }

    @Authorization
    @RequestMapping(value="copy")
    public ResponseData batchCopy(@RequestBody @Valid CommonBatchMoveReq req, BindingResult result) throws ValidException {
        return commonService.batchCopy(req);
    }
    @Authorization
    @RequestMapping(value="active")
    public ResponseData active(@RequestBody BaseReq req) throws ValidException {
        return commonService.active(req);
    }
}
