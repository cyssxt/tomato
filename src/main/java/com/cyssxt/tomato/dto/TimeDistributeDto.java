package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class TimeDistributeDto {
    private TimeDto timeDto;
    private List<DutyDistributeDto> duties;

    public TimeDistributeDto(TimeDto timeDto, List<DutyDistributeDto> duties, Long projectNum) {
        this.timeDto = timeDto;
        this.duties = duties;
        if(this.timeDto!=null) {
            this.timeDto.setProjectNum(projectNum);
        }
    }
}
