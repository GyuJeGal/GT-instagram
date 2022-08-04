package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;


import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;
    private final JwtService jwtService;


    @Autowired
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("")
    @ApiOperation(value = "회원가입")
    public BaseResponse<PostUserRes> createUser(@ApiParam(value = "회원가입 요청 바디") @RequestBody PostUserReq postUserReq) {

        if(postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        if(postUserReq.getPhoneNumber().length() > 11) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        String phoneNumberPattern = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$";
        if(!Pattern.matches(phoneNumberPattern, postUserReq.getPhoneNumber())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        if(postUserReq.getUserName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(postUserReq.getUserName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NAME);
        }

        if(postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        String passwordPattern = "^(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{6,20}";
        if(!Pattern.matches(passwordPattern, postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        if(postUserReq.getBirthDay() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTHDAY);
        }

        String birthDayPattern = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
        if(!Pattern.matches(birthDayPattern, postUserReq.getBirthDay())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTHDAY);
        }

        if(postUserReq.getCheckPrivacy() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PRIVACY);
        }

        if(postUserReq.getCheckPrivacy() != true) {
            return new BaseResponse<>(POST_USERS_INVALID_PRIVACY);
        }

        if(postUserReq.getNickName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        if(postUserReq.getNickName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NICKNAME);
        }

        String nickNamePattern = "^[a-z0-9._]{1,20}$";
        if(!Pattern.matches(nickNamePattern, postUserReq.getNickName())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PostMapping("/login")
    @ApiOperation(value = "자체 로그인")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {

        if(postLoginReq.getLoginId() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_LOGIN_ID);
        }

        if(postLoginReq.getLoginId().length() < 3 || postLoginReq.getLoginId().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVERFLOW_LOGIN_ID);
        }

        if(postLoginReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        String passwordPattern = "^(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{6,20}";
        if(!Pattern.matches(passwordPattern, postLoginReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PostMapping("/kakao-login")
    @ApiOperation(value = "카카오 로그인")
    public BaseResponse<KakaoUserRes> kakaoLogIn(@RequestBody KakaoAccessToken accessToken) {
        if(accessToken.getAccessToken() == null) {
            return new BaseResponse<>(REQUEST_ERROR);
        }

        try {
            KakaoUserRes kakaoUserRes = userService.kakaoLogIn(accessToken.getAccessToken());
            return new BaseResponse<>(kakaoUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/kakao-login/{userId}")
    @ApiOperation(value = "카카오 로그인 후 회원 가입")
    public BaseResponse<String> createKakaoUser(@PathVariable("userId") long userId,
                                                 @RequestBody KakaoUserReq kakaoUserReq) {
        if(kakaoUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }

        if(kakaoUserReq.getPhoneNumber().length() > 11) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        String phoneNumberPattern = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$";
        if(!Pattern.matches(phoneNumberPattern, kakaoUserReq.getPhoneNumber())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        if(kakaoUserReq.getUserName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }

        if(kakaoUserReq.getUserName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NAME);
        }

        if(kakaoUserReq.getBirthDay() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTHDAY);
        }

        String birthDayPattern = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
        if(!Pattern.matches(birthDayPattern, kakaoUserReq.getBirthDay())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTHDAY);
        }

        if(kakaoUserReq.getCheckPrivacy() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PRIVACY);
        }

        if(kakaoUserReq.getCheckPrivacy() != true) {
            return new BaseResponse<>(POST_USERS_INVALID_PRIVACY);
        }

        if(kakaoUserReq.getNickName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        if(kakaoUserReq.getNickName().length() > 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NICKNAME);
        }

        String nickNamePattern = "^[a-z0-9._]{1,20}$";
        if(!Pattern.matches(nickNamePattern, kakaoUserReq.getNickName())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        try {
            userService.createKakaoUser(userId, kakaoUserReq);

            String result = "카카오 회원 가입 완료!";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/nicknames")
    @ApiOperation(value = "닉네임 사용 가능 검사")
    public BaseResponse<String> checkNickName(@RequestBody NickName nickName) {
        if(nickName.getNickName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        if(nickName.getNickName().length() >= 20) {
            return new BaseResponse<>(POST_USERS_OVER_LENGTH_NICKNAME);
        }

        String nickNamePattern = "^[a-z0-9._]{1,20}$";
        if(!Pattern.matches(nickNamePattern, nickName.getNickName())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        try{
            userService.checkNickName(nickName.getNickName());
            String result = "닉네임 검사 완료! 해당 아이디 사용가능";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/{userId}/profiles")
    @ApiOperation(value = "프로필 정보 조회")
    public BaseResponse<GetUserInfo> getUserInfo(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetUserInfo getUserInfo = userService.getUserInfo(userId);
            return new BaseResponse<>(getUserInfo);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("/{userId}/profiles")
    @ApiOperation(value = "프로필 수정(프로필 사진, 웹사이트, 소개)")
    public BaseResponse<String> modifyUserProfile(@PathVariable("userId") long userId, @RequestBody PatchUserReq patchUserReq) {

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.modifyUserProfile(userId, patchUserReq);

            String result = "프로필 수정 성공!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PatchMapping("/{userId}/usernames")
    @ApiOperation(value = "프로필 수정(이름)")
    public BaseResponse<String> modifyUserName(@PathVariable("userId") long userId, @RequestBody UserName userName) {
        if(userName.getUserName() == null) {
            return new BaseResponse<>(PATCH_USERS_EMPTY_USERNAME);
        }

        if(userName.getUserName().length() > 20) {
            return new BaseResponse<>(PATCH_USERS_OVERFLOW_USERNAME);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.modifyUserName(userId, userName.getUserName());

            String result = "이름 수정 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/nicknames")
    @ApiOperation(value = "프로필 수정(사용자 이름)")
    public BaseResponse<String> modifyNickName(@PathVariable("userId") long userId, @RequestBody NickName nickName) {
        if(nickName.getNickName() == null) {
            return new BaseResponse<>(PATCH_USERS_EMPTY_NICKNAME);
        }

        if(nickName.getNickName().length() >= 20) {
            return new BaseResponse<>(PATCH_USERS_OVERFLOW_NICKNAME);
        }

        String nickNamePattern = "^[a-z0-9._]{1,20}$";
        if(!Pattern.matches(nickNamePattern, nickName.getNickName())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.modifyNickName(userId, nickName.getNickName());

            String result = "사용자 이름 수정 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PatchMapping("/{userId}/open-status")
    @ApiOperation(value = "프로필 수정(공개/비공개 설정)")
    public BaseResponse<String> modifyUserStatus(@PathVariable("userId") long userId, @RequestParam("status") Integer status) {
        if(status == null) {
            return new BaseResponse<>(PATCH_USERS_EMPTY_STATUS);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.modifyUserStatus(userId, status);

            String result;
            if(status == 0) {
                result = "비공개 계정 전환 완료!";
            }
            else {
                result = "공개 계정 전환 완료!";
            }
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/status")
    @ApiOperation(value = "회원 탈퇴")
    public BaseResponse<String> deleteUser(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.deleteUser(userId);

            String result = "회원 탈퇴 성공!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/{followUserId}/follows")
    @ApiOperation(value = "팔로우 신청")
    public BaseResponse<String> followUser(@PathVariable("userId") long userId,
                                           @PathVariable("followUserId") long followUserId) {
        // 본인에게 팔로우 신청을 하는 경우
        if(followUserId == userId) {
            return new BaseResponse<>(POST_USERS_INVALID_FOLLOW);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.followUser(userId, followUserId);

            String result = "팔로우 신청 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/{unfollowUserId}/follows")
    @ApiOperation(value = "팔로우 취소")
    public BaseResponse<String> unfollowUser(@PathVariable("userId") long userId,
                                             @PathVariable("unfollowUserId") long unfollowUserId) {
        // 본인에게 팔로우 신청을 하는 경우
        if(unfollowUserId == userId) {
            return new BaseResponse<>(PATCH_USERS_INVALID_UNFOLLOW);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.unfollowUser(userId, unfollowUserId);

            String result = "팔로우 취소 완료!";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}/follows")
    @ApiOperation(value = "팔로우 요청 조회")
    public BaseResponse<List<GetFollow>> getFollows(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            List<GetFollow> getFollowList = userService.getFollows(userId);

            return new BaseResponse<>(getFollowList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/follows/{followId}")
    @ApiOperation(value = "팔로우 요청 승인")
    public BaseResponse<String> acceptFollow(@PathVariable("userId") long userId, @PathVariable("followId") long followId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.acceptFollow(userId, followId);

            String result = "팔로우 요청 승인 성공!";

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{userId}/follows/{followId}")
    @ApiOperation(value = "팔로우 요청 취소")
    public BaseResponse<String> rejectFollow(@PathVariable("userId") long userId, @PathVariable("followId") long followId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.rejectFollow(userId, followId);

            String result = "팔로우 요청 취소 성공!";

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{userId}/privacy")
    @ApiOperation(value = "개인정보 처리 방침 동의 등록")
    public BaseResponse<String> insertUserPrivacy(@PathVariable("userId") long userId) {
        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.insertUserPrivacy(userId);

            String result = "개인정보 처리 방침 동의 등록 완료!";

            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/{userId}")
    @ApiOperation(value = "마이페이지 조회")
    public BaseResponse<GetMyPageRes> getMyPage(@PathVariable("userId") long userId, @RequestParam("pageIndex") int pageIndex) {

        if(pageIndex <= 0) {
            return new BaseResponse<>(INVALID_PAGE_INDEX);
        }

        try {
            long userIdByJwt = jwtService.getUserIdx();
            if (userIdByJwt != userId) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetMyPageRes getMyPageRes = userService.getMyPage(userId, pageIndex);

            return new BaseResponse<>(getMyPageRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}

