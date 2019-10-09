package com.cyssxt.tomato.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DegreeDto {
    Timestamp startTime;
    Timestamp endTime;
    Integer degree;
    String color;
}
