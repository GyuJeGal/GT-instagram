package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserPage;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2201, message = "잘못된 페이지 인덱스입니다."),
            @ApiResponse(code = 3130, message = "개인정보 처리 방침 동의가 필요합니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2170, message = "게시글 내용은 최대 1000자입니다."),
            @ApiResponse(code = 2171, message = "사진 리스트가 필요합니다."),
            @ApiResponse(code = 2172, message = "사진은 최대 10장입니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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
    @PatchMapping("/{userId}/{postId}")
    @ApiOperation(value = "게시글 수정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2170, message = "게시글 내용은 최대 1000자입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3190, message = "게시글 수정에 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> updatePost(@PathVariable("userId") long userId,
                                           @PathVariable("postId") long postId,
                                           @RequestBody PostContents postContents) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(postContents.getContents().length() > 1000) {
                return new BaseResponse<>(POST_POSTS_OVER_LENGTH_CONTENTS);
            }

            postService.updatePost(userId, postId, postContents.getContents());

            String result = "게시글 수정 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/{postId}/status")
    @ApiOperation(value = "게시글 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 3200, message = "게시글 삭제에 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deletePost(@PathVariable("userId") long userId,
                                           @PathVariable("postId") long postId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.deletePost(userId, postId);

            String result = "게시글 삭제 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{postId}/likes")
    @ApiOperation(value = "게시글 좋아요 설정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 3160, message = "이미 게시글 좋아요 상태입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 3165, message = "이미 게시글 좋아요 취소 상태입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2201, message = "잘못된 페이지 인덱스입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2180, message = "댓글 내용을 입력해주세요."),
            @ApiResponse(code = 2185, message = "댓글은 최대 200자 입력해주세요."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
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

            if(setPostCommentReq.getContents().length() > 200) {
                return new BaseResponse<>(POST_POSTS_OVER_LENGTH_COMMENT);
            }

            postService.setPostComment(userId, postId, setPostCommentReq.getContents());

            String result = "댓글 작성 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/comments/{commentId}/likes")
    @ApiOperation(value = "댓글 좋아요 설정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3170, message = "댓글을 찾을 수 없습니다."),
            @ApiResponse(code = 3180, message = "이미 댓글 좋아요 상태입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> setCommentLike(@PathVariable("userId") long userId,
                                               @PathVariable("commentId") long commentId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.setCommentLike(userId, commentId);

            String result = "댓글 좋아요 설정 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/comments/{commentId}/likes")
    @ApiOperation(value = "댓글 좋아요 취소")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3170, message = "댓글을 찾을 수 없습니다."),
            @ApiResponse(code = 3185, message = "이미 댓글 좋아요 취소 상태입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteCommentLike(@PathVariable("userId") long userId,
                                               @PathVariable("commentId") long commentId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.deleteCommentLike(userId, commentId);

            String result = "댓글 좋아요 취소 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/comments/{commentId}/status")
    @ApiOperation(value = "댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 3170, message = "댓글을 찾을 수 없습니다."),
            @ApiResponse(code = 3200, message = "댓글 삭제에 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> deleteComment(@PathVariable("userId") long userId,
                                              @PathVariable("commentId") long commentId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            postService.deleteComment(userId, commentId);

            String result = "댓글 삭제 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{postId}")
    @ApiOperation(value = "게시글 신고")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2190, message = "잘못된 신고 형식입니다."),
            @ApiResponse(code = 3150, message = "게시글을 찾을 수 없습니다."),
            @ApiResponse(code = 3210, message = "게시글 신고에 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> reportPost(@PathVariable("userId") long userId,
                                           @PathVariable("postId") long postId,
                                           @RequestParam("reportType") int reportType) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // reportType 값
            // 1:스팸, 2:나체 이미지 또는 성적 행위, 3:마음에 들지 않습니다, 4:사기 또는 거짓, 5:혐오 발언 또는 상징
            // 6:거짓 정보, 7:따돌림 또는 괴롭힘, 8:폭력 또는 위험한 단체, 9:지식재산권 침해
            if(reportType <= 0 || reportType > 9) {
                return new BaseResponse<>(INVALID_REPORT_TYPE);
            }

            postService.reportPost(userId, postId, reportType);

            String result = "게시글 신고 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/comments/{commentId}")
    @ApiOperation(value = "댓글 신고")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2190, message = "잘못된 신고 형식입니다."),
            @ApiResponse(code = 3170, message = "댓글을 찾을 수 없습니다."),
            @ApiResponse(code = 3220, message = "댓글 신고에 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<String> reportPostComment(@PathVariable("userId") long userId,
                                           @PathVariable("commentId") long commentId,
                                           @RequestParam("reportType") int reportType) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // reportType 값
            // 1:스팸, 2:나체 이미지 또는 성적 행위, 3:혐오 발언 또는 상징, 4:폭력 또는 위험한 단체, 5:불법 또는 규제 상품 판매
            // 6:따돌림 또는 괴롭힘, 7:지식재산권 침해, 8:거짓 정보, 9:자살, 자해 및 섭식 장애
            if(reportType <= 0 || reportType > 9) {
                return new BaseResponse<>(INVALID_REPORT_TYPE);
            }

            postService.reportPostComment(userId, commentId, reportType);

            String result = "댓글 신고 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
