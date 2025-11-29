package nocomment.orato.domain.analysis.record.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import nocomment.orato.domain.analysis.sound.repository.SoundAnalysisRepository;
import nocomment.orato.domain.analysis.video.entity.VideoAnalysis;
import nocomment.orato.domain.analysis.video.repository.VideoAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordSyncService {

    private final RecordRepository recordRepository;
    private final SoundAnalysisRepository soundAnalysisRepository;
    private final VideoAnalysisRepository videoAnalysisRepository;

    @PostConstruct
    @Transactional
    public void syncFeedbackMd() {
        log.info("Starting feedbackMd sync for existing records...");
        
        List<Record> records = recordRepository.findAll();
        int updatedCount = 0;

        for (Record record : records) {
            if (record.getFeedbackMd() == null) {
                String feedbackMd = null;
                
                if ("sound".equals(record.getType())) {
                    Optional<SoundAnalysis> soundAnalysis = soundAnalysisRepository.findById(record.getAnalysis_id());
                    if (soundAnalysis.isPresent()) {
                        feedbackMd = soundAnalysis.get().getFeedbackMd();
                    }
                } else if ("video".equals(record.getType())) {
                    Optional<VideoAnalysis> videoAnalysis = videoAnalysisRepository.findById(record.getAnalysis_id());
                    if (videoAnalysis.isPresent()) {
                        feedbackMd = videoAnalysis.get().getFeedbackMd();
                    }
                }
                
                if (feedbackMd != null) {
                    record.setFeedbackMd(feedbackMd);
                    recordRepository.save(record);
                    updatedCount++;
                    log.info("Updated Record ID: {} with feedbackMd", record.getId());
                }
            }
        }
        
        log.info("Finished syncing feedbackMd. Updated {} records.", updatedCount);
    }
}
