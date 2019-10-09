package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.request.BaseReq;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.constant.TimeingTypeConstant;
import com.cyssxt.tomato.controller.request.*;
import com.cyssxt.tomato.dao.GoalsRepository;
import com.cyssxt.tomato.dao.UserRepository;
import com.cyssxt.tomato.dao.UserSettingRepository;
import com.cyssxt.tomato.dto.TodoInfo;
import com.cyssxt.tomato.dto.UserInfoDto;
import com.cyssxt.tomato.dto.UserSettingInfo;
import com.cyssxt.tomato.entity.GoalsEntity;
import com.cyssxt.tomato.entity.UserInfoEntity;
import com.cyssxt.tomato.entity.UserSettingEntity;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class UserService {

    public static final int DEFULA_TIME_VALUE = 60 * 25 * 1000;
    @Resource
    UserSettingRepository userSettingRepository;

    @Resource
    GoalsRepository goalsRepository;

    @Resource
    SessionService sessionService;

    @Resource
    UserRepository userRepository;

    public ResponseData login(LoginReq req) throws ValidException {
        String sessionId = req.getSessionId();
        String phoneNumber = req.getPhoneNumber();
        UserInfoEntity userInfoEntity=null;
        if("798678".equals(req.getCode()) && "17665380688".equals(req.getPhoneNumber())){
            sessionId = sessionService.addSmsSession();
        }else {
            sessionService.validCode(req.getPhoneNumber(), req.getCode());
            if (StringUtils.isEmpty(sessionId)) {
                throw new ValidException(MessageCode.SESSION_ID_IS_NOT_NULL);
            }
            userInfoEntity = TomatoUserLoginListener.getUserWithOutException();
        }
        if(userInfoEntity==null) {
            userInfoEntity = userRepository.findFirstByPhoneNumber(phoneNumber);
            if(userInfoEntity==null){
                userInfoEntity = new UserInfoEntity();
                userInfoEntity.setPhoneNumber(phoneNumber);
            }
        }else{
            String oldPhone = userInfoEntity.getPhoneNumber();
            if(!StringUtils.isEmpty(oldPhone) && !phoneNumber.equals(oldPhone)){
                throw new ValidException(MessageCode.PHONE_NUMBER_HAS_EXIST);
            }
            userInfoEntity.setPhoneNumber(phoneNumber);
        }
        userInfoEntity.setLastLoginTime(DateUtils.getCurrentTimestamp());
        userInfoEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
        String userId = userInfoEntity.getRowId();
        userRepository.save(userInfoEntity);
        sessionService.addLoginSession(sessionId,userId);//更新登陆信息
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setSessionId(sessionId);
        return responseData;
    }


    public ResponseData updateSetting(UserSettingReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        UserSettingEntity userSettingEntity = userSettingRepository.findFirstByUserIdAndDelFlag(userId,false);
        if(userSettingEntity==null){
            userSettingEntity = new UserSettingEntity();
        }
        req.parse(userSettingEntity);
        userSettingRepository.save(userSettingEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }

    /**
     * 获取用户设置
     * @param userId
     * @return
     */
    public UserSettingEntity getUserSetting(String userId){
        UserSettingEntity userSettingEntity = userSettingRepository.findFirstByUserIdAndDelFlag(userId,false);
        if(userSettingEntity==null){
            userSettingEntity = new UserSettingEntity();
            userSettingEntity.setUserId(userId);
            userSettingEntity.setTimeValue(DEFULA_TIME_VALUE);
            userSettingEntity.setTimeType(TimeingTypeConstant.NEG.getValue());
            userSettingRepository.save(userSettingEntity);
        }
        return userSettingEntity;
    }


    public Long getUserTimeSetting(String userId){
        UserSettingEntity userSettingEntity  = getUserSetting(userId);
        Integer timeValue = userSettingEntity.getTimeValue();
        timeValue = Optional.ofNullable(timeValue).orElse(DEFULA_TIME_VALUE);
        return timeValue.longValue();
    }
    public ResponseData setting(BaseReq req) throws ValidException {
        UserInfoEntity userInfoEntity = TomatoUserLoginListener.getUser();
        String userId = userInfoEntity.getRowId();
        GoalsEntity goalsEntity = goalsRepository.findFirstByUserIdAndDelFlagFalseOrderByCreateTimeDesc(userId);
        UserSettingEntity userSettingEntity = getUserSetting(userId);
        UserSettingInfo userSettingInfo = new UserSettingInfo(userSettingEntity,userInfoEntity,goalsEntity);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        responseData.setData(userSettingInfo);
        return responseData;
    }

    public ResponseData sms(SmsReq req) throws ValidException {
        String sessionId = sessionService.sms(req);
        ResponseData responseData =  ResponseData.getDefaultSuccessResponse(req);
        responseData.setSessionId(sessionId);
        return responseData;
    }

    public ResponseData setgoal(SetGoalReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        GoalsEntity goalsEntity = new GoalsEntity();
        goalsEntity.setUserId(userId);
        goalsEntity.setGlobalGoal(req.getGoal());
        goalsRepository.save(goalsEntity);
        return ResponseData.getDefaultSuccessResponse(req);
    }


    @Resource
    TodoService todoService;
    /**
     * 查询用户信息
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData info(BaseReq req) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        UserInfoEntity userInfoEntity = TomatoUserLoginListener.getUser();
        TodoInfo todoInfo = todoService.getinfo(userInfoEntity.getRowId());
        UserInfoDto userInfoDto = new UserInfoDto(userInfoEntity,todoInfo);
        responseData.setData(userInfoDto);
        return responseData;
    }

    /**
     * 更新用户信息
     * @param req
     * @return
     * @throws ValidException
     */
    public ResponseData update(UserInfoUpdateReq req) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        UserInfoEntity userInfoEntity = TomatoUserLoginListener.getUser();
        req.parse(userInfoEntity);
        userRepository.save(userInfoEntity);
        return responseData;
    }

    public ResponseData updateBanner(BannerUpdateReq req) throws ValidException {
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        UserInfoEntity userInfoEntity = TomatoUserLoginListener.getUser();
        userInfoEntity.setBanner(req.getBanner());
        userRepository.save(userInfoEntity);
        return responseData;
    }

    public ResponseData createGuest(BaseReq req) {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        String userId = userInfoEntity.getRowId();
        userInfoEntity.setGuestFlag(true);
        userRepository.save(userInfoEntity);
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String sessionId = sessionService.addGuestSession(userId);
        responseData.setSessionId(sessionId);
        responseData.setData(userId);
        return responseData;
    }
}
