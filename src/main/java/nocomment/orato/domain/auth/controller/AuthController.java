package nocomment.orato.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.auth.dto.LoginRequest;
import nocomment.orato.domain.auth.dto.LoginResponse;
import nocomment.orato.domain.auth.dto.SignUpRequest;
import nocomment.orato.domain.auth.dto.SignUpResponse;
import nocomment.orato.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 API (회원가입, 로그인)")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "사용자명과 비밀번호를 사용하여 새로운 계정을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = SignUpResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (사용자명 중복, 유효성 검사 실패)")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        try {
            SignUpResponse response = authService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
            summary = "로그인",
            description = "사용자명과 비밀번호로 로그인하고 JWT 토큰을 Cookie에 담아 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "사용자명 또는 비밀번호가 올바르지 않음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);
            
            // JWT 토큰을 Cookie에 저장
            Cookie cookie = new Cookie("Authorization", loginResponse.getToken());
            cookie.setMaxAge(3 * 60 * 60); // 3시간
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            // cookie.setSecure(true); // HTTPS 사용 시 활성화
            
            response.addCookie(cookie);
            
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
