package com.example.demo.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, 200, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_PHONENUMBER(false,2030,"휴대폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONENUMBER(false,2031,"잘못된 휴대폰 번호입니다."),

    POST_USERS_EMPTY_NAME(false,2040,"이름을 입력해주세요."),
    POST_USERS_OVER_LENGTH_NAME(false,2041,"이름은 최대 20자까지 입력해주세요."),

    POST_USERS_EMPTY_PASSWORD(false,2050,"비밀 번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false,2051,"비밀 번호는 특수문자 포함 6자 이상 20자리 이하입니다."),

    POST_USERS_EMPTY_BIRTHDAY(false,2060,"생일을 입력해주세요."),
    POST_USERS_INVALID_BIRTHDAY(false,2061,"잘못된 생일 형식입니다."),

    POST_USERS_EMPTY_PRIVACY(false,2070,"개인정보 약관 동의가 필요합니다."),
    POST_USERS_INVALID_PRIVACY(false,2071,"잘못된 개인정보 약관 동의입니다."),

    POST_USERS_EMPTY_NICKNAME(false,2080,"사용자 이름을 입력해주세요."),
    POST_USERS_OVER_LENGTH_NICKNAME(false,2081,"사용자 이름은 최대 20자까지 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false,2082,"아이디는 영어, 숫자, '_', '.'만 사용 가능합니다."),

    // [POST] /users/login
    POST_USERS_EMPTY_LOGIN_ID(false, 2090, "아이디를 입력해주세요."),
    POST_USERS_OVER_LENGTH_LOGIN_ID(false, 2091, "아이디는 3자리 이상 20자리 이하입니다."),

    // [PATCH] /users/{userId}/usernames
    PATCH_USERS_EMPTY_USERNAME(false,2100,"이름을 입력해주세요."),
    PATCH_USERS_OVER_LENGTH_USERNAME(false,2101,"이름은 최대 20자까지 입력해주세요."),

    // [PATCH] /users/{userId}/nicknames
    PATCH_USERS_EMPTY_NICKNAME(false,2110,"사용자 이름을 입력해주세요."),
    PATCH_USERS_OVER_LENGTH_NICKNAME(false,2111,"사용자 이름은 최대 20자까지 입력해주세요."),

    // [PATCH] /users/{userId}/open-status
    PATCH_USERS_EMPTY_STATUS(false,2120,"공개/비공개 여부를 입력해주세요."),

    // [POST] /users/{userId}/{followUserId}/follows
    POST_USERS_INVALID_FOLLOW(false,2130,"본인에게는 팔로우 신청이 불가합니다."),
    
    // [PATCH] /users/{userId}/{unfollowUserId}/follows
    PATCH_USERS_INVALID_UNFOLLOW(false,2140,"본인에게는 팔로우 취소가 불가합니다."),

    // [PATCH] /users/password
    PATCH_USERS_EMPTY_PHONENUMBER(false,2150,"휴대폰 번호를 입력해주세요."),
    PATCH_USERS_INVALID_PHONENUMBER(false,2151,"잘못된 휴대폰 번호입니다."),

    PATCH_USERS_EMPTY_PASSWORD(false,2160,"비밀번호를 입력해주세요."),
    PATCH_USERS_INVALID_PASSWORD(false,2161,"잘못된 비밀번호입니다."),

    //[POST] /posts/{userId}
    POST_POSTS_OVER_LENGTH_CONTENTS(false,2170,"게시글 내용은 최대 1000자입니다."),
    POST_POSTS_EMPTY_IMG_LIST(false,2171,"사진 리스트가 필요합니다."),
    POST_POSTS_OVERSIZE_IMG_LIST(false,2172,"사진은 최대 10장입니다."),

    //[POST] /posts/{userId}/{postId}/comments
    POST_POSTS_EMPTY_COMMENT(false,2180,"댓글 내용을 입력해주세요."),
    POST_POSTS_OVER_LENGTH_COMMENT(false,2185,"댓글은 최대 200자 입력해주세요."),
    
    // 신고 관련 요청 오류
    INVALID_REPORT_TYPE(false,2190,"잘못된 신고 형식입니다."),
    

    // 페이징 관련 요청 오류
    EMPTY_PAGE_INDEX(false,2200,"페이지 인덱스 값이 필요합니다."),
    INVALID_PAGE_INDEX(false,2201,"잘못된 페이지 인덱스입니다."),




    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    FAILED_TO_LOGIN(false,3010,"없는 아이디이거나 비밀번호가 틀렸습니다."),


    POST_USERS_EXISTS_PHONENUMBER(false,3020,"중복된 휴대폰 번호입니다."),

    DUPLICATED_NICKNAME(false,3030,"중복된 사용자 이름입니다."),


    FAILED_TO_MODIFY_USERNAME(false,3040,"14일 이내에 이름 변경은 최대 2번입니다."),
    FAILED_TO_MODIFY_NICKNAME(false,3050,"14일 이내에 사용자 이름 변경은 최대 2번입니다."),


    ALREADY_PUBLIC_ACCOUNT(false,3060,"이미 공개 계정 상태입니다."),
    ALREADY_PRIVATE_ACCOUNT(false,3070,"이미 비공개 계정 상태입니다."),
    
    ALREADY_FOLLOW(false,3080,"이미 팔로우 상태입니다."),
    ALREADY_FOLLOW_REQUEST(false,3081,"이미 팔로우 요청 상태입니다."),

    ALREADY_UNFOLLOW(false,3090,"이미 언팔로우 상태입니다."),
    
    FAILED_TO_ACCEPT_FOLLOW(false,3100,"잘못된 팔로우 요청 승인입니다."),

    FAILED_TO_REJECT_FOLLOW(false,3110,"잘못된 팔로우 요청 취소입니다."),

    STILL_ENABLE_PRIVACY(false,3120,"이전의 개인정보 처리 방침 동의가 아직 유효합니다."),
    
    NEED_USER_PRIVACY(false,3130,"개인정보 처리 방침 동의가 필요합니다."),

    FAILED_TO_SEARCH_USER(false,3140,"해당 사용자를 찾을 수 없습니다."),

    FAILED_TO_SEARCH_POST(false,3150,"게시글을 찾을 수 없습니다."),
    ALREADY_POST_LIKE(false,3160,"이미 게시글 좋아요 상태입니다."),
    ALREADY_POST_UNLIKE(false,3165,"이미 게시글 좋아요 취소 상태입니다."),

    FAILED_TO_SEARCH_COMMENT(false,3170,"댓글을 찾을 수 없습니다."),
    ALREADY_COMMENT_LIKE(false,3180,"이미 댓글 좋아요 상태입니다."),
    ALREADY_COMMENT_UNLIKE(false,3185,"이미 댓글 좋아요 취소 상태입니다."),

    FAILED_TO_UPDATE_POST(false,3190,"게시글 수정에 실패하였습니다."),
    FAILED_TO_DELETE_POST(false,3200,"게시글 삭제에 실패하였습니다."),

    FAILED_TO_DELETE_COMMENT(false,3200,"댓글 삭제에 실패하였습니다."),
    
    FAILED_TO_REPORT_POST(false,3210,"게시글 신고에 실패하였습니다."),
    FAILED_TO_REPORT_COMMENT(false,3220,"댓글 신고에 실패하였습니다."),

    INVALID_KAKAO_USER(false,3500,"잘못된 카카오 로그인입니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),


    SEND_SMS_ERROR(false, 5000, "문자 인증 전송을 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
