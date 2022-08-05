package com.example.demo.src.post.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SetPostCommentReq {
    @JsonCreator
    public SetPostCommentReq(String contents) {
        this.contents = contents;
    }

    @ApiModelProperty(value = "댓글 내용", required = true)
    private String contents;
}
