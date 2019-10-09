package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class TotalCount {
    Long normalTotal;
    Long overtimeTotal;
    public TotalCount(long normalTotal, long overtimeTotal) {
        this.normalTotal = normalTotal;
        this.overtimeTotal = overtimeTotal;
    }
}
