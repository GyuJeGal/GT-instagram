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
public class PostLoginReq {
    @ApiModelProperty(value = "로그인 아이디(사용자 이름)", required = true)
    private String loginId;

    @ApiModelProperty(value = "비밀 번호", required = true)
    private String password;
}
