package nocomment.orato.domain.analysis.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "API 응답 상태")
public class Status {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;
    
    @Schema(description = "응답 메시지 (선택적)", example = "요청이 성공적으로 처리되었습니다.")
    private String message;



    public Status(int status) {
        this.status = status;
    }

    public Status(int status, String message) {
        this.status = status;
        this.message = message;
    }
}