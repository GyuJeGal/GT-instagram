package com.example.demo.src.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRes {
    @ApiModelProperty(value = "사용자 Id", required = true)
    private long userId;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "아이디", required = true)
    private String nickName;

    @ApiModelProperty(value = "회원 상태", required = true, notes = "-1:탈퇴, 0:휴먼, 1:활성화, 2:정지")
    private String status;

    @ApiModelProperty(value = "회원 가입 일자", required = true)
    private String createAt;

}
