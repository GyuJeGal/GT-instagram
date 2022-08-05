package com.example.demo.src.post.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GetPostCommentsRes {
    public GetPostCommentsRes(long userId, String nickName, String profileImg, String contents, String createAt) {
        this.userId = userId;
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.contents = contents;
        this.createAt = createAt;
    }

    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "게시글 본문", required = true)
    private String contents;

    @ApiModelProperty(value = "작성 시간", required = true)
    private String createAt;

    @ApiModelProperty(value = "댓글 리스트", required = true)
    private List<PostComment> postCommentList;
}
