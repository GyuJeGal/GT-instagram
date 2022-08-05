package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.CreatePostReq;
import com.example.demo.src.post.model.GetPostCommentsRes;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.SetPostCommentReq;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserPage;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostService postService;
    private final JwtService jwtService;


    @Autowired
    public PostController(PostService postService, JwtService jwtService){
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/{userId}")
    @ApiOperation(value = "홈 피드 조회")
    public BaseResponse<List<GetPostRes>> getPosts(@PathVariable("userId") long userId,
                                                   @RequestParam("pageIndex") int pageIndex) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(pageIndex <= 0) {
                return new BaseResponse<>(INVALID_PAGE_INDEX);
            }

            List<GetPostRes> getPostResList = postService.getPosts(userId, pageIndex);

            return new BaseResponse<>(getPostResList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}")
    @ApiOperation(value = "게시글 등록(작성)")
    public BaseResponse<String> createPost(@PathVariable("userId") long userId, @RequestBody CreatePostReq createPostReq) {
        if(createPostReq.getContents().length() > 1000) {
            return new BaseResponse<>(POST_POSTS_OVER_LENGTH_CONTENTS);
        }
        if(createPostReq.getPostImgList() == null) {
            return new BaseResponse<>(POST_POSTS_EMPTY_IMG_LIST);
        }
        if(createPostReq.getPostImgList().size() > 10) {
            return new BaseResponse<>(POST_POSTS_OVERSIZE_IMG_LIST);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.createPost(userId, createPostReq);

            String result = "게시글 작성 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{postId}/likes")
    @ApiOperation(value = "게시글 좋아요 설정")
    public BaseResponse<String> setPostLike(@PathVariable("userId") long userId, @PathVariable("postId") long postId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.setPostLike(userId, postId);

            String result = "게시글 좋아요 설정 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/{postId}/likes")
    @ApiOperation(value = "게시글 좋아요 취소")
    public BaseResponse<String> deletePostLike(@PathVariable("userId") long userId, @PathVariable("postId") long postId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.deletePostLike(userId, postId);

            String result = "게시글 좋아요 취소 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/{postId}/comments")
    @ApiOperation(value = "게시글 댓글 모두 보기")
    public BaseResponse<GetPostCommentsRes> getPostComments(@PathVariable("userId") long userId,
                                                            @PathVariable("postId") long postId,
                                                            @RequestParam("pageIndex") int pageIndex) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(pageIndex <= 0) {
                return new BaseResponse<>(INVALID_PAGE_INDEX);
            }

            GetPostCommentsRes getPostCommentsRes = postService.getPostComments(userId, postId, pageIndex);

            return new BaseResponse<>(getPostCommentsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{postId}/comments")
    @ApiOperation(value = "게시글 댓글 작성")
    public BaseResponse<String> setPostComment(@PathVariable("userId") long userId,
                                                @PathVariable("postId") long postId,
                                                @RequestBody SetPostCommentReq setPostCommentReq) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(setPostCommentReq.getContents().length() == 0) {
                return new BaseResponse<>(POST_POSTS_EMPTY_COMMENT);
            }

            postService.setPostComment(userId, postId, setPostCommentReq.getContents());

            String result = "댓글 작성 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
