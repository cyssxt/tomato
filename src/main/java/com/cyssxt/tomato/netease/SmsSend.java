package com.cyssxt.tomato.netease;

import com.cyssxt.tomato.dto.SmsResult;
import com.gexin.fastjson.JSON;
import com.google.zxing.client.result.SMSMMSResultParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SmsSend {
    private final static Logger logger = LoggerFactory.getLogger(SmsSend.class);

    //发送验证码的请求路径URL
    private static final String
            SERVER_URL = "https://api.netease.im/sms/sendcode.action";

    static Random random = new Random();
    //网易云信分配的账号，请替换你在管理后台应用下申请的Appkey
    @Value("${netease.sms.appKey:''}")
    private String APP_KEY ;
    //网易云信分配的密钥，请替换你在管理后台应用下申请的appSecret
    @Value("${netease.sms.appSecret:''}")
    private String APP_SECRET;
    //随机数
    private static final String NONCE = "fanqie666666";
    //短信模板ID
    @Value("${netease.sms.templateId:''}")
    private String TEMPLATEID;
    //手机号，接收者号码列表，JSONArray格式，限制接收者号码个数最多为100个

    public boolean send(String phoneNumber,String authCode) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(SERVER_URL);
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String NONCE = random.nextInt(1000000000)+"";
        /*
         * 参考计算CheckSum的java代码，在上述文档的参数列表中，有CheckSum的计算文档示例
         */
        String checkSum = SmsUtil.getCheckSum(APP_SECRET, NONCE, curTime);

        // 设置请求的header
        httpPost.addHeader("AppKey", APP_KEY);
        httpPost.addHeader("Nonce", NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的的参数，requestBody参数
        List<NameValuePair> nvps = new ArrayList<>();
        /*
         * 1.如果是模板短信，请注意参数mobile是有s的，详细参数配置请参考“发送模板短信文档”
         * 2.参数格式是jsonArray的格式，例如 "['13888888888','13666666666']"
         * 3.params是根据你模板里面有几个参数，那里面的参数也是jsonArray格式
         */
        nvps.add(new BasicNameValuePair("templateid", TEMPLATEID));
        nvps.add(new BasicNameValuePair("mobile", phoneNumber));
        nvps.add(new BasicNameValuePair("authCode", authCode));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        /*
         * 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
         * 2.具体的code有问题的可以参考官网的Code状态表
         */
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //{"code":200,"msg":"102","obj":"123456"}
        SmsResult smsResult = JSON.parseObject(result,SmsResult.class);
        logger.info("result={}",result);
        return Optional.ofNullable(smsResult).orElse(new SmsResult()).isSuccess();
    }
}