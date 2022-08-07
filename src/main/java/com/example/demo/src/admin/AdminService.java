package com.example.demo.src.admin;

import com.example.demo.config.BaseException;
import com.example.demo.src.admin.model.UserDetail;
import com.example.demo.src.admin.model.UserInfoReq;
import com.example.demo.src.admin.model.UserInfoRes;
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
}
