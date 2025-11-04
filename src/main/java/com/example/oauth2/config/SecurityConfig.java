package com.example.oauth2.config;

import com.example.oauth2.jwt.JWTFilter;
import com.example.oauth2.jwt.JWTUtil;
import com.example.oauth2.oauth2.CustomSuccessHandler;
import com.example.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays; // Arrays를 임포트합니다.
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        // exposedHeaders를 올바르게 설정하여 두 헤더 모두 노출
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                }));

        // csrf disable (JWT를 사용하므로 비활성화)
        http
                .csrf((auth) -> auth.disable());

        // From 로그인 방식 disable (OAuth2 및 JWT를 사용하므로 비활성화)
        http
                .formLogin((auth) -> auth.disable());

        // HTTP Basic 인증 방식 disable (OAuth2 및 JWT를 사용하므로 비활성화)
        http
                .httpBasic((auth) -> auth.disable());

        // JWTFilter 추가 (UsernamePasswordAuthenticationFilter 이전에 실행)
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // oauth2 로그인 설정
        http
                .oauth2Login((oauth2) -> oauth2
                                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                        .userService(customOAuth2UserService)) // 커스텀 OAuth2 UserService 설정
                                .successHandler(customSuccessHandler) // 커스텀 성공 핸들러 설정
                        // .redirectionEndpoint() // OAuth2 로그인 리디렉션 엔드포인트 설정, 기본값이 잘 작동하면 생략 가능
                        // .baseUri("/login/oauth2/code/*") // 예를 들어 구글 콜백이 /login/oauth2/code/google 인 경우
                );

        // 경로별 인가 작업 (수정됨)
        http
                .authorizeHttpRequests((auth) -> auth
                        // 루트 경로, OAuth2 로그인 시작 경로 및 콜백 경로를 permitAll()로 허용합니다.
                        // '/error' 경로도 추가하여 에러 발생 시 리다이렉트 루프를 방지할 수 있습니다.
                        .requestMatchers("/", "/oauth2/**", "/login/oauth2/code/**", "/error").permitAll()
                        .anyRequest().authenticated()); // 그 외 모든 요청은 인증 필요

        // 세션 설정 : STATELESS (JWT를 사용하므로 세션을 사용하지 않음)
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
