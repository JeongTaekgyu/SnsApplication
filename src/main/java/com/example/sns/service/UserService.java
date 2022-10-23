package com.example.sns.service;

import com.example.sns.model.Alarm;
import com.example.sns.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User loadUserByUserName(String userName);
    User join(String userName, String password);
    String login(String userName, String password);
    Page<Alarm> alarmList(Integer userId, Pageable pageable);

}
