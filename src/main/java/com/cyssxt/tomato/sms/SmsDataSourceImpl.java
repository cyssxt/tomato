package com.cyssxt.tomato.sms;

import com.cyssxt.common.dao.RedisDao;
import com.cyssxt.smsspringbootstarter.dao.AbstractSmsDataSource;
import com.cyssxt.smsspringbootstarter.request.SendReq;
import com.cyssxt.smsspringbootstarter.service.SmsService;
import com.cyssxt.tomato.util.TimeUtil;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class SmsDataSourceImpl extends AbstractSmsDataSource {

    @Resource
    RedisDao redisDao;

    @Override
    public String getValue(String key) {
        return redisDao.getStringValue(key);
    }

    @Override
    public SendReq pop(String key) {
        return redisDao.spop(key);
    }

    @Override
    public boolean push(String key,SendReq req) {
        redisDao.sadd(key,req);
        return false;
    }


    @Override
    public boolean cache(String key, String msgCode,int unitValue,TimeUnit timeUnit) {
        //验证有效期
        redisDao.stringSetWithExpireTime(key,msgCode,unitValue, timeUnit);
        return true;
    }


    @Override
    public boolean clear(String key) {
        redisDao.delKey(key);
        return false;
    }

    @Override
    public void repeatSet(String repeatKey, String value, int time, TimeUnit timeUint) {
        redisDao.stringSetWithExpireTime(repeatKey,value,time,timeUint);
    }

    @Override
    public void onDel(String key,String repeatKey, String msgCode) {
        super.onDel(key,repeatKey, msgCode);
        redisDao.delKey(key);
    }
}
