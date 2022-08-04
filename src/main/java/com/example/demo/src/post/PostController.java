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

//    @ResponseBody
//    @GetMapping("/{userId}")
//    @ApiOperation(value = "홈 피드 조회")
//    public BaseResponse<GetPostRes> getPosts(@PathVariable("userId") long userId) {
//        try {
//            long userIdByJwt = jwtService.getUserIdx();
//            if (userIdByJwt != userId) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//
//            return new BaseResponse<>();
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }



}
