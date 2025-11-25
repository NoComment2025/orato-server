package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청")
public class SignUpRequest {

    @Schema(description = "사용자명 (로그인 ID로 사용, 영문/숫자)", example = "user123", required = true)
    private String username;

    @Schema(description = "비밀번호 (최소 8자)", example = "password123!", required = true)
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    private String name;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;
}
