package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.SearchReq;
import com.cyssxt.tomato.service.IndexService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@ApiVersion
@RequestMapping(value="/{version}/index/",method = RequestMethod.POST)
public class IndexController extends BaseController {

    @Resource
    IndexService indexService;

    @Authorization
    @RequestMapping(value="search")
    public ResponseData search(@RequestBody @Valid SearchReq req, BindingResult result) throws ValidException {
        return indexService.search(req);
    }

    @Authorization
    @RequestMapping(value="forecast")
    public ResponseData forecast(@RequestBody @Valid SearchReq req, BindingResult result) throws ValidException {
        return indexService.forecast(req);
    }

    @Authorization
    @RequestMapping(value="staticinfo")
    public ResponseData staticinfo(@RequestBody @Valid BaseReq req, BindingResult result) throws ValidException {
        return indexService.staticinfo(req);
    }

    @Authorization
    @RequestMapping(value = "parents")
    public ResponseData parents(@RequestBody SearchReq req) throws ValidException {
        return indexService.parents(req);
    }
}
