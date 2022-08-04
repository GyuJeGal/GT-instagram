package com.example.demo.src.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyPageRes {
    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "게시글 개수", required = true)
    private Integer countPosts;

    @ApiModelProperty(value = "팔로워 수", required = true)
    private Integer countFollower;

    @ApiModelProperty(value = "팔로잉 수", required = true)
    private Integer countFollowing;

    @ApiModelProperty(value = "게시글 리스트", required = true)
    private List<Post> postList;

}
