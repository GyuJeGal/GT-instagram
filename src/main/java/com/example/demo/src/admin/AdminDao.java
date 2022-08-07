package com.example.demo.src.admin;

import com.example.demo.src.admin.model.UserInfoReq;
import com.example.demo.src.admin.model.UserInfoRes;
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
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")));

                    }
                    // 이름 X, 아이디 X, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where status = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getStatus());
                    }

                }
                else {
                    // 이름 X, 아이디 X, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getCreateAt());
                    }
                    // 이름 X, 아이디 X, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
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
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ?\n" +
                                "order by (userId) desc";
                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getNickName());
                    }
                    // 이름 X, 아이디 O, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ?and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 X, 아이디 O, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 X, 아이디 O, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getNickName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
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
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ?\n" +
                                "order by (userId) desc";

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), userInfoReq.getUserName());
                    }
                    // 이름 O, 아이디 X, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 O, 아이디 X, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 X, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
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
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 O, 회원 가입 날짜 X, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }

                }
                else {
                    // 이름 O, 아이디 O, 회원 가입 날짜 O, 회원 상태 X
                    if(userInfoReq.getStatus() == null) {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getCreateAt()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
                                rs.getString("userName"),
                                rs.getString("nickName"),
                                rs.getString("createAt"),
                                rs.getString("status")), params);
                    }
                    // 이름 O, 아이디 O, 회원 가입 날짜 O, 회원 상태 O
                    else {
                        String getUsersQuery = "select userName, nickName, DATE_FORMAT(createAt, '%y.%m.%d') as createAt, if(status=1, '활성화', if(status=-1, '탈퇴', if(status=0, '휴먼', '정지'))) as status\n" +
                                "from User\n" +
                                "where userName = ? and nickName = ? and  DATE_FORMAT(createAt, '%Y%m%d') = ? and status = ?\n" +
                                "order by (userId) desc";
                        Object[] params = new Object[] {userInfoReq.getUserName(), userInfoReq.getNickName(), userInfoReq.getCreateAt(), userInfoReq.getStatus()};

                        userInfoResList = this.jdbcTemplate.query(getUsersQuery, (rs, rowNum) -> new UserInfoRes(
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
}
