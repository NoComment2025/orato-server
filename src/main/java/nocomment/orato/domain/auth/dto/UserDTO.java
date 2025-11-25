package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 정보 DTO")
public class UserDTO {

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private String role;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "사용자명 (OAuth2 제공자 ID)", example = "google_123456789")
    private String username;
}
