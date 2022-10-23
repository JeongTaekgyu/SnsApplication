package com.example.sns.service;

import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlarmService {

    void send(AlarmType type, AlarmArgs arg, Integer receiverUserId);
    SseEmitter connectAlarm(Integer userId);

}
