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
public class GetFollow {
    @ApiModelProperty(value = "사용자 ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "팔로우 ID", required = true)
    private Long followId;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;
}