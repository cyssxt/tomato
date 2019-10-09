package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class PushClientDataDto<T> {
    List<String> clientIds;
    String value;
    T data;

    public PushClientDataDto(List<String> clientIds, String value) {
        this.clientIds = clientIds;
        this.value = value;
    }

    public PushClientDataDto(List<String> clientIds, String value, T data) {
        this.clientIds = clientIds;
        this.value = value;
        this.data = data;
    }
}
