package com.example.sns.configuration.filter;

import com.example.sns.model.User;
import com.example.sns.service.UserService;
import com.example.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {  // 매 요청 때마다 필터를 씌울거라서 OncePerRequestFilter를 상속받는다.

    private final String secretKey;
    private final UserService userService;

    // 지정한 url인 경우 param에서 체크하도록한다.
    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String token;
        // 들어오는 request로 인증할 수 있다.
        // claims에 있는 유저네임을 꺼내서 유효한지 확인하자

        // 헤더의 형식이 옳바르면 토큰만 빼온다.
        try {
            if(TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else {
                // get header
                final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                // 헤더가 "Bearer "로 시작되지 않는다면 에러 발생
                if (header == null || !header.startsWith("Bearer ")) {
                    log.error("Error occurs while getting header is null or invalid {}", request.getRequestURL());
                    filterChain.doFilter(request, response);
                    return;
                }
                token = header.split(" ")[1].trim(); // Bearer와 토큰사이에 스페이스가 있는거 고려하자
            }

            if (JwtTokenUtils.isExpired(token, secretKey)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // get username from token
            String userName = JwtTokenUtils.getUserName(token, secretKey);
            // check the user is valid
            User userDetails = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
