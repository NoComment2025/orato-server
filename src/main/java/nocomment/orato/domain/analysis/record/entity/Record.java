package nocomment.orato.domain.analysis.record.entity;

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
@Schema(description = "레코드 결과 엔티티")
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "레코드 결과 고유 ID", example = "1")
    private Long id;


    @Schema(description = "분석 타입", example = "video")
    private String type;

    @Schema(description = "분석 아이디", example = "1")
    private Long analysis_id;


    @Schema(description = "분석 주제", example = "면접 답변")
    private String topic;

    @Schema(description = "태그", example = "발음,명확성")
    private String tags;

    @Schema(description = "생성 일시")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Schema(description = "수정 일시")
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Schema(description = "분석 요청한 유저네임")
    private String username;

    @Schema(description = "피드백 마크다운")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String feedbackMd;

    public Record(String type, String topic, String tags, Long analysis_id, String username, String feedbackMd) {
        this.type = type;
        this.topic = topic;
        this.tags = tags;
        this.analysis_id = analysis_id;
        this.username = username;
        this.feedbackMd = feedbackMd;
    }
}
