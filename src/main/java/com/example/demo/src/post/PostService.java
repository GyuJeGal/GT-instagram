package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.CreatePostReq;
import com.example.demo.src.post.model.GetPostCommentsRes;
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

            // 최대 인덱스를 초과했을때
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

    public void setPostLike(long userId, long postId) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }
            
            // 이미 좋아요 상태인 경우
            if(postDao.checkPostLike(userId, postId) == 1) {
                throw new BaseException(ALREADY_POST_LIKE);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.setPostLike(userId, postId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePostLike(long userId, long postId) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }

            // 이미 좋아요 취소 상태인 경우
            if(postDao.checkPostLike(userId, postId) == 0) {
                throw new BaseException(ALREADY_POST_UNLIKE);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.deletePostLike(userId, postId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetPostCommentsRes getPostComments(long userId, long postId, int pageIndex) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }
            int countPostComments = postDao.countPostComments(postId);
            int maxIndex = countPostComments/10 + 1;

            // 최대 인덱스를 초과했을때
            if(pageIndex > maxIndex) {
                throw new BaseException(INVALID_PAGE_INDEX);
            }

        } catch (Exception exception) {
            throw exception;
        }

        try {
            return postDao.getPostComments(userId, postId, pageIndex);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setPostComment(long userId, long postId, String contents) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }

        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.setPostComment(userId, postId, contents);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void setCommentLike(long userId, long commentId) throws BaseException {
        try {
            // 댓글이 존재하지 않는 경우
            if(postDao.checkCommentExists(commentId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_COMMENT);
            }

            // 이미 좋아요 상태인 경우
            if(postDao.checkCommentLike(userId, commentId) == 1) {
                throw new BaseException(ALREADY_COMMENT_LIKE);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.setCommentLike(userId, commentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCommentLike(long userId, long commentId) throws BaseException {
        try {
            // 댓글이 존재하지 않는 경우
            if(postDao.checkCommentExists(commentId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_COMMENT);
            }

            // 이미 좋아요 취소 상태인 경우
            if(postDao.checkCommentLike(userId, commentId) == 0) {
                throw new BaseException(ALREADY_COMMENT_UNLIKE);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.deleteCommentLike(userId, commentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updatePost(long userId, long postId, String contents) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }

            // 본인 게시글이 아닌 경우
            if(postDao.getUserByPost(postId) != userId) {
                throw new BaseException(FAILED_TO_UPDATE_POST);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.updatePost(postId, contents);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePost(long userId, long postId) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }

            // 본인 게시글이 아닌 경우
            if(postDao.getUserByPost(postId) != userId) {
                throw new BaseException(FAILED_TO_DELETE_POST);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.deletePost(postId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteComment(long userId, long commentId) throws BaseException {
        try {
            // 댓글이 존재하지 않는 경우
            if(postDao.checkCommentExists(commentId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_COMMENT);
            }

            // 본인 댓글이 아닌 경우
            if(postDao.getUserByComment(commentId) != userId) {
                throw new BaseException(FAILED_TO_DELETE_COMMENT);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.deleteComment(commentId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void reportPost(long userId, long postId, int reportType) throws BaseException {
        try {
            // 게시글이 존재하지 않는 경우
            if(postDao.checkPostExists(postId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_POST);
            }

            // 본인 게시글인 경우
            if(postDao.getUserByPost(postId) == userId) {
                throw new BaseException(FAILED_TO_REPORT_POST);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.reportPost(userId, postId, reportType);

            // 게시글 신고 횟수가 5번 이상일 때 게시글 invisible 처리
            if(postDao.countPostReports(postId) >= 5) {
                postDao.setPostInvisible(postId);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void reportPostComment(long userId, long commentId, int reportType) throws BaseException {
        try {
            // 댓글이 존재하지 않는 경우
            if(postDao.checkCommentExists(commentId) == 0) {
                throw new BaseException(FAILED_TO_SEARCH_COMMENT);
            }

            // 본인 댓글인 경우
            if(postDao.getUserByComment(commentId) == userId) {
                throw new BaseException(FAILED_TO_REPORT_COMMENT);
            }
        } catch (Exception exception) {
            throw exception;
        }

        try {
            postDao.reportPostComment(userId, commentId, reportType);

            // 댓글 신고 횟수가 5번 이상일 때 게시글 invisible 처리
            if(postDao.countPostCommentReports(commentId) >= 5) {
                postDao.setPostCommentInvisible(commentId);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
