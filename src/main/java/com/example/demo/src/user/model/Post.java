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
public class Post {
    @ApiModelProperty(value = "게시글 ID", required = true)
    private Long postId;

    @ApiModelProperty(value = "게시글 첫 사진 URL", required = true)
    private String profileImg;
}
