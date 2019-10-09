package com.cyssxt.tomato.dto;

import lombok.Data;

@Data
public class TimePushDto {
    String clientId;
    String timeId;
    Long configTime;
}
