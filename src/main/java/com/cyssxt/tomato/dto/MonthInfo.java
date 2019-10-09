package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class MonthInfo {
    Integer month;
    Integer fullMonth;
    public MonthInfo(Integer month, Integer fullMonth) {
        this.month = month;
        this.fullMonth = fullMonth;
    }
}
