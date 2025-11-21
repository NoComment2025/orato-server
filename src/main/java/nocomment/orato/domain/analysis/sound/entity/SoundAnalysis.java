package nocomment.orato.domain.analysis.sound.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SoundAnalysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String type = "sound";

    private String tags;

    private Boolean hasTimeLimit;

    private int analyzeTime;

    private String uuid;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

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
