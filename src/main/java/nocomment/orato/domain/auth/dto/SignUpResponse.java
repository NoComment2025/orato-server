package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "회원가입 응답")
public class SignUpResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명", example = "user123")
    private String username;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "권한", example = "ROLE_USER")
    private String role;
}
