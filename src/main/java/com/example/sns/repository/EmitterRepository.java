package com.example.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class EmitterRepository {
    // 로컬 캐시를 인스턴스에 저장한다.

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Integer userId, SseEmitter sseEmitter){
        final String key = getKey(userId);
        emitterMap.put(key, sseEmitter);
        log.info("Set sseEmitter {}", userId);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(Integer userId){
        final String key = getKey(userId);
        log.info("Set sseEmitter {}", userId);
        return Optional.ofNullable(emitterMap.get(key));
    }

    // 에러 발생하면 지워주자? 더이상 캐시로 들고있을 필요가없다.
    public void delete(Integer userId){
        emitterMap.remove(getKey(userId));
    }

    private String getKey(Integer userId) {
        return "Emitter:UID:" + userId;
    }
}
