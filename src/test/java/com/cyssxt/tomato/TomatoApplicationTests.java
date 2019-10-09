package com.cyssxt.tomato;

import com.cyssxt.common.exception.ValidException;
import com.cyssxt.common.utils.JpaUtil;
import com.cyssxt.tomato.constant.RepeatTypeConstant;
import com.cyssxt.tomato.dao.TodosRepository;
import com.cyssxt.tomato.entity.ToDosEntity;
import com.cyssxt.tomato.netease.SmsSend;
import com.cyssxt.tomato.service.PushService;
import com.cyssxt.tomato.service.TodoService;
import com.cyssxt.tomato.util.AppPushUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TomatoApplicationTests {

    @Resource
    SmsSend smsSend;

    @Resource
    AppPushUtil appPushUtil;

    @Test
    public void push() throws Exception {
    }


    @Resource
    PushService pushService;

    @Test
    public void contextLoads() {
    }
    @Test
    public void send() throws Exception {
        pushService.pushEveryDay();
    }

    @Resource
    TodosRepository todosRepository;

    @Resource
    TodoService todoService;
    @Test
    public void testPlan() throws Exception {
//        ToDosEntity toDosEntity =toDosEntity JpaUtil.check("2da4aae333ae427282416359ece950ab",todosRepository,true);
//        todoService.delTodo(todoServiceEntity,"yyyyMMdd");
    }

    @Test
    public void delTodo() throws ValidException {
        ToDosEntity toDosEntity = JpaUtil.check("93869d2b0aaf43f8822ff2316a7bf126",todosRepository,true);
        toDosEntity.setRepeatType(RepeatTypeConstant.EVERY_MONTH.getValue());
        todoService.delTodo(toDosEntity,20190405);

    }

}

