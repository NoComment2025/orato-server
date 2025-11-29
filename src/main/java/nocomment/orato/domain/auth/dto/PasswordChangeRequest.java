package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class PasswordChangeRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    @Schema(description = "현재 비밀번호", example = "oldPassword123!")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "새 비밀번호", example = "newPassword123!")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    @Schema(description = "새 비밀번호 확인", example = "newPassword123!")
    private String confirmPassword;
}
