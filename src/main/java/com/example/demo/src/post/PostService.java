package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.CreatePostReq;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;

    }

    public List<GetPostRes> getPosts(long userId, int pageIndex) throws BaseException {
        try {
            // 개인정보 처리 방침 동의가 만료된 경우
            if(postDao.getUserPrivacy(userId) == 0) {
                throw new BaseException(NEED_USER_PRIVACY);
            }

            int countUserPosts = postDao.countGetPosts(userId);
            int maxIndex = countUserPosts/10 + 1;

            if(pageIndex > maxIndex) {
                throw new BaseException(INVALID_PAGE_INDEX);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            return postDao.getPosts(userId, pageIndex);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void createPost(long userId, CreatePostReq createPostReq) throws BaseException {
        try {
            postDao.createPost(userId, createPostReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
