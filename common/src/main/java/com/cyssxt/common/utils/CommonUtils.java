package com.cyssxt.common.utils;

import com.cyssxt.common.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 520cloud on 2017-09-05.
 */
public class CommonUtils {

    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
    public static final int DEFAULT_RANDOM_LENGTH = 5;

    /**
     * 判断对象为空
     *
     * @param obj
     *            对象名
     * @return 是否为空
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj)
    {
        if (obj == null)
        {
            return true;
        }
        if ((obj instanceof List))
        {
            return ((List) obj).size() == 0;
        }
        if ((obj instanceof String))
        {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    /**
     * 判断对象不为空
     *
     * @param obj
     *            对象名
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj)
    {
        return !isEmpty(obj);
    }

    public static String generatorKey(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 对字符串md5加密(小写+字母)
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMsgCode(){
        String key = CommonUtils.generatorKey();
        return key.substring(0,6);
    }

    public static String getMsgCodeOfInt(){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<6;i++){
            stringBuffer.append(random());
        }
        return stringBuffer.toString();
    }

    private final static char[] SEEDS = new char[]{'0','1','2','3','4','5','6','7','8','9'};
    public static char random(){
        Random random = new Random();
        return SEEDS[random.nextInt(10)];
    }

    private static int no = 0;
    private static String lastDataStr = null;

    public static String fillStr(int no){
        return fillStr(no, DEFAULT_RANDOM_LENGTH);
    }

    public static String fillStr(int no,int randomLength){
        StringBuffer stringBuffer = new StringBuffer();
        int length = (randomLength-(no+"").length());
        while(length<0){
            randomLength++;
            length = (randomLength-(no+"").length());
        }
        for(int i=0;i<length;i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(no);
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
//        System.out.println(CommonUtils.getMD5("123456"));
        String mac = "ac:bc:32:7c:d5:77";
        String key = "jdj2ujfj2i5jniu";
        String token = getMD5(key+mac);
        String href ="http://192.168.10.254:2060/wifidog/auth?token="+token+"&mac="+mac;
        System.out.println(href);
    }

    private final static Map<String,Integer> cacheOrder = new HashMap<>();

    public static String getOrderNo() {
        Random random = new Random();
        String orderNo = String.format("%s%s",DateUtils.getDataFormatString(new Date(),DateUtils.YYYYMMDDHHMMSS),random.nextInt(10000));
        Integer old = cacheOrder.get(orderNo);
        if(old!=null){
            orderNo = getOrderNo();
        }
        cacheOrder.put(orderNo,1);
        return orderNo;
    }

    public static boolean isTrue(Boolean flag){
        return flag!=null && flag;
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        logger.info("clientIp", ip);
        return ip;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
