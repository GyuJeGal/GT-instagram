package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PageInfo {
    @JsonCreator
    public PageInfo(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    @ApiModelProperty(value = "페이지 인덱스", required = true)
    private Integer pageIndex;
}
