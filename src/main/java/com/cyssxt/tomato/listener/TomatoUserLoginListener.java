package com.cyssxt.tomato.listener;

import com.cyssxt.common.annotation.Authorization;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.listener.UserLoginListener;
import com.cyssxt.tomato.entity.UserInfoEntity;
import com.cyssxt.tomato.errors.MessageCode;
import com.cyssxt.tomato.service.SessionService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Optional;


@Component
public class TomatoUserLoginListener extends UserLoginListener {

    private static ThreadLocal<UserInfoEntity> threadLocal = new ThreadLocal<>();

    @Resource
    SessionService sessionService;

    public static String getUserId() throws ValidException {
        return getUser().getRowId();
    }

    public static UserInfoEntity getUser() throws ValidException {
        return Optional.ofNullable(threadLocal.get()).orElseThrow(()->new ValidException(MessageCode.USER_SHOULD_LOGIN));
    }
    public static UserInfoEntity getUserWithOutException(){
        return threadLocal.get();
    }
    public static String getUserIdWithoutException() {
        UserInfoEntity userInfoEntity = threadLocal.get();
        if(userInfoEntity==null){
            return null;
        }
        return userInfoEntity.getRowId();
    }

    @Override
    public void cacheUserInfo(String sessionId) throws ValidException {
        if(StringUtils.isEmpty(sessionId)){
            return;
        }
        UserInfoEntity userInfoEntity = sessionService.getUser(sessionId);
        threadLocal.set(userInfoEntity);
    }

    /**
     * 登陆校验
     * @param authorization
     * @return
     * @throws ValidException
     */
    @Override
    public boolean login(Authorization authorization) throws ValidException {
        UserInfoEntity userInfoEntity = threadLocal.get();
        if(null!=userInfoEntity){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkSessionId(String sessionId) {
        return true;
    }
}
