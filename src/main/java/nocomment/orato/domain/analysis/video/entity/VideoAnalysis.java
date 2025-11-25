package nocomment.orato.domain.analysis.video.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "비디오 분석 결과 엔티티")
public class VideoAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "분석 결과 고유 ID", example = "1")
    private Long id;

    @Schema(description = "분석 주제", example = "프레젠테이션 발표")
    private String topic;

    @Schema(description = "분석 타입", example = "video")
    private String type = "video";

    @Schema(description = "태그", example = "발음,억양,속도")
    private String tags;

    @Schema(description = "시간 제한 여부", example = "true")
    private Boolean hasTimeLimit;

    @Schema(description = "분석 소요 시간 (초)", example = "300")
    private int analyzeTime;

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

    public VideoAnalysis(String topic, String tags, String feedbackMD, Boolean hasTimeLimit) {
        this.topic = topic;
        this.tags = tags;
        this.feedbackMd = feedbackMD;
        this.hasTimeLimit = hasTimeLimit;
    }

}
