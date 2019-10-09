package com.cyssxt.tomato.dto;

import com.cyssxt.common.constant.CharConstant;
import com.cyssxt.common.exception.ValidException;
import com.cyssxt.tomato.entity.ToDosEntity;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
public class TodoInfoDto {
    List<TagDto> tags;
    TodoSimpleDto todo;

    public TodoInfoDto(ToDosEntity todo, List<TagDto> tags, List<ActionDto> actions) throws ValidException {
        TodoSimpleDto simpleDto = new TodoSimpleDto();
        String execDay = todo.getRepeatExecDay();
        todo.parse(simpleDto);
        if(!StringUtils.isEmpty(execDay)){
            String[] execDays= execDay.split(CharConstant.COMMA);
            Byte[] tmps = new Byte[execDays.length];
            for(int i=0;i<tmps.length;i++){
                tmps[i] = Byte.valueOf(execDays[i]);
            }
            simpleDto.setRepeatExecDays(tmps);
        }
        simpleDto.setItems(actions);
        this.todo = simpleDto;
        this.tags = tags;
    }
}
