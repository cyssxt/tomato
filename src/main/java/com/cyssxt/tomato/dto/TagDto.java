package com.cyssxt.tomato.dto;

import com.cyssxt.common.filters.DefaultFilter;
import lombok.Data;

@Data
public class TagDto extends DefaultFilter {

    private String tagId;
    private String tagName;
    private String contentId;


    @Override
    public String[] getExcludeFields() {
        return new String[]{"contentId"};
    }

    public TagDto(){}
    public TagDto(String tagName) {
        this.tagName = tagName;
    }

    public TagDto(String tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public TagDto(String tagId, String tagName, String contentId) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.contentId = contentId;
    }
}
