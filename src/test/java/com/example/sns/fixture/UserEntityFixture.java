package com.example.sns.fixture;

import com.example.sns.model.entity.UserEntity;

public class UserEntityFixture {
    // 가짜 테스트용 UserEntity인 Fixture를 만듦
    public static UserEntity get(String userName, String password, Integer userId) {
        UserEntity result = new UserEntity();
        result.setId(userId);
        result.setUserName(userName);
        result.setPassword(password);
        return result;
    }
}
