package nocomment.orato.domain.analysis.record.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nocomment.orato.domain.analysis.record.entity.Record;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레코드 응답 DTO")
public class RecordResponse {

    @Schema(description = "레코드 고유 ID", example = "1")
    private Long id;

    @Schema(description = "분석 타입 (sound/video)", example = "sound")
    private String type;

    @Schema(description = "분석 결과 ID", example = "5")
    @JsonProperty("analysis_id")
    private Long analysisId;

    @Schema(description = "분석 주제", example = "면접 답변")
    private String topic;

    @Schema(description = "태그", example = "pre")
    private String tag;

    @Schema(description = "상태", example = "분석 완료됨")
    private String status;

    @Schema(description = "생성 일시", example = "2025-11-29T10:30:00")
    private LocalDateTime createdDate;

    @Schema(description = "수정 일시", example = "2025-11-29T10:30:00")
    private LocalDateTime updatedDate;

    @Schema(description = "사용자 이름", example = "user123")
    private String username;

    @Schema(description = "피드백 마크다운")
    private String feedbackMd;

    // Entity to DTO 변환
    public static RecordResponse from(Record record) {
        return new RecordResponse(
                record.getId(),
                record.getType(),
                record.getAnalysis_id(),
                record.getTopic(),
                record.getTags(),
                "분석 완료됨", // 기본값으로 "분석 완료됨" 설정 (추후 로직 추가 가능)
                record.getCreatedDate(),
                record.getUpdatedDate(),
                record.getUsername(),
                record.getFeedbackMd()
        );
    }
}
