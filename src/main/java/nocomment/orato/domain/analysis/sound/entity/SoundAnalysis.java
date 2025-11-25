package nocomment.orato.domain.analysis.sound.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "음성 분석 결과 엔티티")
public class SoundAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "분석 결과 고유 ID", example = "1")
    private Long id;

    @Schema(description = "분석 주제", example = "면접 답변")
    private String topic;

    @Schema(description = "분석 타입", example = "sound")
    private String type = "sound";

    @Schema(description = "태그", example = "발음,명확성")
    private String tags;

    @Schema(description = "시간 제한 여부", example = "true")
    private Boolean hasTimeLimit;

    @Schema(description = "분석 소요 시간 (초)", example = "180")
    private int analyzeTime;

    @Schema(description = "고유 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    @Schema(description = "생성 일시")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Schema(description = "수정 일시")
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Schema(description = "피드백 마크다운")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String feedbackMd;

    public SoundAnalysis(String topic, String tags, String feedbackMD, Boolean hasTimeLimit, String uuid) {
        this.topic = topic;
        this.tags = tags;
        this.feedbackMd = feedbackMD;
        this.hasTimeLimit = hasTimeLimit;
        this.uuid = uuid;
    }

}
