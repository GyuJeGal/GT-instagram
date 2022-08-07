package com.example.demo.src.admin;

import com.example.demo.src.admin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AdminDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<UserInfoRes> getUsers(UserInfoReq userInfoReq) {
        List<UserInfoRes> userInfoResList;

        if(userInfoReq.getUserName() == null) {
            if(userInfoReq.getNickName() == null) {
                if(userInfoReq.getCreateAt() == null) {
                    // 이름 X, 아이디 X, 회원 가입 날짜 X, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId ,userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")));

                    }
                    // 이름 X, 아이디 X, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId , userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where status = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getStatus());
                    }

                }
                else {
                    // 이름 X, 아이디 X, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId , userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getCreateAt());
                    }
                    // 이름 X, 아이디 X, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
            }
            else {
                if(userInfoReq.getCreateAt() == null) {
                    // 이름 X, 아이디 O, 회원 가입 날짜 X, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getNickName());
                    }
                    // 이름 X, 아이디 O, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ?and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 X, 아이디 O, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 X, 아이디 O, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }

            }
        }
        else {
            if(userInfoReq.getNickName() == null) {
                if(userInfoReq.getCreateAt() == null) {
                    // 이름 O, 아이디 X, 회원 가입 날짜 X, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ?\n" +
                                "order by (userId) desc";

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getUserName());
                    }
                    // 이름 O, 아이디 X, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 O, 아이디 X, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 X, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }

            }
            else {
                if(userInfoReq.getCreateAt() == null) {
                    // 이름 O, 아이디 O, 회원 가입 날짜 X, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 O, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 O, 아이디 O, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 O, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userId, userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getLong("userId"),
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }

            }
            
        }
        return userInfoResList;
    }

    public UserDetail getUserDetail(long userId) {
        String getUserDetailQuery = "select * from User where userId = ?";

        UserDetail userDetail = this.jdbcTemplate.queryForObject(getUserDetailQuery, (rs, rowNum) -> new UserDetail(
                rs.getLong("userId"),
                rs.getString("userName"),
                rs.getString("profileImg"),
                rs.getString("userIntro"),
                rs.getString("webSite"),
                rs.getString("nickName"),
                rs.getInt("status"),
                rs.getString("createAt"),
                rs.getString("updateAt"),
                rs.getString("loginAt"),
                rs.getBoolean("openStatus")), userId );

        return userDetail;
    }

    public void setUserBlock(long userId) {
        String updateQuery = "update User set status = 2 where userId = ?";

        this.jdbcTemplate.update(updateQuery, userId);
    }

    public List<PostReportInfo> getPostReports() {
        String getPostReportsQuery = "select PR.postReportId, PR.postId, U.nickName, PR.createAt,\n" +
                "       case\n" +
                "           when PR.reportType = 1 then '스팸'\n" +
                "           when PR.reportType = 2 then'나체 이미지 또는 성적 행위'\n" +
                "           when PR.reportType = 3 then'마음에 들지 않습니다'\n" +
                "           when PR.reportType = 4 then'사기 또는 거짓'\n" +
                "           when PR.reportType = 5 then'혐오 발언 또는 상징'\n" +
                "           when PR.reportType = 6 then'거짓 정보'\n" +
                "           when PR.reportType = 7 then'따돌림 또는 괴롭힘'\n" +
                "           when PR.reportType = 8 then'폭력 또는 위험한 단체'\n" +
                "           else '지식재산권 침해' end as contents\n" +
                "from PostReport PR\n" +
                "inner join Post P using(postId)\n" +
                "inner join User U on P.userId = U.userId\n" +
                "where status = 1 " +
                "order by (postReportId) desc";

        return this.jdbcTemplate.query(getPostReportsQuery, (rs, rowNum) -> new PostReportInfo(
                rs.getLong("postReportId"),
                rs.getLong("postId"),
                rs.getString("nickName"),
                rs.getString("contents"),
                rs.getString("createAt")));
    }

    public List<CommentReportInfo> getCommentReports() {
        String getPostReportsQuery = "select PCR.commentReportId, PC.postCommentId, U.nickName, PCR.createAt,\n" +
                "       case\n" +
                "           when PCR.reportType = 1 then '스팸'\n" +
                "           when PCR.reportType = 2 then'나체 이미지 또는 성적 행위'\n" +
                "           when PCR.reportType = 3 then'혐오 발언 또는 상징'\n" +
                "           when PCR.reportType = 4 then'폭력 또는 위험한 단체'\n" +
                "           when PCR.reportType = 5 then'불법 또는 규제 상품 판매'\n" +
                "           when PCR.reportType = 6 then'따돌림 또는 괴롭힘'\n" +
                "           when PCR.reportType = 7 then'지식재산권 침해'\n" +
                "           when PCR.reportType = 8 then'거짓 정보'\n" +
                "           else '자살, 자해 및 섭식 장애' end as contents\n" +
                "from PostCommentReport PCR\n" +
                "inner join PostComment PC using(postCommentId)\n" +
                "inner join User U on PC.userId = U.userId\n" +
                "where status = 1 " +
                "order by (commentReportId) desc";

        return this.jdbcTemplate.query(getPostReportsQuery, (rs, rowNum) -> new CommentReportInfo(
                rs.getLong("commentReportId"),
                rs.getLong("postCommentId"),
                rs.getString("nickName"),
                rs.getString("contents"),
                rs.getString("createAt")));
    }

    public void deletePostReport(long reportId) {
        String deleteQuery = "delete from PostReport where postReportId = ?";

        this.jdbcTemplate.update(deleteQuery, reportId);
    }

    public void deleteCommentReport(long reportId) {
        String deleteQuery = "delete from PostCommentReport where commentReportId = ?";

        this.jdbcTemplate.update(deleteQuery, reportId);
    }
}
