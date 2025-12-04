package nocomment.orato.domain.analysis.sound.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "음성 분석 요청 데이터")
public class RequestDataDto {
    
    @NotBlank(message = "주제는 필수입니다.")
    @Size(max = 100, message = "주제는 100자 이하여야 합니다.")
    @Schema(description = "분석할 주제", example = "면접 답변")
    private String topic;
    
    @NotBlank(message = "태그는 필수입니다.")
    @Size(max = 100, message = "태그는 100자 이하여야 합니다.")
    @Schema(description = "태그 (콤마로 구분)", example = "발음,명확성")
    private String tag;
    
    @NotNull(message = "시간 제한 여부는 필수입니다.")
    @Schema(description = "시간 제한 여부", example = "true")
    private Boolean hasTimeLimit;
    
    @PositiveOrZero(message = "시간 제한은 0 이상이어야 합니다.")
    @Schema(description = "시간 제한 (초 단위)", example = "180")
    private int timeLimit;
}
