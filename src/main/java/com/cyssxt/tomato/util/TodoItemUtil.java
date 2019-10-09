package com.cyssxt.tomato.util;

import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.constant.RepeatUnitConstant;
import com.cyssxt.tomato.entity.ToDosEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.sql.Timestamp;
import java.util.*;

public class TodoItemUtil {
    private final static Logger logger = LoggerFactory.getLogger(TodoItemUtil.class);
    public static final int CREATE_DAY_INFO_LENGTH = 7;

    public interface TimeFilter{
        boolean filter(Calendar calendar);
        boolean end(Calendar temp);
    }
    public interface CreateItem{
        void create(Calendar calendar,String todoId,String userId,int sequence);
    }

    /**
     * 计算总共的items
     * @param start
     * @param endTime
     * @param timeFilter
     * @param todoId
     * @param userId
     * @param createItem
     * @param repeatUnitConstant
     * @param repeatUnitValue
     */
    static void calTodoItems(Calendar start , Timestamp endTime, TimeFilter timeFilter, String todoId, String userId,CreateItem createItem,RepeatUnitConstant repeatUnitConstant,int repeatUnitValue){
        int sequence = 1;
        int num = 0;
        while(start.getTimeInMillis()<endTime.getTime() && num < CREATE_DAY_INFO_LENGTH){
            num++;
            boolean flag = timeFilter.filter(start);
            if(!flag){
                createItem.create(start,todoId,userId,sequence);
            }

            Calendar temp = Calendar.getInstance();
            temp.setTime(start.getTime());
            boolean end = timeFilter.end(temp);
            if(end){
                if(repeatUnitConstant.compare(RepeatUnitConstant.WEEK.getValue())){
                    start = nextDay(start,Calendar.WEEK_OF_YEAR,repeatUnitValue);
                    start.set(Calendar.DAY_OF_WEEK,1);
                }else if(repeatUnitConstant.compare(RepeatUnitConstant.MONTH.getValue())){
                    start = nextDay(start,Calendar.MONTH,repeatUnitValue);
                    start.set(Calendar.DAY_OF_MONTH,1);
                }
                sequence+=1;
            }else{
                if(RepeatUnitConstant.DAY==repeatUnitConstant) {
                    start = nextDay(start, repeatUnitConstant.getTimeUnit(), repeatUnitValue);
                }else{
                    start = nextDay(start,Calendar.DATE,1);
                }
                if(repeatUnitConstant.compare(RepeatUnitConstant.DAY.getValue())){
                    sequence+=1;
                }
            }
        }
    }
    public static Calendar nextDay(Calendar calendar,int timeUnit,int unitValue){
        calendar.add(timeUnit,unitValue);
        return calendar;
    }

    public static Calendar nextExecTime(Timestamp startTime,RepeatUnitConstant repeatUnitConstant,int repeatUnitValue){
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        Calendar nextExec = start;
//        long time = now.getTimeInMillis()-start.getTimeInMillis();
//        long day = time/1000/60/60/24;
//        int unitDay = 1;
//        if(){
//
//        }
        while(start.getTimeInMillis()<now.getTimeInMillis()){
            start.add(repeatUnitConstant.getTimeUnit(),repeatUnitValue);
            if(repeatUnitConstant.getFirstDay()!=null) {
                start.set(repeatUnitConstant.getFirstDay(), 1);
            }
            nextExec = start;
        }
        return nextExec;
    }

