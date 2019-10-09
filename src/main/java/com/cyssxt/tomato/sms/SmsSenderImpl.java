package com.cyssxt.tomato.sms;

import com.cyssxt.smsspringbootstarter.core.SmsSender;
import com.cyssxt.smsspringbootstarter.request.SendReq;
import com.cyssxt.tomato.netease.SmsSend;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SmsSenderImpl implements SmsSender {

    @Resource
    SmsSend smsSend;

    @Override
    public boolean send(SendReq req) {
        Boolean flag = false;
        try {
            flag = smsSend.send(req.getPhoneNumber(),req.getMsgCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
