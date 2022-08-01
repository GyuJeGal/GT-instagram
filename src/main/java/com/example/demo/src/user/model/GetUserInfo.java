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
public class GetUserInfo {
    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "웹사이트", required = true)
    private String webSite;

    @ApiModelProperty(value = "소개", required = true)
    private String userIntro;
}
