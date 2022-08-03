package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class KakaoAccessToken {
    @JsonCreator
    public KakaoAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @ApiModelProperty(value = "카카오 엑세스 토큰", required = true)
    private String accessToken;
}
