package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.src.admin.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class AdminService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AdminDao adminDao;
    private final JwtService jwtService;

    public AdminService(AdminDao adminDao, JwtService jwtService) {
        this.adminDao = adminDao;
        this.jwtService = jwtService;
    }

    public List<UserInfoRes> getUsers(UserInfoReq userInfoReq) throws BaseException {
        try {
            return adminDao.getUsers(userInfoReq);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public UserDetail getUserDetail(long userId) throws BaseException {
        try {
            return adminDao.getUserDetail(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setUserBlock(long userId) throws BaseException {
        try {
            adminDao.setUserBlock(userId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PostReportInfo> getPostReports() throws BaseException {
        try {
            return adminDao.getPostReports();
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<CommentReportInfo> getCommentReports() throws BaseException {
        try {
            return adminDao.getCommentReports();
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePostReport(long reportId) throws BaseException {
        try {
            adminDao.deletePostReport(reportId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCommentReport(long reportId) throws BaseException {
        try {
            adminDao.deleteCommentReport(reportId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePost(long postId) throws BaseException {
        try {
            adminDao.deletePost(postId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteComment(long commentId) throws BaseException {
        try {
            adminDao.deleteComment(commentId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
