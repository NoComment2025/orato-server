package nocomment.orato.domain.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "Main", description = "메인 페이지 API")
public class MainController {

    @Operation(summary = "메인 페이지", description = "애플리케이션의 메인 루트 페이지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 페이지 조회 성공")
    })
    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {

        return "main route";
    }

    // 로그인 확인용, Security 에 예외 추가 XX
    @GetMapping("/isL")
    @ResponseBody
    public String checkAPI() {

        return "yes";
    }

    /**
     * 현재 로그인된 사용자의 정보를 조회하는 API 엔드포인트
     * 
     * <p>이 메서드는 Spring Security의 SecurityContext에서 현재 인증된 사용자 정보를 가져옵니다.
     * JWT 토큰이 유효한 경우에만 사용자 정보를 반환하며, 인증되지 않은 경우 401 응답을 반환합니다.</p>
     * 
     * <p><b>동작 원리:</b></p>
     * <ol>
     *   <li>SecurityContextHolder를 통해 현재 요청의 Authentication 객체를 가져옵니다.</li>
     *   <li>Authentication이 null이 아니고, 인증된 상태인지 확인합니다.</li>
     *   <li>Authentication의 Principal을 CustomOAuth2User로 캐스팅하여 사용자 정보를 추출합니다.</li>
     *   <li>사용자의 이름(name)과 사용자명(username)을 Map에 담아 반환합니다.</li>
     * </ol>
     * 
     * <p><b>보안 처리:</b></p>
     * <ul>
     *   <li>JWTFilter에서 토큰을 검증하고 SecurityContext에 Authentication을 설정합니다.</li>
     *   <li>토큰이 없거나 만료된 경우, Authentication이 null이므로 401 Unauthorized를 반환합니다.</li>
     * </ul>
     * 
     * @return ResponseEntity<Map<String, String>> - 사용자 정보를 담은 Map
     *         <ul>
     *           <li>200 OK: 로그인된 사용자 정보 (name, username)</li>
     *           <li>401 Unauthorized: 인증되지 않은 요청</li>
     *         </ul>
     */
    @Operation(
            summary = "현재 로그인된 사용자 정보 조회",
            description = "JWT 토큰을 통해 인증된 현재 사용자의 이름과 사용자명을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "인증되지 않은 사용자 (토큰 없음 또는 만료됨)"
            )
    })
    @GetMapping("/api/my/info")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getMyInfo() {
        
        // SecurityContextHolder: Spring Security에서 현재 실행 중인 스레드의 보안 컨텍스트를 관리하는 클래스
        // SecurityContext: 현재 인증된 사용자의 Authentication 객체를 보관
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Authentication이 null이거나 인증되지 않은 경우 401 반환
        // - JWTFilter에서 유효한 토큰이 있으면 Authentication을 설정함
        // - 토큰이 없거나 만료되었으면 Authentication이 null이거나 인증되지 않은 상태
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        // Authentication의 Principal 객체를 CustomOAuth2User로 캐스팅
        // - Principal: 현재 인증된 사용자를 나타내는 객체
        // - JWTFilter에서 CustomOAuth2User를 Principal로 설정했으므로 타입 캐스팅 가능
        // - CustomOAuth2User는 OAuth2User 인터페이스를 구현하며, UserDTO 정보를 포함
        Object principal = authentication.getPrincipal();
        
        // Principal이 CustomOAuth2User 타입이 아닌 경우 (예: "anonymousUser" 문자열인 경우)
        if (!(principal instanceof CustomOAuth2User)) {
            return ResponseEntity.status(401).build();
        }
        
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        
        // CustomOAuth2User에서 사용자 정보 추출
        // - getName(): UserDTO의 name 필드 (사용자의 실제 이름, 예: "홍길동")
        // - getUsername(): UserDTO의 username 필드 (로그인 ID 또는 OAuth2 제공자 ID)
        String name = customOAuth2User.getName();
        String username = customOAuth2User.getUsername();
        
        // 응답 데이터를 Map으로 구성
        // - 클라이언트에게 JSON 형태로 반환됨: {"name": "홍길동", "username": "user123"}
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("username", username);
        
        // 200 OK 응답과 함께 사용자 정보 반환
        return ResponseEntity.ok(userInfo);
    }
}
