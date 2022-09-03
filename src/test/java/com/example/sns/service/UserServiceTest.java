package com.example.sns.service;

import com.example.sns.controller.model.entity.UserEntity;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.UserEntityFixture;
import com.example.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";

        // mocking
        // 회원가입이 된적이 없기 때문에 userName으로 db에서 찾으면 정보가 없다.
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        //when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class))); // 이코드를 아래 fixture가 있는코드로 변경함
        when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(userName, password)));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        // 회원가입이 된적이 없기 때문에 userName으로 db에서 찾으면 정보가 없다.
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
//        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        // 위에 2행을 fixture로 변환함
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));
        
        
        // 회원가입이 이미 유저가 있으니까 Exception을 던져줄거다
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

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
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
    }

    @Test
    void 로그인시_password가_틀린_경우() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        // 로그인시 패스워드가 틀렸으니까 Exception을 던져줄거다
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
    }
}
