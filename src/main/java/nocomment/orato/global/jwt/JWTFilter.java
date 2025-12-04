package nocomment.orato.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import nocomment.orato.domain.auth.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("Processing JWT for request: {}", requestURI);

        String authorization = null;

        // 1. Authorization 헤더에서 토큰 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authorization = authHeader.substring(7);
            log.debug("Authorization token found in header");
        }

        // 2. 헤더에 없으면 쿠키에서 확인
        if (authorization == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("Authorization".equals(cookie.getName())) {
                        authorization = cookie.getValue();
                        log.debug("Authorization token found in cookie");
                        break;
                    }
                }
            }
        }

        if (authorization == null) {
            log.debug("No JWT token provided for request: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(authorization)) {
                log.debug("Expired JWT token for request: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsername(authorization);
            String role = jwtUtil.getRole(authorization);
            String name = jwtUtil.getName(authorization);

            log.debug("JWT validated for username: {}", username);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole(role);
            userDTO.setName(name);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Authentication set in SecurityContext for request: {}", requestURI);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("Failed to process JWT for request: {}", requestURI, e);
            filterChain.doFilter(request, response);
        }
    }
}
