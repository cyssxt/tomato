package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dao.TimeService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/time/",method = RequestMethod.POST)
public class TimeController extends BaseController {

    @Resource
    TimeService timeService;

    @Authorization
    @RequestMapping(value="start")
    public ResponseData start(@RequestBody @Valid TimeStartReq req, BindingResult result) throws ValidException {
        return timeService.start(req);
    }

    @Authorization
    @RequestMapping(value="stop")
    public ResponseData stop(@RequestBody @Valid TimeStopReq req, BindingResult result) throws ValidException {
        return timeService.stop(req);
    }

    @Authorization
    @RequestMapping(value="pause")
    public ResponseData pause(@RequestBody @Valid TimePauseReq req, BindingResult result) throws ValidException {
        return timeService.pause(req);
    }

    @Authorization
    @RequestMapping(value="update")
    public ResponseData update(@RequestBody @Valid TimeCreateTodoReq req, BindingResult result) throws ValidException {
        return timeService.update(req);
    }

    @Authorization
    @RequestMapping(value="list")
    public ResponseData list(@RequestBody @Valid TimeListReq req, BindingResult result) throws ValidException, IOException {
        return timeService.list(req);
    }

    @Authorization
    @RequestMapping(value="degrees")
    public ResponseData degrees(@RequestBody @Valid TimeListReq req, BindingResult result) throws ValidException {
        return timeService.degrees(req);
    }

    @Authorization
    @RequestMapping(value="total")
    public ResponseData total(@RequestBody @Valid TimeTotalReq req, BindingResult result) throws ValidException {
        return timeService.total(req);
    }

}
