package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.PageReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.MonthLogReq;
import com.cyssxt.tomato.controller.request.UserCreateReq;
import com.cyssxt.tomato.controller.request.UserLogPageReq;
import com.cyssxt.tomato.service.ActionService;
import com.cyssxt.tomato.service.UserLogService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/userlog/")
public class UserLogController extends ActionController<UserCreateReq, UserLogPageReq> {
    @Resource
    UserLogService userLogService;
    @Override
    public ActionService getActionService() {
        return userLogService;
    }


    @Authorization
    @RequestMapping(value="months")
    public ResponseData months(@RequestBody PageReq req, BindingResult result) throws ValidException {
        return userLogService.months(req);
    }

    @Authorization
    @RequestMapping(value="logByMonth")
    public ResponseData logByMonth(@RequestBody MonthLogReq req, BindingResult result) throws ValidException {
        return userLogService.logsByMonth(req);
    }
}