    public static void userDefined(ToDosEntity toDosEntity, String userId,CreateItem createItem){
        Boolean repeatFlag = toDosEntity.getRepeatFlag();
        String todoId = toDosEntity.getRowId();
        repeatFlag = Optional.ofNullable(repeatFlag).orElse(false);
        int repeatUnitValue = toDosEntity.getRepeatUnitValue();
        if(repeatFlag){
            Byte unit = toDosEntity.getRepeatUnit();
            Timestamp endTime = toDosEntity.getRepeatEndTime();
            Timestamp startTime = toDosEntity.getExecuteTime();
            String repeatExecDay = toDosEntity.getRepeatExecDay();
            if (RepeatUnitConstant.DAY.compare(unit)) {
                Calendar start = nextExecTime(startTime,RepeatUnitConstant.DAY,repeatUnitValue);
                calTodoItems(start, endTime, new TimeFilter() {
                    @Override
                    public boolean filter(Calendar calendar) {
                        return false;
                    }

                    @Override
                    public boolean end(Calendar temp) {
                        return false;
                    }
                }, todoId, userId, createItem,RepeatUnitConstant.DAY,repeatUnitValue);
            }else if(RepeatUnitConstant.WEEK.compare(unit)){
                if(StringUtils.isEmpty(repeatExecDay)){
                    return;
                }
                filter(endTime, startTime, repeatExecDay,repeatUnitValue,Calendar.DAY_OF_WEEK,todoId,userId,createItem,RepeatUnitConstant.WEEK);
            }else if(RepeatUnitConstant.MONTH.compare(unit)){
                if(StringUtils.isEmpty(repeatExecDay)){
                    return;
                }
                filter( endTime, startTime, repeatExecDay,repeatUnitValue,Calendar.DAY_OF_MONTH,todoId,userId,createItem,RepeatUnitConstant.MONTH);
            }
        }else{
            Calendar calendar = Calendar.getInstance();
            Timestamp start = toDosEntity.getExecuteTime();
            calendar.setTime(start);
            createItem.create(calendar,todoId,userId,1);
        }

    }

    private static void filter(Timestamp endTime, Timestamp startTime, String repeatExecDay,int repeatUnitValue,int timeUnit,String todoId,String userId,CreateItem createItem,RepeatUnitConstant repeatUnitConstant) {
        String[] repeatExecDays = repeatExecDay.split(CharConstant.COMMA);
        List<String> items = Arrays.asList(repeatExecDays);
        Calendar start = nextExecTime(startTime,repeatUnitConstant,repeatUnitValue);
        calTodoItems(start, endTime, new TimeFilter() {
            @Override
            public boolean filter(Calendar calendar) {
                int dayType = calendar.get(timeUnit);
                return !items.contains(dayType+"");
            }

            @Override
            public boolean end(Calendar temp) {
                boolean flag = false;
                if(RepeatUnitConstant.WEEK.compare(repeatUnitConstant.getValue())){
                    int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
                    flag =  dayOfWeek==Calendar.SATURDAY;
                }else if(RepeatUnitConstant.MONTH.compare(repeatUnitConstant.getValue())){
                    int oldMonth = temp.get(Calendar.MONTH);
                    temp.add(Calendar.DAY_OF_MONTH,1);
                    int newMonth = temp.get(Calendar.MONTH);
                    flag = oldMonth!=newMonth;
                }
                return flag;
            }
        }, todoId, userId, createItem,repeatUnitConstant,repeatUnitValue);
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,1);
        calendar.set(Calendar.DATE,31);
        System.out.println(calendar.getTime());
//        ToDosEntity todo = new ToDosEntity();
//        Calendar start = Calendar.getInstance();
//        start.add(Calendar.HOUR,1);
//        start.set(Calendar.MONTH,0);
//        System.out.println(start.getTime());
//        todo.setExecuteTime(new Timestamp(start.getTimeInMillis()));
//        todo.setRepeatFlag(true);
//        todo.setRepeatUnitValue(1);
//        todo.setRepeatUnit(RepeatUnitConstant.WEEK.getValue());
//        Calendar endTime = Calendar.getInstance();
//        endTime.add(Calendar.DATE,100);
//        todo.setRepeatEndTime(new Timestamp(endTime.getTimeInMillis()));
//        todo.setRepeatExecDay("1,2,31");
//        userDefined(todo, "123", (calendar, todoId, userId,sequence) -> {
//            String dateStr = DateUtils.getDataFormatString(calendar.getTime(),DateUtils.YYYYMMDD);
//            logger.info("dateStr={},sequence={}",dateStr,sequence);
//        });
    }
}
