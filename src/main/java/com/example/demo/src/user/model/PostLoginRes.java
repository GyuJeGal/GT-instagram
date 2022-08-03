package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRes {
    @ApiModelProperty(value = "jwt 값", required = true)
    private String jwt;

    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;
}
