package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.google.gson.Gson;
import net.nurigo.java_sdk.api.Message;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;

    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {

        try {
            //휴대폰 번호 중복 체크
            if (userDao.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1) {
                throw new BaseException(POST_USERS_EXISTS_PHONENUMBER);
            }

            //사용자 닉네임 중복 체크
            if (userDao.checkNickName(postUserReq.getNickName()) == 1) {
                throw new BaseException(DUPLICATED_NICKNAME);
            }
        } catch (Exception exception) {
            throw exception;
        }

        //비밀 번호 암호화
        try {
            String pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try {
            long userId = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userId);

            return new PostUserRes(jwt, userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void checkNickName(String nickName) throws BaseException {
        try {
            //사용자 닉네임 중복 체크
            if (userDao.checkNickName(nickName) == 1) {
                throw new BaseException(DUPLICATED_NICKNAME);
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    public void modifyUserProfile(long userId, PatchUserReq patchUserReq) throws BaseException {
        try {
            userDao.modifyUserProfile(userId, patchUserReq);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserInfo getUserInfo(long userId) throws BaseException {
        try {
            GetUserInfo getUserInfo = userDao.getUserInfo(userId);
            return getUserInfo;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void modifyUserName(long userId, String userName) throws BaseException {
        try {
            // 같은 이름으로 변경한 경우(아래의 로직 건너뛰기, 변경 카운트 증가X)
            if(!userName.equals(userDao.getUserName(userId))) {
                // 14일 이내에 이름을 2번 변경한 경우
                if(userDao.countModifyUserName(userId) >= 2) {
                    throw new BaseException(FAILED_TO_MODIFY_USERNAME);
                }
                else {
                    userDao.modifyUserName(userId, userName);
                }
            }

        } catch (Exception exception) {
            throw exception;
        }
    }

    public void modifyNickName(long userId, String nickName) throws BaseException {
        try {
            // 같은 사용자 이름으로 변경한 경우(아래의 로직 건너뛰기, 변경 카운트 증가X)
            if(!nickName.equals(userDao.getNickName(userId))) {
                // 14일 이내에 사용자 이름을 2번 변경한 경우
                if(userDao.countModifyNickName(userId) >= 2) {
                    throw new BaseException(FAILED_TO_MODIFY_NICKNAME);
                }
                else {
                    // 사용자 이름이 중복되는 경우
                    if(userDao.checkNickName(nickName) == 1) {
                        throw new BaseException(DUPLICATED_NICKNAME);
                    }
                    else {
                        userDao.modifyNickName(userId, nickName);
                    }
                }
            }

        } catch (Exception exception) {
            throw exception;
        }
    }

    public void modifyUserStatus(long userId, Integer status) throws BaseException {
        try {
            Integer userOpenStatus = userDao.getUserOpenStatus(userId);
            // 사용자 계정이 공개 계정일 때
            if(userOpenStatus == 1) {
                // 공개 계정이지만 공개 계정으로의 요청이 들어오는 잘못된 경우
                if(userOpenStatus.equals(status)) {
                    throw new BaseException(ALREADY_PUBLIC_ACCOUNT);
                }
            }
            // 사용자 계정이 비공개 계정일 때
            else {
                // 비공개 계정이지만 비공개 계정으로의 요청이 들어오는 잘못된 경우
                if(userOpenStatus.equals(status)) {
                    throw new BaseException(ALREADY_PRIVATE_ACCOUNT);
                }
            }

        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.modifyUserStatus(userId, status);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteUser(long userId) throws BaseException {
        try {
            userDao.deleteUser(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void followUser(long userId, long followUserId) throws BaseException {
        try {
            // 팔로우 요청이 있는 경우
            if (userDao.checkFollow(userId, followUserId) == 1) {

                long followId = userDao.getFollowId(userId, followUserId);

                // 팔로우가 된 상태인 경우
                if(userDao.getFollowStatus(followId) == 1) {
                    throw new BaseException(ALREADY_FOLLOW);
                }
                // 상대방이 비공개 계정으로서 팔로우 요청 상태인 경우
                else {
                    throw new BaseException(ALREADY_FOLLOW_REQUEST);
                }

            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.followUser(userId, followUserId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void unfollowUser(long userId, long unfollowUserId) throws BaseException {
        try {
            // 팔로우가 이미 취소 되어있는 경우
            if (userDao.checkFollow(userId, unfollowUserId) == 0) {
                throw new BaseException(ALREADY_UNFOLLOW);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            long followId = userDao.getFollowId(userId, unfollowUserId);

            userDao.unfollowUser(followId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetFollow> getFollows(long userId) throws BaseException {
        try {
            return userDao.getFollows(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void acceptFollow(long userId, long followId) throws BaseException {
        try {
            // 요청 받은 사용자 ID(userId)와 팔로우 요청받은 사용자 ID(followedUserId)가 다른 경우
            if(userDao.getUserIdForFollow(followId) != userId) {
                throw new BaseException(FAILED_TO_ACCEPT_FOLLOW);
            }
            
            int followStatus = userDao.getFollowStatus(followId);
            // 팔로우 요청 상태가 이미 승인된 경우
            if(followStatus == 1) {
                throw new BaseException(ALREADY_FOLLOW);
            }
            // 팔로우 요청 상태가 이미 취소된 경우
            else if(followStatus == -1) {
                throw new BaseException(ALREADY_UNFOLLOW);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.acceptFollow(followId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void rejectFollow(long userId, long followId) throws BaseException {
        try {
            // 요청 받은 사용자 ID(userId)와 팔로우 요청받은 사용자 ID(followedUserId)가 다른 경우
            if(userDao.getUserIdForFollow(followId) != userId) {
                throw new BaseException(FAILED_TO_REJECT_FOLLOW);
            }

            int followStatus = userDao.getFollowStatus(followId);
            // 팔로우 요청 상태가 이미 승인된 경우
            if(followStatus == 1) {
                throw new BaseException(ALREADY_FOLLOW);
            }
            // 팔로우 요청 상태가 이미 취소된 경우
            else if(followStatus == -1) {
                throw new BaseException(ALREADY_UNFOLLOW);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.rejectFollow(followId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        // 아이디 존재하는지 체크
        if(userDao.checkUser(postLoginReq.getLoginId()) == 0) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        User user = userDao.getPwd(postLoginReq);

        //비밀 번호 암호화
        String encryptedPassword;
        try {
            encryptedPassword = new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        //비밀 번호가 일치할 때
        if(user.getPassword().equals(encryptedPassword)) {
            long userId = user.getUserId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(jwt, userId);
        }
        //비밀 번호가 일치하지 않을 때
        else {
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    public KakaoUserRes kakaoLogIn(String accessToken) throws BaseException {
        try {
            Long userId = getUserByToken(accessToken);

            // 카카오 로그인은 성공하였지만 회원이 아닌 경우 또는 회원 가입 절차가 완료되지 않은 경우
            if(userDao.checkUserId(userId) == 0) {
                userDao.createKakaoUser(userId);

                String jwt = jwtService.createJwt(userId);
                return new KakaoUserRes(true, jwt, userId);
            }
            // 회원인 경우
            else {
                String jwt = jwtService.createJwt(userId);
                return new KakaoUserRes(false, jwt, userId);
            }

        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public Long getUserByToken(String accessToken) throws BaseException {
        try {
            // HttpHeader 오브젝트 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
            RestTemplate rt = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

            // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoProfileRequest,
                    String.class
            );
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.getBody());

            System.out.println(element.toString());


            Long id = element.getAsJsonObject().get("id").getAsLong();

            System.out.println("id : " + id);

            return id;

        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public void createKakaoUser(long userId, KakaoUserReq kakaoUserReq) throws BaseException {
        try {
            //휴대폰 번호 중복 체크
            if (userDao.checkPhoneNumber(kakaoUserReq.getPhoneNumber()) == 1) {
                throw new BaseException(POST_USERS_EXISTS_PHONENUMBER);
            }

            //사용자 닉네임 중복 체크
            if (userDao.checkNickName(kakaoUserReq.getNickName()) == 1) {
                throw new BaseException(DUPLICATED_NICKNAME);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.updateKakaoUser(userId, kakaoUserReq);

        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    public void insertUserPrivacy(long userId) throws BaseException {
        try {
            // 개인정보 처리 방침 동의가 이미 유효한 경우
            if (userDao.getUserPrivacy(userId) == 1) {
                throw new BaseException(STILL_ENABLE_PRIVACY);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            userDao.insertUserPrivacy(userId);

        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
