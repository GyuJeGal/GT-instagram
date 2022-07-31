package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NickName {

    @JsonCreator
    public NickName(String nickName) {
        this.nickName = nickName;
    }

    @ApiModelProperty(value = "닉네임", required = true)
    private String nickName;
}
