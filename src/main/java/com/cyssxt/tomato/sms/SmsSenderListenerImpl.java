package com.cyssxt.tomato.sms;

import com.cyssxt.smsspringbootstarter.core.SmsSendListener;
import com.cyssxt.smsspringbootstarter.request.SendReq;
import org.springframework.stereotype.Component;

@Component
public class SmsSenderListenerImpl implements SmsSendListener {
    @Override
    public void success(SendReq req) {

    }

    @Override
    public void fail(SendReq req) {

    }
}
