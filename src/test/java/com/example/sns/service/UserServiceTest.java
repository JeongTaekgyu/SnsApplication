package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.UserEntityFixture;
import com.example.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    // 서비스단 테스트 작성

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;


    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        // 회원가입이 된적이 없기 때문에 userName으로 db에서 찾으면 정보가 없다.
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        //when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class))); // 이코드를 아래 fixture가 있는코드로 변경함
        when(userEntityRepository.save(any())).thenReturn((UserEntityFixture.get(userName, password, 1)));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password,1 );

        // mocking
        // 회원가입이 된적이 없기 때문에 userName으로 db에서 찾으면 정보가 없다.
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
//        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        // 위에 2행을 fixture로 변환함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));
        
        
        // 회원가입이 이미 유저가 있으니까 Exception을 던져줄거다
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        // Exception에 들어간 에러코드가 맞게 들어갔는지도 테스트한다.
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password,1 );

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);    // 로그인이 정상 동작해야하니까 이거 맞아야한다.

        // 정상적으로 동작하면 에러가 throw 되면 안된다.
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        // 회원가입이 된적이 없기 때문에 userName으로 db에서 찾으면 정보가 없다.
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // 회원가입한 유저가 없으니까 Exception을 던져줄거다
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        // Exception에 들어간 에러코드가 맞게 들어갔는지도 테스트한다.
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인시_password가_틀린_경우() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";
        UserEntity fixture = UserEntityFixture.get(userName, password, 1);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        // 로그인시 패스워드가 틀렸으니까 Exception을 던져줄거다
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
        // Exception에 들어간 에러코드가 맞게 들어갔는지도 테스트한다.
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}
