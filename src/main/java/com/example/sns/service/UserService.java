package com.example.sns.service;

import com.example.sns.controller.model.User;
import com.example.sns.controller.model.entity.UserEntity;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO: implement
    public User join(String userName, String password){
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);

        // 회원가입 진행 = user를 등록
        userEntityRepository.save(new UserEntity());
        
        return new User();
    }

    // 로그인 성공하면 그에 맞는 토큰을 반환
    // TODO: implement
    public String login(String userName, String password){
        // 회원가입 여부 체크, 없으면 Throw 던져줌
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException());
        
        // 비밀번호 체크
        if(!userEntity.getPassword().equals(password)){
            throw new SnsApplicationException();
        }

        // 토큰 생성

        return "";
    }
}
