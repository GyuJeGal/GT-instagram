package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.admin.model.UserDetail;
import com.example.demo.src.admin.model.UserInfoReq;
import com.example.demo.src.admin.model.UserInfoRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AdminService adminService;
    private final JwtService jwtService;

    public AdminController(AdminService adminService, JwtService jwtService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/users")
    @ApiOperation(value = "관리자 페이지(회원 조회)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<UserInfoRes>> getUsers(@RequestParam(value = "name", required = false) String userName,
                                                    @RequestParam(value = "userId", required = false) String nickName,
                                                    @RequestParam(value = "createAt", required = false) String createAt,
                                                    @RequestParam(value = "status", required = false) Integer status) {
        UserInfoReq userInfoReq = new UserInfoReq(userName, nickName, status, createAt);

        try{
            List<UserInfoRes> userInfoList = adminService.getUsers(userInfoReq);
            return new BaseResponse<>(userInfoList);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/users/{userId}")
    @ApiOperation(value = "관리자 페이지(회원 상세 조회)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<UserDetail> getUserDetail(@PathVariable("userId") long userId) {

        try{
            UserDetail userDetail = adminService.getUserDetail(userId);
            return new BaseResponse<>(userDetail);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
