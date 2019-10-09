package com.cyssxt.tomato.listener;

import com.cyssxt.tomato.schedule.ScheduleManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CustomSpringContextLoaderListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    ScheduleManager scheduleManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        scheduleManager.init();
    }
}
