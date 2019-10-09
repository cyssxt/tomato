package com.cyssxt.tomato.controller;

import com.cyssxt.common.annotation.ApiVersion;
import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.controller.BaseController;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.service.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.Valid;

@ApiVersion
@RestController
@RequestMapping(value="/{version}/user/",method = RequestMethod.POST)
public class UserController extends BaseController {

    @Resource
    UserService userService;

    /**
     * 临时用户
     * @param req
     * @return
     */
    @RequestMapping(value="createGuest")
    public ResponseData createGuest(@RequestBody BaseReq req){
        return userService.createGuest(req);
    }

    /**
     * 登陆
     * @param req
     * @param result
     * @return
     */
    @RequestMapping(value="login")
    public ResponseData login(@RequestBody @Valid LoginReq req, BindingResult result) throws ValidException {
        return userService.login(req);
    }

    @RequestMapping(value="sms")
    public ResponseData sms(@RequestBody @Valid SmsReq req, BindingResult result) throws ValidException {
        return userService.sms(req);
    }

    /**
     * 查询用户信息
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @RequestMapping(value="info")
    @Authorization
    public ResponseData info(@RequestBody @Valid BaseReq req, BindingResult result) throws ValidException {
        return userService.info(req);
    }

    /**
     * 更新用户信息
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @RequestMapping(value="update")
    @Authorization
    public ResponseData update(@RequestBody @Valid UserInfoUpdateReq req, BindingResult result) throws ValidException {
        return userService.update(req);
    }

    /**
     * 更新用户banner
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @RequestMapping(value="updateBanner")
    @Authorization
    public ResponseData updateBanner(@RequestBody @Valid BannerUpdateReq req, BindingResult result) throws ValidException {
        return userService.updateBanner(req);
    }


    /**
     * 更新用户设置
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @RequestMapping(value="updateSetting")
    @Authorization
    public ResponseData updateSetting(@RequestBody @Valid UserSettingReq req, BindingResult result) throws ValidException {
        return userService.updateSetting(req);
    }

    /**
     * 查询设置内容
     * @param req
     * @param result
     * @return
     * @throws ValidException
     */
    @RequestMapping(value="setting")
    @Authorization
    public ResponseData setting(@RequestBody @Valid BaseReq req, BindingResult result) throws ValidException {
        return userService.setting(req);
    }

    @RequestMapping(value="setgoal")
    @Authorization
    public ResponseData setgoal(@RequestBody @Valid SetGoalReq req, BindingResult result) throws ValidException {
        return userService.setgoal(req);
    }
}
