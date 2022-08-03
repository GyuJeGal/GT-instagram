package com.example.demo.src.user;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserScheduler {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;

    @Autowired
    public UserScheduler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkUserPrivacy() {
        logger.info("개인정보 처리 방침 동의 체크");

        userDao.checkUserPrivacy();
    }
}