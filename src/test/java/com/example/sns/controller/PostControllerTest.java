package com.example.sns.controller;

import com.example.sns.controller.request.PostCreateRequest;
import com.example.sns.controller.request.PostModifyRequest;
import com.example.sns.controller.request.UserJoinRequest;
import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.PostEntityFixture;
import com.example.sns.model.Post;
import com.example.sns.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트작성() throws Exception{
        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    @WithAnonymousUser  // 익명의 유저
    void 포스트작성시_로그인하지않은경우() throws Exception{
        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());   //.
    }

    // ---------------------- modify(update) ----------------------
    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트수정() throws Exception{
        String title = "title";
        String body = "body";

        when(postService.modify(eq(title), eq(body), any(), any())).
                thenReturn(Post.fromEntity(PostEntityFixture.get("userName",1 ,1)));

        mockMvc.perform(put("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    @WithAnonymousUser  // 익명의 유저
    void 포스트수정시_로그인하지않은경우() throws Exception{
        String title = "title";
        String body = "body";

        mockMvc.perform(put("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());   // 권한이 없음을 기대한다.
    }

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트수정시_본인이_작성한_글이_아니라면_에러발생() throws Exception{
        String title = "title";
        String body = "body";

        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).
                when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트수정시_수정하려는_글이_없는경우_에러발생() throws Exception{
        String title = "title";
        String body = "body";

        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).
                when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(put("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isNotFound());   // 찾을 수 없기를 기대한다.
    }

    // ---------------------- delete ----------------------
    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트삭제() throws Exception{
        mockMvc.perform(delete("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    @WithAnonymousUser // 익명의 유저
    void 포스트삭제시_로그인하지_않은경우() throws Exception{
        mockMvc.perform(delete("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());   // 권한이 없기를 기대한다.
    }

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트삭제시_작성자와_삭제요청자가_다를경우() throws Exception{
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());   // 권한이 없기를 기대한다.
    }

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 포스트삭제시_삭제하려는_포스트가_존재하지_않을_경우() throws Exception{
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());   // 찾을 수 없기를 기대한다.
    }

    // ---------------------- feedlist ----------------------
    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 피드목록() throws Exception{
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    @WithAnonymousUser // 익명의 유저
    void 피드목록요청시_로그인하지_않은경우() throws Exception{
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(delete("/api/v1/posts")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());   // 권한이 없기를 기대한다.
    }

    @Test
    @WithMockUser   // 인증된 상태로 테스트를 진행
    void 내_피드목록() throws Exception{
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());   // status가 정상으로 되기를 기대한다.
    }

    @Test
    @WithAnonymousUser // 익명의 유저
    void 내_피드목록요청시_로그인하지_않은경우() throws Exception{
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(delete("/api/v1/posts/my")  // 해당 url로 post 요청한다.
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());   // 권한이 없기를 기대한다.
    }

}
