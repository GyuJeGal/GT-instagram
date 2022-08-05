package com.example.demo.src.post;

import com.example.demo.src.post.model.CreatePostReq;
import com.example.demo.src.post.model.GetPostRes;
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
}
