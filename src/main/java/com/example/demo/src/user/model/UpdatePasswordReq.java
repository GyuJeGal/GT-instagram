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
public class UpdatePasswordReq {
    @ApiModelProperty(value = "휴대폰 번호", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "비밀 번호", required = true)
    private String password;
}
