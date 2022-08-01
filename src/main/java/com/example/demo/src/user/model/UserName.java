package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserName {
    @JsonCreator
    public UserName(String userName) {
        this.userName = userName;
    }

    @ApiModelProperty(value = "이름", required = true)
    private String userName;
}
