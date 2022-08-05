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
public class CreatePostReq {
    @ApiModelProperty(value = "게시글 사진 리스트", required = true)
    private List<String> postImgList;
    
    @ApiModelProperty(value = "게시글 내용", required = true)
    private String contents;
    
}
