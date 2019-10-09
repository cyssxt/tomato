package com.cyssxt.tomato.util;

import com.alibaba.fastjson.JSON;
import com.cyssxt.tomato.dto.CustomMsgDto;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppPushUtil {

    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    @Value("${getui.appId}")
    private String appId = "t3sitNlGgW7bwi02PZlcs4";
    @Value("${getui.appKey}")
    private String appKey = "sNyyOZAgJd7yPbOKif0bA6";
    @Value("${getui.masterSecret}")
    private String masterSecret = "diZIHLyCys6CwzwHscJMx1";
    static String url = "http://sdk.open.api.igexin.com/apiex.htm";

    public NotificationTemplate getNotifyTemplate(String title,String body, CustomMsgDto customMsgDto) {
        NotificationTemplate template = new NotificationTemplate();
        APNPayload.DictionaryAlertMsg dictionaryAlertMsg = new APNPayload.DictionaryAlertMsg();
        if(!StringUtils.isEmpty(title)){
            dictionaryAlertMsg.setTitle(title);
        }
        dictionaryAlertMsg.setBody(body);
        APNPayload apnPayload = new APNPayload();
        apnPayload.addCustomMsg("params", customMsgDto);
        apnPayload.setAlertMsg(dictionaryAlertMsg);
        template.setAPNInfo(apnPayload);
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionType(2);
        template.setChannelLevel(4);
        Style0 style0 = new Style0();
        style0.setTitle(title);
        style0.setText("test");
        template.setStyle(style0);
        return template;
    }

    //    public static void main(String[] args) throws Exception {
//        List list = new ArrayList();
////        list.add("137b6e191d216be5de595b7d6eb6cf0e");
//        list.add("c6a6129520c04897ab7dedcac91b8df7");
//        System.out.println(push("这个是标题",new CustomMsgDto(),list));
//    }
    public Map push(String title,String body, CustomMsgDto customMsgDto, List<String> clientIds) {

        IGtPush push = new IGtPush(url, appKey, masterSecret);

        NotificationTemplate template = getNotifyTemplate(title,body, customMsgDto);
        ListMessage message = new ListMessage();
        message.setData(template);
        message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600);
        Target target = null;
        String contentId = push.getContentId(message);
        List<Target> targets = new ArrayList<>();
        for (String clientId : clientIds) {
            target = new Target();
            target.setAppId(appId);
            // 设置cid
            target.setClientId(clientId);
            targets.add(target);
        }
        IPushResult ret = push.pushMessageToList(contentId, targets);
        return ret.getResponse();
    }
}


