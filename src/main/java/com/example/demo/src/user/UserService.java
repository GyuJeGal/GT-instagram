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

//
//    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
//        //이메일 존재하는지 체크
//        if(userDao.checkEmail(postLoginReq.getEmail()) == 0) {
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//
//        User user = userDao.getPwd(postLoginReq);
//
//        //비밀 번호 암호화
//        String encryptedPassword;
//        try {
//            encryptedPassword = new SHA256().encrypt(postLoginReq.getPassword());
//        } catch (Exception exception) {
//            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
//        }
//        if(user.getPassword().equals(encryptedPassword)) {
//            long userId = user.getUserId();
//            String jwt = jwtService.createJwt(userId);
//            return new PostLoginRes(jwt, userId);
//        }
//        else {
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//
//
//
//    }
//
//    public GetUserInfo getUser(long userId) throws BaseException {
//        try {
//            GetUserInfo getUserInfo = userDao.getUser(userId);
//            return getUserInfo;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void deleteUser(long userId) throws BaseException {
//        try {
//             userDao.deleteUser(userId);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public GetUserDetail getUserDetail(long userId) throws BaseException {
//        try {
//            GetUserDetail getUserDetail = userDao.getUserDetail(userId);
//            return getUserDetail;
//
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void modifyUserProfile(long userId, String imgUrl) throws BaseException {
//        try {
//            userDao.modifyUserProfile(userId, imgUrl);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//
//    }
//
//    public void modifyUserName(long userId, String userName) throws BaseException {
//        try {
//            userDao.modifyUserName(userId, userName);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void modifyUserEmail(long userId, String email) throws BaseException {
//        try {
//            userDao.modifyUserEmail(userId, email);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void modifyUserBirthDay(long userId, String birthDay) throws BaseException {
//        try {
//            userDao.modifyUserBirthDay(userId, birthDay);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void modifyUserGender(long userId, Character gender) throws BaseException {
//        try {
//            userDao.modifyUserGender(userId, gender);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public void modifyUserPhoneNumber(long userId, String phoneNumber) throws BaseException {
//        try {
//            userDao.modifyUserPhoneNumber(userId, phoneNumber);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public GetBasketProduct getBasketProducts(long userId) throws BaseException {
//        try {
//             GetBasketProduct getBasketProduct = userDao.getBasketProducts(userId);
//             return getBasketProduct;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<GetOrderList> getOrderList(long userId) throws BaseException {
//        try {
//            List<GetOrderList> getOrderLists = userDao.getOrderList(userId);
//            return getOrderLists;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<GetLikeProduct> getLikeProducts(long userId) throws BaseException {
//        try {
//            List<GetLikeProduct> getLikeProductList = userDao.getLikeProducts(userId);
//            return getLikeProductList;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<GetLikeOnlineClasses> getLikeOnlineClasses(long userId) throws BaseException {
//        try {
//            List<GetLikeOnlineClasses> getLikeOnlineClasses = userDao.getLikeOnlineClasses(userId);
//            return getLikeOnlineClasses;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<GetLikeOfflineClasses> getLikeOfflineClasses(long userId) throws BaseException {
//        try {
//            List<GetLikeOfflineClasses> getLikeOfflineClasses = userDao.getLikeOfflineClasses(userId);
//            return getLikeOfflineClasses;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public KakaoUser kakaoLogIn(String code) throws BaseException {
//        String accessToken = "";
//        String refreshToken = "";
//        String reqUrl = "https://kauth.kakao.com/oauth/token";
//
//        try {
//            URL url = new URL(reqUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
//
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//
//            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
//            StringBuilder sb = new StringBuilder();
//            sb.append("grant_type=authorization_code");
//            sb.append("&client_id=ce6eb7f79713e251c5d2932d6b45b0e7"); // TODO REST_API_KEY 입력
//            sb.append("&redirect_uri=http://localhost:9000/users/kakao-login"); // TODO 인가코드 받은 redirect_uri 입력
//            sb.append("&code=" + code);
//            bw.write(sb.toString());
//            bw.flush();
//
//            //결과 코드가 200이라면 성공
//            int responseCode = connection.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line = "";
//            String result = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println("response body : " + result);
//
//            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(result);
//
//            accessToken = element.getAsJsonObject().get("access_token").getAsString();
//            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
//
//            System.out.println("access_token : " + accessToken);
//            System.out.println("refresh_token : " + refreshToken);
//
//            br.close();
//            bw.close();
//
//
//            Long userId = getUserByToken(accessToken);
//
//            if(userDao.checkUserId(userId) == 0) {
//                throw new BaseException(INVALID_KAKAO_USER);
//            }
//
//            String jwt = jwtService.createJwt(userId);
//            return new KakaoUser(userId, jwt);
//
//        } catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//
//    }
//
//    public Long getUserByToken(String accessToken) {
//        // HttpHeader 오브젝트 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
//        RestTemplate rt = new RestTemplate();
//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
//
//        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoProfileRequest,
//                String.class
//        );
//        JsonParser parser = new JsonParser();
//        JsonElement element = parser.parse(response.getBody());
//
//        System.out.println(element.toString());
//
//
//        Long id = element.getAsJsonObject().get("id").getAsLong();
//
//        System.out.println("id : " + id);
//
//        return id;
//
//    }
//

}
