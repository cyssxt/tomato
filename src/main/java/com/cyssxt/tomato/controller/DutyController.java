package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.DutyCreateReq;
import com.cyssxt.tomato.controller.request.DutyPageReq;
import com.cyssxt.tomato.controller.request.DutySaveReq;
import com.cyssxt.tomato.service.ActionService;
import com.cyssxt.tomato.service.DutyService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.Valid;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/duty/",method = RequestMethod.POST)
public class DutyController extends ActionController<DutyCreateReq, DutyPageReq> {

    @Resource
    DutyService dutyService;

//    @RequestMapping(value="create")
//    public ResponseData create(@RequestBody @Valid DutyCreateReq req, BindingResult result) throws ValidException {
//        return dutyService.create(req);
//    }

    @Override
    public ActionService getActionService() {
        return dutyService;
    }

    @RequestMapping(value="colors")
    public ResponseData colors(@RequestBody BaseReq req) throws ValidException {
        return dutyService.colors(req);
    }

    @Authorization
    @RequestMapping(value="save")
    public ResponseData save(@RequestBody @Valid DutySaveReq req, BindingResult result) throws ValidException {
        return dutyService.save(req);
    }
}
