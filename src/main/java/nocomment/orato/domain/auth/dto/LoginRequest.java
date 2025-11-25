package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "사용자명", example = "user123", required = true)
    private String username;

    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;
}
