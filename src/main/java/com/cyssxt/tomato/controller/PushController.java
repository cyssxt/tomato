package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.PushRegisterReq;
import com.cyssxt.tomato.service.PushService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/push/")
public class PushController extends BaseController {

    @Resource
    PushService pushService;

    @Authorization
    @RequestMapping(value="register")
    public ResponseData register(@RequestBody PushRegisterReq req) throws ValidException {
        return pushService.register(req);
    }

    @RequestMapping(value="test/{clientId}/{type}")
    public ResponseData test(@PathVariable("clientId") String clientId,@PathVariable("type")Byte type) throws Exception {
        return pushService.test(clientId,type);
    }
}
