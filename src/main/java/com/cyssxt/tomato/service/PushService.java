package com.cyssxt.tomato.service;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.response.ResponseData;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.constant.PushInfoContentConstant;
import com.cyssxt.tomato.controller.request.PushRegisterReq;
import com.cyssxt.tomato.dao.PushItemRepository;
import com.cyssxt.tomato.dao.PushRespository;
import com.cyssxt.tomato.dao.TimeService;
import com.cyssxt.tomato.dao.UserPushRepository;
import com.cyssxt.tomato.dto.CustomMsgDto;
import com.cyssxt.tomato.dto.PushClientDataDto;
import com.cyssxt.tomato.dto.TimePushDto;
import com.cyssxt.tomato.dto.WillStartDto;
import com.cyssxt.tomato.entity.PushInfoEntity;
import com.cyssxt.tomato.entity.PushItemEntity;
import com.cyssxt.tomato.entity.UserPushRelationEntity;
import com.cyssxt.tomato.listener.TomatoUserLoginListener;
import com.cyssxt.tomato.util.AppPushUtil;
import com.gexin.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PushService {

    private final static Logger logger = LoggerFactory.getLogger(PushService.class);

    @Resource
    public PushRespository pushRespository;

    @Resource
    PushItemRepository pushItemRepository;

    @Resource
    TimeService timeService;

    @Resource
    public UserPushRepository userPushRepository;

    @Resource
    TodoService todoService;

    @Resource
    AppPushUtil appPushUtil;

    @Resource
    CommonService commonService;

    /**
     * 1.	计时模块：
     * 场景：倒计时结束
     * 文案：太棒了，您专注了XX分钟！
     * 点击通知操作：打开p073(计时结束)
     * 2.	每天通知：
     * 场景：每天早上9：00推送
     * 文案：新的一天开始啦，做点什么吧！
     * 点击通知操作：打开app
     * 3.	待办截至提醒：
     * 场景：待办开启提醒（个人中设置过提醒方式），按照设置的时间提醒
     * 文案：还有xx（设置的时间），您有一条待办即将开始！
     * 点击通知操作：打开 p050（预测-今天）
     * 4.	截至时间：
     * 场景：每天早上10：00
     * 文案：有n条待办将在今天截至。
     * 点击通知操作：打开p052（今天列表）
     * 5.	超过3天没有打开APP
     * 文案：您已经3天没有来过了，计划已经荒草丛生。
     * 点击通知操作：打开app
     */

    public ResponseData register(PushRegisterReq req) throws ValidException {
        String userId = TomatoUserLoginListener.getUserId();
        Byte clientType = req.getClientType();
        ResponseData responseData = ResponseData.getDefaultSuccessResponse(req);
        String getuiId = req.getGetuiId();
        UserPushRelationEntity userPushRelationEntity = userPushRepository.findFirstByUserIdAndClientType(userId,clientType);
        if(userPushRelationEntity==null){
            userPushRelationEntity = new UserPushRelationEntity();
            userPushRelationEntity.setUserId(userId);
            userPushRelationEntity.setGetuiId(getuiId);
            userPushRelationEntity.setClientType(clientType);
        }else{
            userPushRelationEntity.setGetuiId(getuiId);
        }
        if(userPushRelationEntity.getDelFlag()!=null && userPushRelationEntity.getDelFlag()){
            userPushRelationEntity.setUpdateTime(DateUtils.getCurrentTimestamp());
            userPushRelationEntity.setDelFlag(false);
            userPushRelationEntity.setGetuiId(getuiId);
        }
        userPushRepository.save(userPushRelationEntity);
        return responseData;
    }

    public ResponseData test(String clientId,Byte type) throws Exception {
        Object result = null;
        if(PushInfoContentConstant.TIME_END.compare(type)){
            result =pushTime(clientId);
        }else if(PushInfoContentConstant.EVERY_DAY.compare(type)){
            result =pushEveryDay(clientId);
        }else if(PushInfoContentConstant.OVER_THREE_DAY.compare(type)){
            result =pushThreeDay(clientId);
        }else if(PushInfoContentConstant.WILL_END.compare(type)){
            result = pushEnd(clientId);
        }else if(PushInfoContentConstant.TODO_WILL_START.compare(type)){
            result = pushWillStart(clientId);
        }
        ResponseData responseData = ResponseData.getDefaultSuccessResponse();
        responseData.setData(result);
        return responseData;
    }
    public List<Map<String,String>> pushWillStart() throws ValidException {
        return pushWillStart(null);
    }
    public List<Map<String,String>> pushWillStart(String clientId) throws ValidException {
        List<WillStartDto> willStartDtos = StringUtils.isEmpty(clientId)?todoService.getWillStartDto():getWillStartTest(clientId);
        if(CollectionUtils.isEmpty(willStartDtos)){
            logger.info("pushWillStart is null ");
            return null;
        }

        List<String> rowIds = new ArrayList<>();
        List<PushClientDataDto> pushClientDataDtos = new ArrayList<>();
        for(WillStartDto willStartDto:willStartDtos){
            String tmp = willStartDto.getClientId();
            String rowId = willStartDto.getRowId();
            if(StringUtils.isEmpty(tmp)||StringUtils.isEmpty(rowId)){
                continue;
            }
            rowIds.add(rowId);
            List<String> clientIds = new ArrayList<>();
            clientIds.add(tmp);
            PushClientDataDto pushClientDataDto = new PushClientDataDto(clientIds,willStartDto.getValue(),willStartDto);
            pushClientDataDtos.add(pushClientDataDto);
        }
        todoService.updatePushFlag(rowIds);
        return pushByData(PushInfoContentConstant.TODO_WILL_START,pushClientDataDtos);
    }

    public List<WillStartDto> getWillStartTest(String clientId){
        List<WillStartDto> list = new ArrayList<>();
        WillStartDto willStartDto = new WillStartDto();
        willStartDto.setClientId(clientId);
        willStartDto.setExecuteTime(DateUtils.getCurrentTimestamp());
        willStartDto.setRemindMinute(60L);
        willStartDto.setTodoId("1");
        willStartDto.setRowId("1");
        list.add(willStartDto);
        return list;
    }
    /**
     * 测试
     * @return
     * @throws ValidException
     */
    public List<Map<String,String>> pushTime() throws ValidException {
        return pushTime(null);
    }

    /**
     * 计时结束推送
     * @throws ValidException
     */
    public List<Map<String,String>> pushTime(String testClientId) throws ValidException {
        logger.info("pushTime");
        List<TimePushDto> timePushDtos;
        if(!StringUtils.isEmpty(testClientId)){
            timePushDtos = new ArrayList<>();
            TimePushDto timePushDto = new TimePushDto();
            timePushDto.setClientId(testClientId);
            timePushDto.setConfigTime(25*60*1000L);
            timePushDto.setTimeId("test");
            timePushDtos.add(timePushDto);
        }else{
            timePushDtos = timeService.getEndTime();
        }
        if (CollectionUtils.isEmpty(timePushDtos)) {
            return null;
        }
        List<Map<String,String>> result = null;
        List<PushClientDataDto> pushClientDataDtos = new ArrayList<>();
        logger.info("pushClientDataDtos={}",pushClientDataDtos.size());
        List<String> timeIds = new ArrayList<>();
        for(TimePushDto timePushDto:timePushDtos){
            String clientId = timePushDto.getClientId();
            if (StringUtils.isEmpty(clientId)) {
                continue;
            }
            List<String> clientIds = new ArrayList<>();
            clientIds.add(clientId);
            long minutes = timePushDto.getConfigTime()/1000/60;
            pushClientDataDtos.add(new PushClientDataDto(clientIds,minutes+"",timePushDto));
            result = pushByData(PushInfoContentConstant.TIME_END,pushClientDataDtos);
            timeIds.add(timePushDto.getTimeId());
        }
        timeService.updatePushFlag(timeIds);
        return result;
    }

    public List<String> getAllUseClients(){
        List<String> allclientIds = userPushRepository.getAllClientId();
        return allclientIds;
    }

    /**
     * 超过3天没有登陆推送
     */
    public List<Map<String,String>> pushThreeDay(){
        return pushThreeDay(null);
    }

    public List<Map<String,String>> pushThreeDay(String clientId){
        List<String> clientIds;
        if(!StringUtils.isEmpty(clientId)){
            clientIds = getClientIds(clientId);
        }else{
            clientIds = commonService.getClientIds(-3);
        }
        return pushByClientIds(PushInfoContentConstant.OVER_THREE_DAY,clientIds);
    }

    /**
     * 每天推送
     */
    public List<Map<String,String>> pushEveryDay(){
        return pushEveryDay(null);
    }
    public List<Map<String,String>> pushEveryDay(String clientId){
        List<String> clientIds = !StringUtils.isEmpty(clientId)?getClientIds(clientId):getAllUseClients();
        return pushByClientIds(PushInfoContentConstant.EVERY_DAY,clientIds);
    }

    /**
     * 通知待办截止
     * @throws ValidException
     */
    public List<Map<String,String>> pushEnd() throws ValidException {
        return pushEnd(null);
    }
    public List<Map<String,String>> pushEnd(String clientId) throws ValidException {
        Integer dateNo = DateUtils.getCurrentDataFormatInteger();
        Map<Object,List<String>> result = StringUtils.isEmpty(clientId)?todoService.getAllEnd(dateNo):getMapClients(clientId);
        Iterator keys = result.keySet().iterator();
        List<PushClientDataDto> clientDtos = new ArrayList<>();
        while (keys.hasNext()){
            Object key = keys.next();
            List<String> clients = result.get(key);
            clientDtos.add(new PushClientDataDto(clients,key+""));
        }
        return pushByData(PushInfoContentConstant.WILL_END,clientDtos);
    }

    Map<Object,List<String>> getMapClients(String clientIds){
        Map<Object,List<String>> map = new HashMap<>();
        List<String> dataList = new ArrayList<>();
        dataList.add(clientIds);
        map.put(112,dataList);
        return map;
    }

    public List<String> getClientIds(String clientId){
        List<String> clientIds = new ArrayList<>();
        clientIds.add(clientId);
        return clientIds;
    }

    /**
     * 通过ClientId推送
     * @param pushInfoContentConstant
     * @param clientIds
     */
    public List<Map<String,String>> pushByClientIds(PushInfoContentConstant pushInfoContentConstant,List<String> clientIds){
        if(CollectionUtils.isEmpty(clientIds)){
            return null;
        }
        List<PushClientDataDto> dataList = new ArrayList<>();
        dataList.add(new PushClientDataDto(clientIds,""));
        return pushByData(pushInfoContentConstant,dataList);
    }

    public List<Map<String,String>> pushByData(PushInfoContentConstant pushInfoContentConstant,List<PushClientDataDto> dataList) {
        Byte value = pushInfoContentConstant.getValue();
        String text = pushInfoContentConstant.getText();
        String title = pushInfoContentConstant.getTitle();
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        List<Map<String,String>> map = new ArrayList<>();
        for(PushClientDataDto pushClientDataDto:dataList) {
            String data = pushClientDataDto.getValue()+"";
            Object object = pushClientDataDto.getData();
            text = text.replace("{value}",data);
            logger.info("push info={},text={}", value, text);
            List<String> clients = pushClientDataDto.getClientIds();
            CustomMsgDto customMsgDto = new CustomMsgDto<>(pushInfoContentConstant.getValue(),object);
            Map<String, String> response = appPushUtil.push(title,text,customMsgDto , clients);
            logger.info("push,ret={}", response);
            insertPushInfo(title,text,customMsgDto,response,clients);
            map.add(response);
        }
        return map;
    }

    void insertPushInfo(String title,String text,CustomMsgDto customMsgDto,Map<String,String> response,List<String> clientIds){
        PushInfoEntity pushInfoEntity = new PushInfoEntity();
        pushInfoEntity.setResponse(JSON.toJSONString(response));
        pushInfoEntity.setContent(JSON.toJSONString(customMsgDto));
        pushInfoEntity.setPushTitle(title);
        pushInfoEntity.setPushText(text);
        String taskId = response.get("taskId");
        pushInfoEntity.setTaskId(taskId);
        String pushId = pushInfoEntity.getRowId();
        for(String clientId:clientIds){
            PushItemEntity pushItemEntity = new PushItemEntity();
            pushItemEntity.setClientId(clientId);
            pushItemEntity.setPushId(pushId);
            pushItemRepository.save(pushItemEntity);
        }
        pushRespository.save(pushInfoEntity);

    }


}
