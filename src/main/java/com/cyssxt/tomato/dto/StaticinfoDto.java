package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class StaticinfoDto {
    private Long totalInbox;
    private Long totalToday;

    public StaticinfoDto(Long totalInbox, Long totalToday) {
        this.totalInbox = totalInbox;
        this.totalToday = totalToday;
    }
}
