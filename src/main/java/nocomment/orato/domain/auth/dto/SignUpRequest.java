package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청")
public class SignUpRequest {

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 4, max = 20, message = "사용자명은 4자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자명은 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    @Schema(description = "사용자명 (로그인 ID로 사용, 영문/숫자)", example = "user123", required = true)
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다.")
    @Schema(description = "비밀번호 (최소 8자)", example = "password123!", required = true)
    private String password;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(max = 50, message = "사용자 이름은 50자 이하여야 합니다.")
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
}
