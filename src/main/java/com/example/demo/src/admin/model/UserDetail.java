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
public class UserDetail {
    @ApiModelProperty(value = "사용자 Id", required = true)
    private long userId;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "소개", required = true)
    private String userIntro;

    @ApiModelProperty(value = "웹사이트", required = true)
    private String webSite;

    @ApiModelProperty(value = "아이디", required = true)
    private String nickName;

    @ApiModelProperty(value = "회원 상태", required = true, notes = "-1:탈퇴, 0:휴먼, 1:활성화, 2:정지")
    private int status;

    @ApiModelProperty(value = "회원 가입 일자", required = true)
    private String createAt;

    @ApiModelProperty(value = "프로필 수정 일자", required = true)
    private String updateAt;
    
    @ApiModelProperty(value = "로그인 가입 일자", required = true)
    private String loginAt;

    @ApiModelProperty(value = "계정 공개/비공개 여부", required = true)
    private boolean openStatus;
}
