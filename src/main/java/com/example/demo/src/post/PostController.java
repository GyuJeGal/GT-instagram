package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.GetPostRes;
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

import static com.example.demo.config.BaseResponseStatus.INVALID_PAGE_INDEX;
import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;

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
    public BaseResponse<String> createPost(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }


            String result = "게시글 작성 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
