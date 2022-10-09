package com.example.sns.util;

import java.util.Optional;

// casting을 위한 클래스
public class ClassUtils {

    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        // clazz가 null 이 아니고 해당 인스턴스라면 캐스팅을 해주고 그게 아니면 empty를 반환한다.
        return clazz != null && clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();
    }
}
