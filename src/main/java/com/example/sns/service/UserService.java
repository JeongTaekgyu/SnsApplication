package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.model.User;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.repository.UserEntityRepository;
import com.example.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    // UserDetailsService 인터페이스를 implements 받지 않고 직업 구현 한다.
    public User loadUserByUserName(String userName){
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    @Transactional  // Exception이 발생하면 엔티티를 저장하는 부분이 rollback이 될 수 있다.
    public User join(String userName, String password){
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        // user가 있다면 에러를 발생시킨다.
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
//        throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
//        throw new RuntimeException(); // save 이후에도 @Transactional이 있기 때문에 에러 발생하면 db에 저장안된다.
        return User.fromEntity(userEntity);
    }

    // 로그인 성공하면 그에 맞는 토큰을 반환
    // TODO: implement
    public String login(String userName, String password){
        // 회원가입 여부 체크, 없으면 Throw 던져줌
        UserEntity userEntity = userEntityRepository.findByUserName(userName).
                orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        
        // 비밀번호 체크
        // rawpassword와 encodedpassword가 매치가 안되면 에러 발생시킨다.
        if(!encoder.matches(password, userEntity.getPassword())) {
        //if(!userEntity.getPassword().equals(password)){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

    // TODO : alarm return
    public Page<Void> alarmList(String usreName, Pageable pageable) {

        return Page.empty();
    }
}
