package nocomment.orato.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import nocomment.orato.domain.auth.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println("JWTFilter - Processing request: " + requestURI);
        
        String authorization = null;
        
        // 1. Authorization 헤더에서 토큰 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authorization = authHeader.substring(7);
            System.out.println("Authorization token found in header");
        }
        
        // 2. 헤더에 없으면 쿠키에서 확인
        if (authorization == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println("Cookie found: " + cookie.getName());
                    if ("Authorization".equals(cookie.getName())) {
                        authorization = cookie.getValue();
                        System.out.println("Authorization token found in cookie");
                    }
                }
            } else {
                System.out.println("No cookies found in request");
            }
        }

        if (authorization == null) {
            System.out.println("token null - allowing request to proceed without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(authorization)) {
                System.out.println("token expired - allowing request to proceed without authentication");
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsername(authorization);
            String role = jwtUtil.getRole(authorization);
            String name = jwtUtil.getName(authorization);
            
            System.out.println("JWT validated - username: " + username + ", role: " + role);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole(role);
            userDTO.setName(name);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            System.out.println("Authentication set in SecurityContext");

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.err.println("Error processing JWT: " + e.getMessage());
            e.printStackTrace();
            filterChain.doFilter(request, response);
        }
    }}