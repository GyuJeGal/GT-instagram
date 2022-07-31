package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    @ApiModelProperty(value = "휴대폰 번호", required = true)
    private String phoneNumber;
    
    @ApiModelProperty(value = "이름", required = true)
    private String userName;
    
    @ApiModelProperty(value = "비밀번호", required = true)
    private String password;
    
    @ApiModelProperty(value = "생일", required = true)
    private String birthDay;
    
    @ApiModelProperty(value = "개인정보 동의 여부", required = true)
    private Boolean checkPrivacy;
    
    @ApiModelProperty(value = "닉네임", required = true)
    private String nickName;
    
}