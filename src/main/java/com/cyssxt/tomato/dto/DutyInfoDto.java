package com.cyssxt.tomato.dto;

import lombok.Data;

import java.util.List;

@Data
public class DutyInfoDto {
    private String title;
    private String color;
    private List<DutySectionDto> items;
    private List<TagDto> tags;
}
