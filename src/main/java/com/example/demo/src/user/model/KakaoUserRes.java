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
public class KakaoUserRes {
    @ApiModelProperty(value = "회원 가입 절차 필요 여부", required = true)
    private Boolean needCreateUser;

    @ApiModelProperty(value = "jwt 값", required = true)
    private String jwt;

    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;
}
