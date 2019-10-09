package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class PushClientDto {
    String clientId;
    Object value;

    public PushClientDto(String clientId, Object value) {
        this.clientId = clientId;
        this.value = value;
    }
    public PushClientDto(){}
}
