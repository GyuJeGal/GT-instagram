# 1. 시스템 구성도
![시스템 구성도](https://user-images.githubusercontent.com/69112154/183280976-a14119f5-ecce-4772-8074-cd92e27f4305.jpg)
# 2. 폴더 구조 및 기능 설명
### 1) config : jwt secret 키 값, 오류 코드 및 message, Swagger 설정
### 2) src : 메인 로직 처리
- 도메인 별로 패키지 구성(User, Post)
#### a) src.user : /users 아래의 요청을 처리
- 사용자 관련 정보 조회 및 수정 처리
- 개인정보 처리 방침 동의를 체크하는 Scheduler 클래스 포함
#### b) src.post : /posts 아래의 요청을 처리
- 게시글 관련 정보 조회 및 수정 처리
#### c) src.admin : /admin 아래의 요청을 처리
- 관리자 페이지 조회 및 수정, 삭제 처리
### 3) utils : jwt 값, 비밀번호 암호화 처리
