package nocomment.orato.domain.analysis.video.entity;

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
public class VideoAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String type = "video";

    private String tags;

    private Boolean hasTimeLimit;

    private int analyzeTime;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

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
