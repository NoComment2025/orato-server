package nocomment.orato.domain.analysis.video.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "비디오 분석 요청 데이터")
public class RequestDataDto {
    
    @Schema(description = "분석할 주제", example = "프레젠테이션 발표")
    private String topic;
    
    @Schema(description = "태그 (콤마로 구분)", example = "발음,억양,속도")
    private String tag;
    
    @Schema(description = "시간 제한 여부", example = "true")
    private Boolean hasTimeLimit;
    
    @Schema(description = "시간 제한 (초 단위)", example = "300")
    private int timeLimit;
}
