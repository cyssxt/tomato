package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.FinishListReq;
import com.cyssxt.tomato.controller.request.TimeDistributeReq;
import com.cyssxt.tomato.dto.ConsumTimeReq;
import com.cyssxt.tomato.service.StatisticsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/statistics/")
public class StatisticsController extends BaseController {

    @Resource
    StatisticsService statisticsService;

    @Authorization
    @RequestMapping(value="timeDistribute")
    public ResponseData timeDistribute(@RequestBody TimeDistributeReq req) throws ValidException {
        return statisticsService.timeDistribute(req);
    }

    @Authorization
    @RequestMapping(value="conumTime")
    public ResponseData conumTime(@RequestBody ConsumTimeReq req, BindingResult result) throws ValidException {
        return statisticsService.conumTime(req);
    }

    @Authorization
    @RequestMapping(value="finishDegrees")
    public ResponseData finishDegrees(@RequestBody FinishListReq req, BindingResult result) throws ValidException {
        return statisticsService.finishDegrees(req);
    }
}
