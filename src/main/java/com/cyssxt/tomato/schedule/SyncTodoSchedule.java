package com.cyssxt.tomato.schedule;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.utils.DateUtils;
import com.cyssxt.tomato.entity.ToDosEntity;
import com.cyssxt.tomato.service.PushService;
import com.cyssxt.tomato.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SyncTodoSchedule {

    private final static Logger logger = LoggerFactory.getLogger(SyncTodoSchedule.class);
    private final static ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static final int PUSH_TIME_INTERVAL = 1000 * 30;
    @Resource
    TodoService todoService;

    @Resource
    PushService pushService;

    @Scheduled(cron = "0 0 1 * * ?")//每天1点执行
    public void sync() {
        final Integer generator = DateUtils.getCurrentDataFormatInteger();
        List<ToDosEntity> toDosEntityList = todoService.repeats();
        for (ToDosEntity toDosEntity : toDosEntityList) {
            todoService.delTodo(toDosEntity,generator);
        }
    }

    void init() {
        executorService.execute(() -> {
            while (true) {
                try {
                    pushService.pushWillStart();
                    pushService.pushThreeDay();
                    pushService.pushTime();
                } catch (Exception e) {
                    logger.error("push thread={}", e);
                } finally {
                    try {
                        Thread.sleep(PUSH_TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Scheduled(cron = "0 0 9 * * ?")//每天9点推送打开App
    public void pushAll() {
        logger.info("push all");
        pushService.pushEveryDay();
    }

    @Scheduled(cron = "0 0 10 * * ?")//每天10点执行
    public void pushEnd() {
        try {
            logger.info("push end");
            pushService.pushEnd();
        } catch (ValidException e) {
            e.printStackTrace();
        }
    }

}
