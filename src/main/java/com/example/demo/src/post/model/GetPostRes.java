package com.example.demo.src.post.model;

import com.example.demo.src.user.model.Post;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GetPostRes {
    public GetPostRes(long userId, String nickName, String profileImg, long postId, boolean postLike,
                      int countLike, String contents, int countComment, String createAt) {
        this.userId = userId;
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.postId = postId;
        this.postLike = postLike;
        this.countLike = countLike;
        this.contents = contents;
        this.countComment = countComment;
        this.createAt = createAt;
    }

    @ApiModelProperty(value = "사용자 ID", required = true)
    private long userId;

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "게시글 ID", required = true)
    private long postId;

    @ApiModelProperty(value = "게시글 사진 리스트", required = true)
    private List<String> postImgList;

    @ApiModelProperty(value = "좋아요 여부", required = true)
    private boolean postLike;

    @ApiModelProperty(value = "좋아요 개수", required = true)
    private int countLike;

    @ApiModelProperty(value = "게시글 내용", required = true)
    private String contents;

    @ApiModelProperty(value = "댓글 개수", required = true)
    private int countComment;

    @ApiModelProperty(value = "작성 시간", required = true)
    private String createAt;
}
