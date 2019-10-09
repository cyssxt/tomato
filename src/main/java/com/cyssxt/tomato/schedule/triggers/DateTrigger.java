package com.cyssxt.tomato.schedule.triggers;

import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.tomato.constant.RepeatTypeConstant;
import com.cyssxt.tomato.constant.RepeatUnitConstant;
import com.cyssxt.tomato.entity.ToDosEntity;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class DateTrigger implements Trigger {

    private ToDosEntity toDosEntity;
    private Calendar lastExecTime;
    private int hour;
    private int minute;

    public DateTrigger(ToDosEntity toDosEntity) {
        this.toDosEntity = toDosEntity;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDosEntity.getExecuteTime());
        this.lastExecTime = calendar;
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        return nextExecuteTime(date);
    }

    /**
     * 下次执行时间计算
     * @param lastDate
     * @return
     */
    Date nextExecuteTime(Date lastDate){
        Boolean repeatFlag = toDosEntity.getRepeatFlag();
        Byte repeatType = toDosEntity.getRepeatType();
        if(null==repeatFlag||!repeatFlag || RepeatTypeConstant.NONE.compare(repeatType)){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDate);
        Calendar nextExecDate = null;
        if(RepeatTypeConstant.EVERY_DAY.compare(repeatType)){
            nextExecDate = dayNextExecTime(calendar);
        }else if(RepeatTypeConstant.EVERY_WEEK.compare(repeatType)){
            nextExecDate = weekNextExecTime(calendar);
        }else if(RepeatTypeConstant.EVERY_WORK_DAY.compare(repeatType)){
            nextExecDate = workdayNextExecTime(calendar);
        }else if(RepeatTypeConstant.EVERY_MONTH.compare(repeatType)){
            nextExecDate = monthNextExecTime(calendar);
        }else if (RepeatTypeConstant.USER_DEFINED.compare(repeatType)){
            nextExecDate = userDefined(calendar);
        }
        nextExecDate.set(Calendar.HOUR_OF_DAY,hour);
        nextExecDate.set(Calendar.MINUTE,minute);
        toDosEntity.getRepeatUnit();
        toDosEntity.getRepeatEndTime();
        toDosEntity.getRepeatExecDay();
        return nextExecDate.getTime();
    }

    /**
     * 每天下次执行时间
     * @param calendar
     * @return
     */
    public Calendar dayNextExecTime(Calendar calendar){
        calendar.add(Calendar.DATE,1);
//        calendar.set(Calendar.HOUR_OF_DAY,hour);
//        calendar.set(Calendar.MINUTE,minute);
        return calendar;
    }

    /**
     * 每周下次执行时间
     * @param calendar
     * @return
     */
    public Calendar weekNextExecTime(Calendar calendar){
        calendar.add(Calendar.WEEK_OF_MONTH,7);
        calendar.set(Calendar.DAY_OF_WEEK,1);
//        calendar.set(Calendar.HOUR_OF_DAY,hour);
//        calendar.set(Calendar.MINUTE,minute);
        return calendar;
    }

    /**
     * 每周工作日下次执行时间
     * @param calendar
     * @return
     */
    public Calendar workdayNextExecTime(Calendar calendar){
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        if(weekday>=Calendar.FRIDAY){
            calendar.add(Calendar.WEEK_OF_MONTH,1);
            calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        }
        return calendar;
    }
    /**
     * 每月3号下次执行时间
     * @param calendar
     * @return
     */
    public Calendar monthNextExecTime(Calendar calendar){
        calendar.add(Calendar.MONTH,1);
        calendar.set(Calendar.DAY_OF_MONTH,3);
        return calendar;
    }

    public Calendar userDefined(Calendar calendar){
        Byte unit = toDosEntity.getRepeatUnit();
        int unitValue = toDosEntity.getRepeatUnitValue();
        String execDay = toDosEntity.getRepeatExecDay();
        String[] execDays = execDay.split(CharConstant.SEMICOLON);
        for(String days:execDays) {
            int unitType = Calendar.DATE;
            Integer execDayType = null;
            if (RepeatUnitConstant.DAY.compare(unit)) {
                unitType = Calendar.DATE;
            } else if (RepeatUnitConstant.WEEK.compare(unit)) {
                unitType = Calendar.WEEK_OF_MONTH;
                execDayType = Calendar.DAY_OF_WEEK;
            } else if (RepeatUnitConstant.MONTH.compare(unit)) {
                unitType = Calendar.MONTH;
                execDayType = Calendar.DAY_OF_MONTH;
            }
            calendar.add(unitType, unitValue);
            //execDay=-1表示每月最后一天
            if (execDayType != null) {
                if ("-1" == execDay) {//月末最后一天
                    calendar.add(Calendar.MONTH, 2);
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    calendar.set(execDayType, Integer.valueOf(execDay));
                }
            }
        }
        return calendar;
    }
}
