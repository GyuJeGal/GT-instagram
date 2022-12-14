package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PostUserRes {
    @ApiModelProperty(value = "jwt 값", required = true)
    private String jwt;

    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;
}
