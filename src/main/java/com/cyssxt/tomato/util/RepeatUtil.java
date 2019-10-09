package com.cyssxt.tomato.util;

import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.tomato.constant.RepeatUnitConstant;
import org.bouncycastle.util.Times;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class RepeatUtil {

    public static final int MAX_CAL_NUM = 7;

    public abstract static class RepeatCallback{
        void exec(Calendar start,Timestamp endTime){
            Date now = new Date();
            long time = now.getTime();
            if(start.getTimeInMillis()<time || (endTime!=null && endTime.getTime()<start.getTimeInMillis())){
                return;
            }
            onItem(start);
        }
        public abstract void onItem(Calendar start);
    }

    public static long getDiffTimeOfNow(Calendar start){
        Calendar now = Calendar.getInstance();
        long time = now.getTimeInMillis()-start.getTimeInMillis();
        return time;
    }

    /**
     * 两个时间相差
     * @param start
     * @param current
     * @return
     */
    public static long getDiffTime(Calendar start,Calendar current){
        long time = current.getTimeInMillis()-start.getTimeInMillis();
        return time;
    }

    /**
     * 执行天数
     * @param start
     * @param execDays
     * @param timeUint
     * @param repeatCallback
     */
    public static int execDays(Calendar start,String execDays,int timeUint,RepeatCallback repeatCallback, Timestamp endTime,Integer num){
        if (!StringUtils.isEmpty(execDays)) {
            String[] days = execDays.split(CharConstant.COMMA);
            for (String day : days) {
                start.set(timeUint, Integer.valueOf(day));
                if(start.getTimeInMillis()<Calendar.getInstance().getTimeInMillis()){
                    continue;
                }
                num--;
                repeatCallback.exec(start,endTime);
            }
        }
        return num;
    }

    /**
     * 每周执行
     * @param start
     * @param current
     * @param repeatUnitValue
     * @param execDays
     * @param repeatCallback
     * @return
     */
    public static Calendar weekNextDay(Calendar start,Calendar current, int repeatUnitValue, String execDays, RepeatCallback repeatCallback, Timestamp endTime){
        long time = getDiffTime(start,current);
        Long week = time/1000/60/60/24/7;
        Long diffWeek = week/repeatUnitValue;
        int num=MAX_CAL_NUM;
        if(time>0){
            if(diffWeek>=1) {
                start.add(RepeatUnitConstant.WEEK.getTimeUnit(), diffWeek.intValue()*repeatUnitValue);
            }
        }else{
            num=execDays(start,execDays,Calendar.DAY_OF_WEEK,repeatCallback,endTime,num);
        }
        int totalNum = MAX_CAL_NUM;
        while(num>0 && totalNum>0) {
            totalNum--;
            num=execDays(start,execDays,Calendar.DAY_OF_WEEK,repeatCallback,endTime,num);
            start.add(RepeatUnitConstant.WEEK.getTimeUnit(), repeatUnitValue);
        }
        return start;
    }

    /**
     * 下一天
     * @param start
     * @param current
     * @param repeatUnitValue
     * @param repeatCallback
     * @return
     */
    public static Calendar nextDay(Calendar start,Calendar current,int repeatUnitValue,RepeatCallback repeatCallback,Timestamp endTime){
        long time = getDiffTime(start,current);
        Long day = time/1000/60/60/24;
        Long diffDay = day/repeatUnitValue-1;
        if(time>0){
            if(diffDay>=1){
                start.add(Calendar.DATE,diffDay.intValue()*repeatUnitValue);
            }
        }else{
            repeatCallback.exec(start,endTime);
        }
        int num = MAX_CAL_NUM;
        int totalNum = MAX_CAL_NUM;
        while(num>=0&&totalNum>=0) {
            totalNum--;
            repeatCallback.exec(start,endTime);
            start.add(RepeatUnitConstant.DAY.getTimeUnit(), repeatUnitValue);
            num--;
        }
        return start;
    }

    /**
     * 月份
     * @param start
     * @param current
     * @param repeatUnitValue
     * @param execDays
     * @param repeatCallback
     * @return
     */
    public static Calendar monthNextDay(Calendar start, Calendar current, int repeatUnitValue, String execDays, RepeatCallback repeatCallback, Timestamp endTime) {
        int num=MAX_CAL_NUM;
        int startYear = start.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH);
        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH);
        int diffYearMonth = (currentYear-startYear)*12;
        int diffMonth = currentMonth-startMonth+diffYearMonth;
        Integer diff = diffMonth/repeatUnitValue;
        int totalNum = MAX_CAL_NUM;
        if(diff>0) {
            if (diff >= 1) {
                start.add(RepeatUnitConstant.MONTH.getTimeUnit(), diff.intValue() * repeatUnitValue);
            }
        }else{
            num=execDays(start,execDays,Calendar.DAY_OF_MONTH,repeatCallback,endTime,num);
        }
        while(num>0 && totalNum>0) {
            totalNum--;
            start.add(RepeatUnitConstant.MONTH.getTimeUnit(), repeatUnitValue);
            num=execDays(start,execDays,Calendar.DAY_OF_MONTH,repeatCallback,endTime,num);
        }
        return start;
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,1);
        calendar.set(Calendar.MINUTE,10);
        calendar.set(Calendar.HOUR_OF_DAY,10);
//        nextDay(new Timestamp(calendar.getTimeInMillis()), 2, calendar1 -> System.out.println(calendar1.getTime()));
//        nextDay(calendar, Calendar.getInstance(), 2, start -> System.out.println(start.get()),null);
    }


}
