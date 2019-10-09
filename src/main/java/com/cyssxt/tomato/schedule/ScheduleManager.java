package com.cyssxt.tomato.schedule;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.tomato.service.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScheduleManager {

    public static final int PUSH_SLEEP_TIME = 2000;

    private final static Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

    @Resource
    SyncTodoSchedule syncTodoSchedule;

    public void init(){
        syncTodoSchedule.init();
    }
}
