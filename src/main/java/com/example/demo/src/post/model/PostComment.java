package com.example.demo.src.post.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {
    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "댓글 내용", required = true)
    private String contents;

    @ApiModelProperty(value = "작성 시간", required = true)
    private String createAt;

    @ApiModelProperty(value = "좋아요 여부", required = true)
    private boolean commentLike;
}
