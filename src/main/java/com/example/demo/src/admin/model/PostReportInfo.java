package com.example.demo.src.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PostReportInfo {
    @ApiModelProperty(value = "신고 번호", required = true)
    private long reportId;

    @ApiModelProperty(value = "게시글 번호", required = true)
    private long postId;

    @ApiModelProperty(value = "작성자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value = "신고 내용", required = true)
    private String contents;

    @ApiModelProperty(value = "신고 시간", required = true)
    private String createAt;
}
