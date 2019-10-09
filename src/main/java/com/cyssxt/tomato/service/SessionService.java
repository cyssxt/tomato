package com.cyssxt.tomato.service;

import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.common.constant.SessionTypeConstant;
import com.cyssxt.common.dao.RedisDao;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.utils.CommonUtils;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.smsspringbootstarter.request.SendReq;
import com.cyssxt.smsspringbootstarter.service.SmsService;
import com.cyssxt.tomato.controller.request.SmsReq;
import com.cyssxt.tomato.dao.SessionRepository;
import com.cyssxt.tomato.dao.UserRepository;
import com.cyssxt.tomato.dto.UserSessionDto;
import com.cyssxt.tomato.entity.SessionsEntity;
import com.cyssxt.tomato.entity.UserInfoEntity;
import com.cyssxt.tomato.errors.MessageCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    @Resource
    RedisDao redisDao;

    @Resource
    UserRepository userRepository;

    @Resource
    SessionRepository sessionRepository;

    @Resource
    SmsService smsService;

    public String addSmsSession(){
        return addSession(SessionTypeConstant.SMS.getValue());
    }

    public String addGuestSession(String userId){
        return addSession(userId,SessionTypeConstant.GUEST);
    }

    public String addSession(Byte type){
        return addSession(null,SessionTypeConstant.SMS);
    }

    /**
     * 更新用户绘画
     * @param sessionId
     * @param userId
     * @param sessionTypeConstant
     * @throws ValidException
     */
    public void updateSession(String sessionId,String userId,SessionTypeConstant sessionTypeConstant) throws ValidException {
        SessionsEntity sessionsEntity = JpaUtil.check(sessionId,sessionRepository,true);
        sessionsEntity.setLoginType(sessionTypeConstant.getValue());
        sessionsEntity.setUserId(userId);
        sessionRepository.save(sessionsEntity);
    }

    public UserInfoEntity getUser(String sessionId) throws ValidException {
        String userId = getUserId(sessionId);
        if(!StringUtils.isEmpty(userId)){
            return JpaUtil.check(userId,userRepository,false);
        }
        return null;
    }

    /**
     * 通过数据库获取sessionId
     * @param sessionId
     * @return
     * @throws ValidException
     */
    public SessionsEntity getUserIdByDb(String sessionId){
        Optional<SessionsEntity> optional = sessionRepository.findById(sessionId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public String addLoginSession(String sessionId,String userId) throws ValidException {
        updateSession(sessionId,userId,SessionTypeConstant.LOGIN);
        redisDao.stringSet(sessionId,renderValue(userId, SessionTypeConstant.LOGIN.getValue()),30, TimeUnit.DAYS);
        return sessionId;
    }

    /**
     * 增加会话
     * @param userId
     * @param sessionTypeConstant
     * @return
     */
    public String addSession(String userId,SessionTypeConstant sessionTypeConstant){
        SessionsEntity sessionsEntity = new SessionsEntity();
        sessionsEntity.setLoginType(sessionTypeConstant.getValue());
        sessionsEntity.setUserId(userId);
        sessionRepository.save(sessionsEntity);
        String sessionId = sessionsEntity.getRowId();
        redisDao.stringSet(sessionId,renderValue(userId,sessionTypeConstant.getValue()),30,TimeUnit.DAYS);
        return sessionId;
    }

    /**
     * 获取userId
     * @param sessionId
     * @return
     * @throws ValidException
     */
    public String getUserId(String sessionId) throws ValidException {
        if(StringUtils.isEmpty(sessionId)){
            throw new ValidException(MessageCode.SESSION_ID_IS_NOT_NULL);
        }
        String cacheValue = redisDao.getStringValue(sessionId);
        String userId=null;
        if(cacheValue==null){
            SessionsEntity sessionsEntity = getUserIdByDb(sessionId);
            if(sessionsEntity!=null){
                userId = sessionsEntity.getUserId();
            }
        }else{
            UserSessionDto userSessionDto = new UserSessionDto(sessionId,cacheValue);
            userId = userSessionDto.getUserId();
        }
        return userId;
    }

    public String renderValue(String userId,Byte type){
        return String.format("%s%s%s",userId, CharConstant.UNDERLINE,type+"");
    }


    public String sms(SmsReq req) throws ValidException {
        String sessionId = addSmsSession();
        String phoneNumber = req.getPhoneNumber();
        smsService.sendSms(phoneNumber);
        return sessionId;
    }

    public void validCode(String phoneNumber, String code) throws ValidException {
        if(!smsService.validCode(phoneNumber,code)){
            throw new ValidException(MessageCode.CODE_ERROR);
        }
    }
}
