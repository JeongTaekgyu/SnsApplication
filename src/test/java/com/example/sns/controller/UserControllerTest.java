package com.example.sns.controller;

import com.example.sns.controller.model.User;
import com.example.sns.controller.request.UserJoinRequest;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc   // api 형태의 테스트
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void 회원가입() throws Exception{
        String userName = "userName";
        String password = "password";

        // TODO: mocking
        when(userService.join()).thenReturn(mock(User.class));  // 정상적으로 동작하는 경우 User클래스 반환

        mockMvc.perform(post("/api/v1/users/join")  // 해당 url로 post 요청한다.
                .contentType(MediaType.APPLICATION_JSON)
                // todo : add request body
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
        ).andDo(print())
         .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    public void 회원가입_이미_회원가입된_userName으로_회원가입을_하는경우_에러반환() throws Exception{
        String userName = "userName";
        String password = "password";

        when(userService.join()).thenThrow(new SnsApplicationException());

        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        // todo : add request body
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isConflict());  // 에러를 반환하는 테스트이기 때문에 conflict를 기대한다.
    }

}
