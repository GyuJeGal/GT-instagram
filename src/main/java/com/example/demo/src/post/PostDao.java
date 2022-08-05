package com.example.demo.src.post;

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
        String getPostsQuery = "select P.userId, U.nickName, U.profileImg, P.postId, if(PL1.postLike=1, true, false) as postLike, if(isNull(PL2.countLike), 0, PL2.countLike) as countLike, P.contents\n" +
                "from Post P\n" +
                "inner join User U using(userId)\n" +
                "left outer join (select postId, status as postLike from PostLike where userId = ? and status = 1) PL1 using(postId)\n" +
                "left outer join (select postId, count(postLikeId) as countLike from PostLike group by (postId)) PL2 using(postId)\n" +
                "where userId in (select followedUserId from Follow where followingUserId = ? and status = 1) order by (postId) desc limit 0,?";

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
                rs.getString("contents")), params);

        String getPostImgQuery = "select imgUrl from PostImg where postId = ?";

        for(int i = 0; i < getPostResList.size(); i++)  {
            List<String> postImgList = this.jdbcTemplate.query(getPostImgQuery, (rs, rowNum) -> rs.getString("imgUrl"), postIdList.get(i));

            getPostResList.get(i).setPostImgList(postImgList);
        }

        return getPostResList;
    }
}
