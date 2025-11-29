package nocomment.orato.global.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import nocomment.orato.domain.auth.dto.CustomOidcUser;
import nocomment.orato.global.jwt.JWTUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("CustomSuccessHandler - Principal type: " + authentication.getPrincipal().getClass().getName());
        
        // OAuth2User 또는 OidcUser 모두 처리 가능
        String username = null;
        String name = null;
        
        // CustomOidcUser인 경우 (Google OIDC)
        if (authentication.getPrincipal() instanceof CustomOidcUser) {
            System.out.println("Using CustomOidcUser");
            CustomOidcUser customOidcUser = (CustomOidcUser) authentication.getPrincipal();

            username = customOidcUser.getUsername();
            name = customOidcUser.getName();
        }
        // CustomOAuth2User인 경우 (Naver 등)
        else if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            System.out.println("Using CustomOAuth2User");
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
            username = customUserDetails.getUsername();
            name = customUserDetails.getName();
        } 
        // 기본 OAuth2User인 경우 (fallback)
        else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            System.out.println("Using default OAuth2User/OidcUser");
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = 
                (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            // OAuth2User의 name을 username으로 사용
            username = oauth2User.getName();
            name = oauth2User.getAttribute("name").toString();
            System.out.println("Extracted username: " + username);
        }

        if (username == null) {
            username = authentication.getName();
            System.out.println("Fallback username: " + username);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        System.out.println("Creating JWT for username: " + username + ", role: " + role);
        String token = jwtUtil.createJwt(username, role, name, 60*60*60L);

        response.addCookie(createCookie("Authorization", token));
        
        // 테스트를 위해 백엔드로 리다이렉트 (프론트엔드가 준비되면 http://localhost:3000/로 변경)
        System.out.println("Redirecting to /");
        response.sendRedirect("http://localhost:5173/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
