package com.cyssxt.tomato.dto;

import com.cyssxt.tomato.controller.request.TimeFormatReq;
import lombok.Data;

@Data
public class ConsumTimeReq extends TimeFormatReq {
    Integer monthNo;
}
