package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


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
    POST_USERS_EMPTY_EMAIL(false, 2020, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2021, "이메일 형식을 확인해주세요."),
    POST_USERS_OVER_LENGTH_EMAIL(false, 2022, "이메일 길이를 확인해주세요."),


    POST_USERS_EMPTY_PHONENUMBER(false,2030,"휴대폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONENUMBER(false,2031,"잘못된 휴대폰 번호입니다."),

    POST_USERS_EMPTY_NAME(false,2040,"이름을 입력해주세요."),
    POST_USERS_OVER_LENGTH_NAME(false,2041,"이름은 최대 20자까지 입력해주세요."),

    POST_USERS_EMPTY_PASSWORD(false,2050,"비밀 번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false,2051,"비밀 번호는 특수문자 포함 6자 이상입니다."),

    POST_USERS_EMPTY_BIRTHDAY(false,2060,"생일을 입력해주세요."),
    POST_USERS_INVALID_BIRTHDAY(false,2061,"잘못된 생일 형식입니다."),

    POST_USERS_EMPTY_PRIVACY(false,2070,"개인정보 약관 동의가 필요합니다."),
    POST_USERS_INVALID_PRIVACY(false,2071,"잘못된 개인정보 약관 동의입니다."),

    POST_USERS_EMPTY_NICKNAME(false,2080,"사용자 이름을 입력해주세요."),
    POST_USERS_OVER_LENGTH_NICKNAME(false,2081,"사용자 이름은 최대 20자까지 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false,2082,"아이디는 영어, 숫자, '_', '.'만 사용 가능합니다."),

    // [PATCH] /users/{userId}/usernames
    PATCH_USERS_EMPTY_USERNAME(false,2100,"이름을 입력해주세요."),
    PATCH_USERS_OVERFLOW_USERNAME(false,2101,"이름은 최대 20자까지 입력해주세요."),

    // [PATCH] /users/{userId}/nicknames
    PATCH_USERS_EMPTY_NICKNAME(false,2110,"사용자 이름을 입력해주세요."),
    PATCH_USERS_OVERFLOW_NICKNAME(false,2111,"사용자 이름은 최대 20자까지 입력해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3010, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 이메일이거나 비밀번호가 틀렸습니다."),

    POST_USERS_EXISTS_PHONENUMBER(false,3020,"중복된 휴대폰 번호입니다."),

    DUPLICATED_NICKNAME(false,3030,"중복된 사용자 이름입니다."),


    FAILED_TO_MODIFY_USERNAME(false,3040,"14일 이내에 이름 변경은 최대 2번입니다."),
    FAILED_TO_MODIFY_NICKNAME(false,3050,"14일 이내에 사용자 이름 변경은 최대 2번입니다."),



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
