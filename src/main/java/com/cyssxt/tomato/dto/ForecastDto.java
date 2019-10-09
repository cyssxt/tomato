package com.cyssxt.tomato.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ForecastDto {
    BigInteger count;
    Integer date;
    BigInteger weekDay;
}
