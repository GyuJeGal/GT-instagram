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
public class GetUserPage {
    public GetUserPage(String nickName, String profileImg, String userName, Integer countPosts,
                       Integer countFollower, Integer countFollowing, int followStatus) {
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.userName = userName;
        this.countPosts = countPosts;
        this.countFollower = countFollower;
        this.countFollowing = countFollowing;
        this.followStatus = followStatus;
    }

    @ApiModelProperty(value = "사용자 이름", required = true)
    private String nickName;

    @ApiModelProperty(value = "프로필 사진", required = true)
    private String profileImg;

    @ApiModelProperty(value = "이름", required = true)
    private String userName;

    @ApiModelProperty(value = "소개", required = true)
    private String userIntro;

    @ApiModelProperty(value = "웹사이트", required = true)
    private String webSite;

    @ApiModelProperty(value = "게시글 개수", required = true)
    private Integer countPosts;

    @ApiModelProperty(value = "팔로워 수", required = true)
    private Integer countFollower;

    @ApiModelProperty(value = "팔로잉 수", required = true)
    private Integer countFollowing;

    @ApiModelProperty(value = "팔로우 여부", required = true, reference = "-1:언팔로우 상태, 0:팔로우 요청 중, 1:팔로우 중")
    private int followStatus;

    @ApiModelProperty(value = "게시글 리스트", required = true)
    private List<Post> postList;
}
