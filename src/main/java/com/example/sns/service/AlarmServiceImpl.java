package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import com.example.sns.model.entity.AlarmEntity;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.repository.AlarmEntityRepository;
import com.example.sns.repository.EmitterRepository;
import com.example.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService{

    private final static Long DEFAULT_TIMEOUT = 60L * 100 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;

//    public void send(Integer alarmId, Integer userId) {
    public void send(AlarmType type, AlarmArgs arg, Integer receiverUserId) {
        UserEntity user = userEntityRepository.findById(receiverUserId).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(user, type, arg) );

        // 인스턴스를 가져온다.
        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try {
                // ALARM_NAME = "alarm" 이란 이름의 알람이 발생되면 클라이언트에서 "alarm"이란 이름으로 발생된 이벤트가 발생한다.
                sseEmitter.send(SseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e){
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId)); // connect 끝났을 때 해줄 동작
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));    // Timeout때 해줄 동작

        // 커넥트 됐을 때 커넥트 됐다고 이벤트를 전송해준다.
        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
        } catch (IOException exception){
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
