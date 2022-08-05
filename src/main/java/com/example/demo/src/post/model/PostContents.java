package com.example.demo.src.post.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PostContents {
    @JsonCreator
    public PostContents(String contents) {
        this.contents = contents;
    }

    @ApiModelProperty(value = "게시글 본문", required = true)
    private String contents;
}
