package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public long createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (userName, nickName, phoneNumber, password, birthDay) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getNickName(), postUserReq.getPhoneNumber()
                , postUserReq.getPassword(), postUserReq.getBirthDay()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String getUserIdQuery = "select last_insert_id()";
        long userId = this.jdbcTemplate.queryForObject(getUserIdQuery,long.class);

        String insertUserPrivacyQuery = "insert into UserPrivacy (userId) VALUES (?)";
        this.jdbcTemplate.update(insertUserPrivacyQuery, userId);

        return userId;
    }

    public int checkPhoneNumber(String phoneNumber) {
        String checkPhoneNumberQuery = "select exists (select userId from User where phoneNumber = ?)";
        String checkParam = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkPhoneNumberQuery, int.class, checkParam);
    }

    public int checkNickName(String nickName) {
        String checkPhoneNumberQuery = "select exists (select userId from User where nickName = ?)";
        String checkParam = nickName;
        return this.jdbcTemplate.queryForObject(checkPhoneNumberQuery, int.class, checkParam);
    }

    public void modifyUserProfile(long userId, PatchUserReq patchUserReq) {
        String updateQuery = "update User set profileImg = ?, webSite = ?, userIntro = ? where userId = ?";
        Object[] updateParams = new Object[] {patchUserReq.getProfileImg(), patchUserReq.getWebSite(),
                patchUserReq.getUserIntro(), userId};

        this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public GetUserInfo getUserInfo(long userId) {
        String getUserInfoQuery = "select profileImg, userName, nickName, webSite, userIntro\n" +
                "from User\n" +
                "where userId = ?";
        return this.jdbcTemplate.queryForObject(getUserInfoQuery, (rs, rowNum) -> new GetUserInfo(
                rs.getString("profileImg"),
                rs.getString("userName"),
                rs.getString("nickName"),
                rs.getString("webSite"),
                rs.getString("userIntro")), userId);
    }

    public int countModifyUserName(long userId) {
        String countModifyUserNameQuery = "select count(updateUserNameId)\n" +
                "from UpdateUserName\n" +
                "where userId = ? and TIMESTAMPDIFF(DAY, createAt, NOW()) < 14";

        return this.jdbcTemplate.queryForObject(countModifyUserNameQuery, int.class, userId);
    }

    public void modifyUserName(long userId, String userName) {
        String updateQuery = "update User set userName = ? where userId = ?";
        Object[] updateParams = new Object[] {userName, userId};
        this.jdbcTemplate.update(updateQuery, updateParams);

        String insertQuery = "insert into UpdateUserName (contents, userId) VALUES (?,?)";
        this.jdbcTemplate.update(insertQuery, updateParams);
    }

    public String getUserName(long userId) {
        String getUserNameQuery = "select userName from User where userId = ?";
        return this.jdbcTemplate.queryForObject(getUserNameQuery, String.class, userId);
    }

    public int countModifyNickName(long userId) {
        String countModifyUserNameQuery = "select count(updateNickNameId)\n" +
                "from UpdateNickName\n" +
                "where userId = ? and TIMESTAMPDIFF(DAY, createAt, NOW()) < 14";

        return this.jdbcTemplate.queryForObject(countModifyUserNameQuery, int.class, userId);
    }

    public void modifyNickName(long userId, String nickName) {
        String updateQuery = "update User set nickName = ? where userId = ?";
        Object[] updateParams = new Object[] {nickName, userId};
        this.jdbcTemplate.update(updateQuery, updateParams);

        String insertQuery = "insert into UpdateUserName (contents, userId) VALUES (?,?)";
        this.jdbcTemplate.update(insertQuery, updateParams);

    }

    public String getNickName(long userId) {
        String getNickNameQuery = "select nickName from User where userId = ?";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userId);
    }

    public Integer getUserOpenStatus(long userId) {
        String getNickNameQuery = "select openStatus from User where userId = ?";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, Integer.class, userId);
    }

    public void modifyUserStatus(long userId, Integer status) {
        String updateQuery = "update User set openStatus = ? where userId = ?";
        Object[] updateParams = new Object[] {status, userId};
        this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public void deleteUser(long userId) {
        // 회원 탈퇴: -1, 차단 계정: 0, 정상 회원: 1, 휴면 계정: 2, 카카오 회원 가입 대기 중: 3
        String updateQuery = "update User set status = -1 where userId = ?";
        this.jdbcTemplate.update(updateQuery, userId);
    }

    public int checkFollow(long userId, long followUserId) {
        String checkFollowQuery = "select exists (select followId from Follow where followingUserId = ? and followedUserId = ? and status >= 0)";
        Object[] params = new Object[] {userId, followUserId};
        return this.jdbcTemplate.queryForObject(checkFollowQuery, int.class, params);
    }

    public long getFollowId(long userId, long followUserId) {
        String getFollowIdQuery = "select followId from Follow where followingUserId = ? and followedUserId = ? and status >= 0";
        Object[] params = new Object[] {userId, followUserId};
        return this.jdbcTemplate.queryForObject(getFollowIdQuery, long.class, params);
    }

    public int getFollowStatus(long followId) {
        String getFollowStatusQuery = "select status from Follow where followId = ?";
        return this.jdbcTemplate.queryForObject(getFollowStatusQuery, int.class, followId);
    }

    public void followUser(long userId, long followUserId) {
        // 팔로우할 대상이 공개 계정인 경우
        if(getUserOpenStatus(followUserId) == 1) {
            String insertQuery = "insert into Follow (followingUserId, followedUserId) VALUES (?,?)";
            Object[] params = new Object[] {userId, followUserId};
            this.jdbcTemplate.update(insertQuery, params);
        }
        // 팔로우할 대상이 비공개 계정인 경우
        else {
            String insertQuery = "insert into Follow (followingUserId, followedUserId, status) VALUES (?,?,?)";
            Object[] params = new Object[] {userId, followUserId, 0};
            this.jdbcTemplate.update(insertQuery, params);
        }
    }

    public void unfollowUser(long followId) {
        // 팔로우 취소: -1, 팔로우 승인 대기 중: 0, 팔로우 중: 1
        String updateQuery = "update Follow set status = -1 where followId = ?";
        this.jdbcTemplate.update(updateQuery, followId);
    }

    public List<GetFollow> getFollows(long userId) {
        String getFollowsQuery = "select F.followingUserId, F.followId, U.profileImg, U.userName, U.nickName\n" +
                "from Follow F\n" +
                "inner join User U on F.followingUserId = U.userId\n" +
                "where followedUserId = ? and F.status = 0 order by (F.followId) desc";

        List<GetFollow> getFollowList = this.jdbcTemplate.query(getFollowsQuery, (rs, rowNum) -> new GetFollow(
                rs.getLong("followingUserId"),
                rs.getLong("followId"),
                rs.getString("profileImg"),
                rs.getString("userName"),
                rs.getString("nickName")), userId);

        return getFollowList;
    }

    public long getUserIdForFollow(long followId) {
        String getUserIdQuery = "select followedUserId from Follow where followId = ?";
        return this.jdbcTemplate.queryForObject(getUserIdQuery, long.class, followId);
    }

    public void acceptFollow(long followId) {
        // 팔로우 취소: -1, 팔로우 승인 대기 중: 0, 팔로우 중: 1
        String updateQuery = "update Follow set status = 1 where followId = ?";
        this.jdbcTemplate.update(updateQuery, followId);
    }

    public void rejectFollow(long followId) {
        // 팔로우 취소: -1, 팔로우 승인 대기 중: 0, 팔로우 중: 1
        String updateQuery = "update Follow set status = -1 where followId = ?";
        this.jdbcTemplate.update(updateQuery, followId);
    }

    public int checkUser(String loginId) {
        String checkUserQuery = "select exists (select userId from User where nickName = ?)";
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, loginId);
    }

    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userId, password from User where nickName = ?";
        String param = postLoginReq.getLoginId();
        return this.jdbcTemplate.queryForObject(getPwdQuery, (rs, rowNum) ->
                new User(rs.getLong("userId"), rs.getString("password")), param);
    }

    public int checkUserId(Long userId) {
        String checkQuery = "select exists (select userId from User where userId=? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userId);
    }

    public void createKakaoUser(Long userId) {
        String createKakaoUserQuery = "insert into User (userId) VALUES (?)";

        this.jdbcTemplate.update(createKakaoUserQuery, userId);
    }

    public void updateKakaoUser(Long userId, KakaoUserReq kakaoUserReq) {
        String updateQuery = "update User set status = 1 and userName = ? and nickName = ? and phoneNumber = ? and birthDay = ? where userId = ?";
        Object[] params = new Object[] {kakaoUserReq.getUserName(), kakaoUserReq.getNickName(),
                kakaoUserReq.getPhoneNumber(), kakaoUserReq.getBirthDay(), userId};

        this.jdbcTemplate.update(updateQuery, params);

        String insertUserPrivacyQuery = "insert into UserPrivacy (userId) VALUES (?)";
        this.jdbcTemplate.update(insertUserPrivacyQuery, userId);
    }

    public void checkUserPrivacy() {
        String updateQuery = "update UserPrivacy set status = 0 where TIMESTAMPDIFF(Year, createAt, NOW()) >= 1 and status = 1";
        this.jdbcTemplate.update(updateQuery);
    }


    public int getUserPrivacy(long userId) {
        String getUserPrivacyQuery = "select exists (select userPrivacyId from UserPrivacy where userId=? and status = 1)";
        return this.jdbcTemplate.queryForObject(getUserPrivacyQuery, int.class, userId);
    }

    public void insertUserPrivacy(long userId) {
        String insertUserPrivacyQuery = "insert into UserPrivacy (userId) VALUES (?)";
        this.jdbcTemplate.update(insertUserPrivacyQuery, userId);
    }

    public int countUserPost(long userId) {
        String countUserPostQuery = "select count(postId) from Post where userId = ?";
        return this.jdbcTemplate.queryForObject(countUserPostQuery, int.class, userId);
    }

    public GetMyPageRes getMyPage(long userId, Integer pageIndex) {
        String getUserInfoQuery = "select U.nickName, U.profileImg, U.userName, U.userIntro, U.webSite, CP.countPosts,CF1.countFollower, CF2.countFollowing\n" +
                "from User U\n" +
                "left outer join (select userId, count(postId) as countPosts from Post group by (userId)) CP using(userId)\n" +
                "left outer join (select followedUserId, count(followId) as countFollower from Follow group by (followedUserId)) CF1 on CF1.followedUserId=U.userId\n" +
                "left outer join (select followingUserId, count(followId) as countFollowing from Follow group by (followingUserId)) CF2 on CF2.followingUserId=U.userId\n" +
                "where U.userId = ?";

        String getUserPostsQuery = "select P.postId, PI.imgUrl\n" +
                "from Post P\n" +
                "inner join (select postId, imgUrl from PostImg group by (postId)) PI using(postId)\n" +
                "where userId = ? order by(postId) desc limit 0, ?";

        int size = 9 * pageIndex;
        Object[] params = new Object[] {userId, size};


        List<Post> postList = this.jdbcTemplate.query(getUserPostsQuery, (rs, rowNum) -> new Post(
                rs.getLong("postId"),
                rs.getString("imgUrl")), params);

        GetMyPageRes getMyPageRes = this.jdbcTemplate.queryForObject(getUserInfoQuery, (rs, rowNum) -> new GetMyPageRes(
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("userName"),
                rs.getString("userIntro"),
                rs.getString("webSite"),
                rs.getInt("countPosts"),
                rs.getInt("countFollower"),
                rs.getInt("countFollowing"),
                postList), userId);

        return getMyPageRes;
    }

    public GetUserPage GetPublicUserPage(long userId, long visitUserId, int pageIndex) {
        String getUserInfoQuery = "select U.nickName, U.profileImg, U.userName, U.userIntro, U.webSite, CP.countPosts,CF1.countFollower, CF2.countFollowing\n" +
                "from User U\n" +
                "left outer join (select userId, count(postId) as countPosts from Post group by (userId)) CP using(userId)\n" +
                "left outer join (select followedUserId, count(followId) as countFollower from Follow group by (followedUserId)) CF1 on CF1.followedUserId=U.userId\n" +
                "left outer join (select followingUserId, count(followId) as countFollowing from Follow group by (followingUserId)) CF2 on CF2.followingUserId=U.userId\n" +
                "where U.userId = ?";

        String getIsFollowQuery = "select exists(select followId from Follow where followingUserId = ? and followedUserId = ? and status = 1)";
        Object[] GetIsFollowParams = new Object[] {userId, visitUserId};

        int followStatus;
        if(this.jdbcTemplate.queryForObject(getIsFollowQuery, int.class, GetIsFollowParams) == 1) {
            followStatus = 1; // 팔로우 중
        }
        else {
            followStatus = -1; // 언팔로우 상태
        }

        String getUserPostsQuery = "select P.postId, PI.imgUrl\n" +
                "from Post P\n" +
                "inner join (select postId, imgUrl from PostImg group by (postId)) PI using(postId)\n" +
                "where userId = ? order by(postId) desc limit 0, ?";

        int size = 9 * pageIndex;
        Object[] getUserPostsParams = new Object[] {visitUserId, size};


        List<Post> postList = this.jdbcTemplate.query(getUserPostsQuery, (rs, rowNum) -> new Post(
                rs.getLong("postId"),
                rs.getString("imgUrl")), getUserPostsParams);

        GetUserPage getUserPage = this.jdbcTemplate.queryForObject(getUserInfoQuery, (rs, rowNum) -> new GetUserPage(
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("userName"),
                rs.getString("userIntro"),
                rs.getString("webSite"),
                rs.getInt("countPosts"),
                rs.getInt("countFollower"),
                rs.getInt("countFollowing"),
                followStatus, postList), visitUserId);
        return getUserPage;
    }

    public GetUserPage GetPrivateUserPage(long userId, long visitUserId) {
        String getUserInfoQuery = "select U.nickName, U.profileImg, U.userName, CP.countPosts,CF1.countFollower, CF2.countFollowing\n" +
                "from User U\n" +
                "left outer join (select userId, count(postId) as countPosts from Post group by (userId)) CP using(userId)\n" +
                "left outer join (select followedUserId, count(followId) as countFollower from Follow group by (followedUserId)) CF1 on CF1.followedUserId=U.userId\n" +
                "left outer join (select followingUserId, count(followId) as countFollowing from Follow group by (followingUserId)) CF2 on CF2.followingUserId=U.userId\n" +
                "where U.userId = ?";

        String getIsFollowQuery = "select exists(select followId from Follow where followingUserId = ? and followedUserId = ? and status = 0)";
        Object[] GetIsFollowParams = new Object[] {userId, visitUserId};

        int followStatus;
        if(this.jdbcTemplate.queryForObject(getIsFollowQuery, int.class, GetIsFollowParams) == 1) {
            followStatus = 0; // 팔로우 요청 중
        }
        else {
            followStatus = -1; // 언팔로우 상태
        }

        return this.jdbcTemplate.queryForObject(getUserInfoQuery, (rs, rowNum) -> new GetUserPage(
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("userName"),
                rs.getInt("countPosts"),
                rs.getInt("countFollower"),
                rs.getInt("countFollowing"),
                followStatus), visitUserId);
    }

    public long getUserIdByPhoneNumber(String phoneNumber) {
        String getUserQuery = "select userId from User where phoneNumber = ?";
        return this.jdbcTemplate.queryForObject(getUserQuery, long.class, phoneNumber);
    }

    public void updatePassword(long userId, String password) {
        String updateQuery = "update User set password = ? where userId = ?";
        Object[] updateParams = new Object[] {password, userId};

        this.jdbcTemplate.update(updateQuery, updateParams);
    }
}
