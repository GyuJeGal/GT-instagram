package com.example.demo.src.post;

import com.example.demo.src.post.model.CreatePostReq;
import com.example.demo.src.post.model.GetPostCommentsRes;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.PostComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getUserPrivacy(long userId) {
        String getUserPrivacyQuery = "select exists (select userPrivacyId from UserPrivacy where userId=? and status = 1)";
        return this.jdbcTemplate.queryForObject(getUserPrivacyQuery, int.class, userId);
    }

    public int countGetPosts(long userId) {
        String getCountPostsQuery = "select count(postId) from Post where userId in (select followedUserId from Follow where followingUserId = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(getCountPostsQuery, int.class, userId);
    }

    public List<GetPostRes> getPosts(long userId, int pageIndex) {
        String getPostsQuery = "select P.userId, U.nickName, U.profileImg, P.postId, if(PL1.postLike=1, true, false) as postLike, if(isNull(PL2.countLike), 0, PL2.countLike) as countLike, P.contents,if(isNull(PC.countComment), 0, PC.countComment) as countComment,\n" +
                "       case\n" +
                "           when TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP) >= 30 then DATE_FORMAT(P.createAt, '%c월 %e일')\n" +
                "           when TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP), '일')\n" +
                "           when TIMESTAMPDIFF(HOUR , P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(Hour, P.createAt, CURRENT_TIMESTAMP), '시간')\n" +
                "           when TIMESTAMPDIFF(MINUTE, P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(MINUTE, P.createAt, CURRENT_TIMESTAMP), '분')\n" +
                "           else concat(TIMESTAMPDIFF(SECOND, P.createAt, CURRENT_TIMESTAMP), '초') end as createAt\n" +
                "from Post P\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select postId, status as postLike from PostLike where userId = ? and status = 1) PL1 using(postId)\n" +
                "left outer join (select postId, count(postLikeId) as countLike from PostLike group by (postId)) PL2 using(postId)\n" +
                "left outer join (select postId, count(postCommentId) as countComment from PostComment group by (postId)) PC using(postId)\n" +
                "where userId in (select followedUserId from Follow where followingUserId = ? and status = 1) and P.status = 1 order by (postId) desc limit 0,?";

        int size = 10 * pageIndex;
        Object[] params = new Object[] {userId, userId, size};

        List<Long> postIdList = this.jdbcTemplate.query(getPostsQuery, (rs, rowNum) -> new Long(rs.getLong("postId")), params);

        List<GetPostRes> getPostResList = this.jdbcTemplate.query(getPostsQuery, (rs, rowNum) -> new GetPostRes(
                rs.getLong("userId"),
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getLong("postId"),
                rs.getBoolean("postLike"),
                rs.getInt("countLike"),
                rs.getString("contents"),
                rs.getInt("countComment"),
                rs.getString("createAt")), params);

        String getPostImgQuery = "select imgUrl from PostImg where postId = ?";

        for(int i = 0; i < getPostResList.size(); i++)  {
            List<String> postImgList = this.jdbcTemplate.query(getPostImgQuery, (rs, rowNum) -> rs.getString("imgUrl"), postIdList.get(i));

            getPostResList.get(i).setPostImgList(postImgList);
        }

        return getPostResList;
    }

    public void createPost(long userId, CreatePostReq createPostReq) {
        String insertQuery1 = "insert into Post (userId, contents) VALUES (?,?)";
        Object[] params1 = new Object[] {userId, createPostReq.getContents()};
        this.jdbcTemplate.update(insertQuery1, params1);

        String getPostIdQuery = "select last_insert_id()";
        long postId = this.jdbcTemplate.queryForObject(getPostIdQuery, long.class);

        String insertQuery2 = "insert into PostImg (postId, imgUrl) VALUES (?,?)";
        for(int i = 0; i < createPostReq.getPostImgList().size(); i++) {
            Object[] params2 = new Object[] {postId, createPostReq.getPostImgList().get(i)};
            this.jdbcTemplate.update(insertQuery2, params2);
        }
    }

    public int checkPostLike(long userId, long postId) {
        String checkPostLikeQuery = "select exists (select postLikeId from PostLike where userId = ? and postId = ? and status = 1)";
        Object[] params = new Object[] {userId, postId};
        return this.jdbcTemplate.queryForObject(checkPostLikeQuery, int.class, params);
    }

    public void setPostLike(long userId, long postId) {
        String insertLikeQuery = "insert into PostLike (userId, postId) VALUES (?,?)";
        Object[] params = new Object[] {userId, postId};
        this.jdbcTemplate.update(insertLikeQuery, params);
    }

    public int checkPostExists(long postId) {
        String checkPostLikeQuery = "select exists (select postId from Post where postId = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkPostLikeQuery, int.class, postId);
    }

    public void deletePostLike(long userId, long postId) {
        String updateQuery = "update PostLike set status = 0 where userId = ? and postId = ?";
        Object[] params = new Object[] {userId, postId};

        this.jdbcTemplate.update(updateQuery, params);
    }

    public int countPostComments(long postId) {
        String countCommentsQuery = "select count(postCommentId) from PostComment where postId = ?";
        return this.jdbcTemplate.queryForObject(countCommentsQuery, int.class, postId);
    }


    public GetPostCommentsRes getPostComments(long userId, long postId, int pageIndex) {
        String getPostContentsQuery = "select P.userId, U.nickName, U.profileImg, P.contents,\n" +
                "       case\n" +
                "           when TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP) >= 30 then DATE_FORMAT(P.createAt, '%c월 %e일')\n" +
                "           when TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(DAY, P.createAt, CURRENT_TIMESTAMP), '일')\n" +
                "           when TIMESTAMPDIFF(HOUR , P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(Hour, P.createAt, CURRENT_TIMESTAMP), '시간')\n" +
                "           when TIMESTAMPDIFF(MINUTE, P.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(MINUTE, P.createAt, CURRENT_TIMESTAMP), '분')\n" +
                "           else concat(TIMESTAMPDIFF(SECOND, P.createAt, CURRENT_TIMESTAMP), '초') end as createAt\n" +
                "from Post P\n" +
                "inner join User U using(userId)\n" +
                "where postId = ?";

        GetPostCommentsRes getPostCommentsRes = this.jdbcTemplate.queryForObject(getPostContentsQuery, (rs, rowNum) -> new GetPostCommentsRes(
                rs.getLong("userId"),
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("contents"),
                rs.getString("createAt")), postId);

        String getPostCommentsListQuery = "select PC.userId, PC.postCommentId, U.nickName, U.profileImg, PC.contents,\n" +
                "       case\n" +
                "           when TIMESTAMPDIFF(DAY, PC.createAt, CURRENT_TIMESTAMP) >= 30 then DATE_FORMAT(PC.createAt, '%c월 %e일')\n" +
                "           when TIMESTAMPDIFF(DAY, PC.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(DAY, PC.createAt, CURRENT_TIMESTAMP), '일')\n" +
                "           when TIMESTAMPDIFF(HOUR , PC.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(Hour, PC.createAt, CURRENT_TIMESTAMP), '시간')\n" +
                "           when TIMESTAMPDIFF(MINUTE, PC.createAt, CURRENT_TIMESTAMP) >= 1 then concat(TIMESTAMPDIFF(MINUTE, PC.createAt, CURRENT_TIMESTAMP), '분')\n" +
                "           else concat(TIMESTAMPDIFF(SECOND, PC.createAt, CURRENT_TIMESTAMP), '초') end as createAt, if(PCL.commentLike=1, true, false) as commentLike\n" +
                "from PostComment PC\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select postCommentId, status as commentLike from PostCommentLike where userId = ? and status = 1) PCL using(postCommentId)\n" +
                "where postId = ? limit 0, ?";

        int size = 10 * pageIndex;
        Object[] params = new Object[] {userId, postId, size};

        List<PostComment> postCommentList = this.jdbcTemplate.query(getPostCommentsListQuery, (rs, rowNum) -> new PostComment(
                rs.getLong("userId"),
                rs.getLong("postCommentId"),
                rs.getString("nickName"),
                rs.getString("profileImg"),
                rs.getString("contents"),
                rs.getString("createAt"),
                rs.getBoolean("commentLike")), params);

        getPostCommentsRes.setPostCommentList(postCommentList);
        return getPostCommentsRes;
    }

    public void setPostComment(long userId, long postId, String contents) {
        String insertPostCommentQuery = "insert into PostComment (userId, postId, contents) VALUES (?,?,?)";
        Object[] params = new Object[] {userId, postId, contents};
        this.jdbcTemplate.update(insertPostCommentQuery, params);
    }

    public int checkCommentExists(long commentId) {
        String checkCommentQuery = "select exists (select postCommentId from PostComment where postCommentId = ? and status = 1)";
        return this.jdbcTemplate.queryForObject(checkCommentQuery, int.class, commentId);
    }

    public int checkCommentLike(long userId, long commentId) {
        String checkCommentLikeQuery = "select exists (select postCommentLikeId from PostCommentLike where userId = ? and postCommentId = ? and status = 1)";
        Object[] params = new Object[] {userId, commentId};
        return this.jdbcTemplate.queryForObject(checkCommentLikeQuery, int.class, params);
    }

    public void setCommentLike(long userId, long commentId) {
        String insertLikeQuery = "insert into PostCommentLike (userId, postCommentId) VALUES (?,?)";
        Object[] params = new Object[] {userId, commentId};
        this.jdbcTemplate.update(insertLikeQuery, params);
    }

    public void deleteCommentLike(long userId, long commentId) {
        String updateQuery = "update PostCommentLike set status = 0 where userId = ? and postCommentId = ?";
        Object[] params = new Object[] {userId, commentId};

        this.jdbcTemplate.update(updateQuery, params);
    }

    public long getUserByPost(long postId) {
        String getUserByPostQuery = "select userId from Post where postId = ?";
        return this.jdbcTemplate.queryForObject(getUserByPostQuery, long.class, postId);
    }

    public void updatePost(long postId, String contents) {
        String updateQuery = "update Post set contents = ? where postId = ?";
        Object[] params = new Object[] {contents, postId};

        this.jdbcTemplate.update(updateQuery, params);
    }

    public void deletePost(long postId) {
        String updateQuery = "update Post set status = -1 where postId = ?";
        Object[] params = new Object[] {postId};

        this.jdbcTemplate.update(updateQuery, params);
    }
}
