package nocomment.orato.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 응답 DTO")
public class PasswordChangeResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "메시지", example = "비밀번호가 성공적으로 변경되었습니다.")
    private String message;

    public static PasswordChangeResponse success() {
        return new PasswordChangeResponse(true, "비밀번호가 성공적으로 변경되었습니다.");
    }

    public static PasswordChangeResponse fail(String message) {
        return new PasswordChangeResponse(false, message);
    }
}
