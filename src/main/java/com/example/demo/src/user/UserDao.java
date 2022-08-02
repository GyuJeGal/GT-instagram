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
        return this.jdbcTemplate.queryForObject(getUserIdQuery,long.class);
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
        // 회원 탈퇴: -1, 차단 계정: 0, 정상 회원: 1, 휴면 계정: 2
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


//
//    public User getPwd(PostLoginReq postLoginReq) {
//        String getPwdQuery = "select userId, password from User where emailAddr = ?";
//        String getPwdParam = postLoginReq.getEmail();
//        return this.jdbcTemplate.queryForObject(getPwdQuery, (rs, rowNum) ->
//                new User(rs.getLong("userId"), rs.getString("password")), getPwdParam);
//    }
//
//    public GetUserInfo getUser(long userId) {
//        String getUserQuery = "select U.profileImg, case when U.grade = 1 then '아기손' when U.grade = 2 then '곰손' when U.grade = 3 then '은손' else '금손' end as grade, U.nickName, U.rewardPoint, UC.countCoupon\n" +
//                "from User U\n" +
//                "left outer join (select userId, count(couponId) as countCoupon from UserCoupon group by (userId)) UC using (userId)\n" +
//                "where userId = ?";
//        return this.jdbcTemplate.queryForObject(getUserQuery, (rs, rowNum) -> new GetUserInfo(
//                rs.getString("profileImg"),
//                rs.getString("grade"),
//                rs.getString("nickName"),
//                rs.getInt("rewardPoint"),
//                rs.getInt("countCoupon")), userId);
//    }
//
//    public void deleteUser(long userId) {
//        String deleteUserQuery = "update User set status = 0 where userId = ?";
//        this.jdbcTemplate.update(deleteUserQuery, userId);
//    }
//
//    public GetUserDetail getUserDetail(long userId) {
//        String getUserQuery = "select profileImg, nickName, emailAddr, date_format(birthDay, '%Y년 %c월 %e일') as birthDay, if(isnull(gender), null, if(gender='F', '여성', '남성')) as gender, phoneNumber, address\n" +
//                "from User\n" +
//                "where userId = ?";
//        return this.jdbcTemplate.queryForObject(getUserQuery, (rs, rowNum) -> new GetUserDetail(
//                rs.getString("profileImg"),
//                rs.getString("nickName"),
//                rs.getString("emailAddr"),
//                rs.getString("birthDay"),
//                rs.getString("gender"),
//                rs.getString("phoneNumber"),
//                rs.getString("address")), userId);
//    }
//
//    public void modifyUserProfile(long userId, String imgUrl) {
//        String updateQuery = "update User set profileImg = ? where userId = ?";
//        Object[] params = new Object[] {imgUrl, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public void modifyUserName(long userId, String userName) {
//        String updateQuery = "update User set nickName = ? where userId = ?";
//        Object[] params = new Object[] {userName, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public void modifyUserEmail(long userId, String email) {
//        String updateQuery = "update User set emailAddr = ? where userId = ?";
//        Object[] params = new Object[] {email, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public void modifyUserBirthDay(long userId, String birthDay) {
//        String updateQuery = "update User set birthDay = ? where userId = ?";
//        Object[] params = new Object[] {birthDay, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public void modifyUserGender(long userId, Character gender) {
//        String genderParam = String.valueOf(gender);
//        String updateQuery = "update User set gender = ? where userId = ?";
//        Object[] params = new Object[] {genderParam, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public void modifyUserPhoneNumber(long userId, String phoneNumber) {
//        String updateQuery = "update User set phoneNumber = ? where userId = ?";
//        Object[] params = new Object[] {phoneNumber, userId};
//        this.jdbcTemplate.update(updateQuery, params);
//    }
//
//    public GetBasketProduct getBasketProducts(long userId) {
//
//        String getBasketIdQuery = "select basketId from Basket where userId = ?";
//        long basketId = this.jdbcTemplate.queryForObject(getBasketIdQuery, long.class, userId);
//
//        String getCountProductQuery = "select count(basketDetailId) as countProduct from BasketDetail where basketId = ?";
//        int countProduct = this.jdbcTemplate.queryForObject(getCountProductQuery, int.class, basketId);
//
//        String getOrderProductIdQuery = "select orderProductId from BasketDetail where basketId = ?";
//        List<Long> orderProductId = this.jdbcTemplate.query(getOrderProductIdQuery,
//                (rs, rowNum) -> rs.getLong("orderProductId"), basketId);
//
//        List<BasketProductDetail> basketProductDetailList = new ArrayList<>();
//
//        for(int i = 0; i <orderProductId.size(); i++) {
//            String getOptionQuery = "select PO.optionName, POD.detailName\n" +
//                    "from OrderOption OO\n" +
//                    "inner join ProductOption PO using(productOptionId)\n" +
//                    "inner join ProductOptionDetail POD using(productOptionDetailId)\n" +
//                    "where orderProductId = ?";
//
//            List<BasketProductOption> basketProductOptionList = this.jdbcTemplate.query(getOptionQuery, (rs, rowNum) -> new BasketProductOption(
//                    rs.getString("optionName"),
//                    rs.getString("detailName")), orderProductId.get(i));
//
//            String getBasketProductQuery = "select PD.basketDetailId, W.writerId, W.nickName, PI.imgUrl, P.title, if(P.leftAmount=-1, '주문시 제작', concat(P.leftAmount,'개 남음')) as leftAmount,\n" +
//                    "       (round(P.price-(P.price*P.discountRate/100), -2) + POD.addPrice)*PD.orderCount as finalPrice, PD.orderCount,\n" +
//                    "       if((round(P.price-(P.price*P.discountRate/100), -2) + POD.addPrice)*PD.orderCount > P.freeAmount, 0, P.deliveryFee) as deliveryFee, P.freeAmount\n" +
//                    "from BasketDetail PD\n" +
//                    "inner join OrderProduct OP using (orderProductId)\n" +
//                    "inner join Product P using(productId)\n" +
//                    "inner join Writer W using (writerId)\n" +
//                    "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
//                    "inner join (select orderProductId, sum(detailPrice) as addPrice from OrderOption inner join ProductOptionDetail using (productOptionDetailId)\n" +
//                    "    inner join OrderProduct using(orderProductId) where orderProductId in (select orderProductId from BasketDetail where basketId = ?)\n" +
//                    "group by (orderProductId)) POD using(orderProductId)\n" +
//                    "where PD.orderProductId = ?";
//
//            Object[] params = new Object[] {basketId, orderProductId.get(i)};
//
//            BasketProductDetail basketProductDetail = this.jdbcTemplate.queryForObject(getBasketProductQuery, (rs, rowNum) -> new BasketProductDetail(
//                    rs.getLong("basketDetailId"),
//                    rs.getLong("writerId"),
//                    rs.getString("nickName"),
//                    rs.getString("imgUrl"),
//                    rs.getString("title"),
//                    rs.getString("leftAmount"),
//                    basketProductOptionList,
//                    rs.getInt("finalPrice"),
//                    rs.getInt("orderCount"),
//                    rs.getInt("deliveryFee"),
//                    rs.getInt("freeAmount")), params);
//
//            basketProductDetailList.add(basketProductDetail);
//        }
//
//        return new GetBasketProduct(countProduct, basketProductDetailList);
//    }
//
//    public List<GetOrderList> getOrderList(long userId) {
//        String getOrderListQuery = "select OP.productId, date_format(O.createAt, '%Y.%m.%d') as orderAt,\n" +
//                "       (round(P.price-(P.price*P.discountRate/100), -2) + POD.addPrice)*O.orderCount as finalPrice, PI.imgUrl,\n" +
//                "       P.title, W.nickName, if(O.status=2, '작가 발송 완료', '발송 준비중') as sendStatus\n" +
//                "from Ordered O\n" +
//                "inner join OrderProduct OP using (orderProductId)\n" +
//                "inner join Product P using(productId)\n" +
//                "inner join (select orderProductId, sum(detailPrice) as addPrice\n" +
//                "    from OrderOption\n" +
//                "    inner join ProductOptionDetail using (productOptionDetailId)\n" +
//                "    inner join OrderProduct using(orderProductId) where orderProductId in (select orderProductId from Ordered where userId = ?)\n" +
//                "                                                  group by (orderProductId)) POD using(orderProductId)\n" +
//                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
//                "inner join Writer W using (writerId)\n" +
//                "where userId = ?";
//        Object[] params = new Object[] {userId, userId};
//
//        List<GetOrderList> orderLists = this.jdbcTemplate.query(getOrderListQuery, (rs, rowNum) -> new GetOrderList(
//                rs.getLong("productId"),
//                rs.getString("orderAt"),
//                rs.getInt("finalPrice"),
//                rs.getString("imgUrl"),
//                rs.getString("title"),
//                rs.getString("nickName"),
//                rs.getString("sendStatus")), params);
//        return orderLists;
//    }
//
//
//    public List<GetLikeProduct> getLikeProducts(long userId) {
//        String getLikeProductQuery = "select PL.productId, PI.imgUrl,W.nickName, P.title, P.price, P.discountRate, round(P.price-(P.price*P.discountRate/100)) as finalPrice,\n" +
//                "       P.rating, PR1.countReview, PR.contents\n" +
//                "from ProductLike PL\n" +
//                "inner join Product P using(productId)\n" +
//                "inner join (select productId, imgUrl from ProductImg group by (productId)) PI using(productId)\n" +
//                "inner join Writer W using (writerId)\n" +
//                "left outer join (select productId, count(productReviewId) as countReview from ProductReview group by (productId)) PR1 using (productId)\n" +
//                "left outer join (select * from (select productReviewId, productId, contents from ProductReview order by (productReviewId) desc LIMIT 18446744073709551615) as PR group by(productId)) PR using (productId)\n" +
//                "where userId = ?";
//        List<GetLikeProduct> getLikeProductList = this.jdbcTemplate.query(getLikeProductQuery, (rs, rowNum) -> new GetLikeProduct(
//                rs.getLong("productId"),
//                rs.getString("imgUrl"),
//                rs.getString("nickName"),
//                rs.getString("title"),
//                rs.getInt("price"),
//                rs.getInt("discountRate"),
//                rs.getInt("finalPrice"),
//                rs.getDouble("rating"),
//                rs.getInt("countReview"),
//                rs.getString("contents")), userId);
//        return getLikeProductList;
//    }
//
//    public List<GetLikeOnlineClasses> getLikeOnlineClasses(long userId) {
//        String getLikeClassQuery = "select OCL.onlineClassId, OC.profileImg, CC.categoryName, case when OC.level=1 then '쉬움' when OC.level=2 then '보통' else '어려움' end as level,\n" +
//                "       OC.title, W.nickName\n" +
//                "from OnlineClassLike OCL\n" +
//                "inner join OnlineClass OC using (onlineClassId)\n" +
//                "inner join ClassCategory CC using (categoryId)\n" +
//                "inner join Writer W using (writerId)\n" +
//                "where userId = ?";
//        List<GetLikeOnlineClasses> getLikeOnlineClasses = this.jdbcTemplate.query(getLikeClassQuery, (rs, rowNum) -> new GetLikeOnlineClasses(
//                rs.getLong("onlineClassId"),
//                rs.getString("profileImg"),
//                rs.getString("categoryName"),
//                rs.getString("level"),
//                rs.getString("title"),
//                rs.getString("nickName")), userId);
//        return getLikeOnlineClasses;
//    }
//
//    public List<GetLikeOfflineClasses> getLikeOfflineClasses(long userId) {
//        String getLikeClassQuery = "select OCL.offlineClassId, OCI.imgUrl, OC.address, CC.categoryName, OC.title, OC.price\n" +
//                "from OfflineClassLike OCL\n" +
//                "inner join OfflineClass OC using (offlineClassId)\n" +
//                "inner join (select offlineClassId, imgUrl from OfflineClassImg group by (offlineClassId)) OCI using (offlineClassId)\n" +
//                "inner join ClassCategory CC using (categoryId)\n" +
//                "where userId = ?";
//
//        List<GetLikeOfflineClasses> getLikeOfflineClasses = this.jdbcTemplate.query(getLikeClassQuery, (rs, rowNum) -> new GetLikeOfflineClasses(
//                rs.getLong("offlineClassId"),
//                rs.getString("imgUrl"),
//                rs.getString("address"),
//                rs.getString("categoryName"),
//                rs.getString("title"),
//                rs.getInt("price")), userId);
//        return getLikeOfflineClasses;
//    }
//
//    public int checkUserId(Long userId) {
//        String checkQuery = "select exists (select userId from User where userId=?)";
//        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userId);
//    }
}
