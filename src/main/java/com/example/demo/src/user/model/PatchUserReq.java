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
public class PatchUserReq {
    @ApiModelProperty(value = "프로필 사진", required = false)
    private String profileImg;

    @ApiModelProperty(value = "웹사이트", required = false)
    private String webSite;

    @ApiModelProperty(value = "소개", required = false)
    private String userIntro;
}
