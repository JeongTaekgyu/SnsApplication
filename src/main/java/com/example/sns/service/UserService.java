package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.model.User;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    public User join(String userName, String password){
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        // user가 있다면 에러를 발생시킨다.
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, password));
        
        return User.fromEntity(userEntity);
    }

    // 로그인 성공하면 그에 맞는 토큰을 반환
    // TODO: implement
    public String login(String userName, String password){
        // 회원가입 여부 체크, 없으면 Throw 던져줌
        UserEntity userEntity = userEntityRepository.findByUserName(userName).
                orElseThrow(() -> new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));
        
        // 비밀번호 체크
        if(!userEntity.getPassword().equals(password)){
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, "");
        }

        // 토큰 생성

        return "";
    }
}