package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinishDto {
    private Integer fullMonth;
    private Integer monthNo;
    private List<FinishItemDto> items;
}
