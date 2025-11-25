package nocomment.orato.domain.analysis.sound.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "음성 분석 요청 데이터")
public class RequestDataDto {
    
    @Schema(description = "분석할 주제", example = "면접 답변")
    private String topic;
    
    @Schema(description = "태그 (콤마로 구분)", example = "발음,명확성")
    private String tag;
    
    @Schema(description = "시간 제한 여부", example = "true")
    private Boolean hasTimeLimit;
    
    @Schema(description = "시간 제한 (초 단위)", example = "180")
    private int timeLimit;
}
