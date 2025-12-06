package nocomment.orato.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import nocomment.orato.domain.auth.dto.CustomOidcUser;
import nocomment.orato.global.config.OratoProperties;
import nocomment.orato.global.jwt.JWTUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

@Component
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final OratoProperties oratoProperties;

    public CustomSuccessHandler(JWTUtil jwtUtil, OratoProperties oratoProperties) {

        this.jwtUtil = jwtUtil;
        this.oratoProperties = oratoProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.debug("OAuth authentication success with principal type: {}", authentication.getPrincipal().getClass().getName());
        
        // OAuth2User 또는 OidcUser 모두 처리 가능
        String username = null;
        String name = null;
        
        // CustomOidcUser인 경우 (Google OIDC)
        if (authentication.getPrincipal() instanceof CustomOidcUser) {
            log.debug("Handling OAuth success with CustomOidcUser");
            CustomOidcUser customOidcUser = (CustomOidcUser) authentication.getPrincipal();

            username = customOidcUser.getUsername();
            name = customOidcUser.getName();
        }
        // CustomOAuth2User인 경우 (Naver 등)
        else if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            log.debug("Handling OAuth success with CustomOAuth2User");
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
            username = customUserDetails.getUsername();
            name = customUserDetails.getName();
        } 
        // 기본 OAuth2User인 경우 (fallback)
        else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            log.debug("Handling OAuth success with default OAuth2User");
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            // OAuth2User의 name을 username으로 사용
            username = oauth2User.getName();
            name = Objects.toString(oauth2User.getAttribute("name"), username);
        }

        if (username == null) {
            username = authentication.getName();
            log.debug("Falling back to authentication name for OAuth success");
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, name, 60*60*60L);

        response.addHeader("Set-Cookie", createCookie("Authorization", token, request.isSecure()).toString());
        
        log.info("OAuth login succeeded, redirecting to frontend");
        response.sendRedirect(oratoProperties.getFrontend().getRedirectUrl());
    }

    private ResponseCookie createCookie(String key, String value, boolean secure) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60 * 60)
                .build();
    }
}
