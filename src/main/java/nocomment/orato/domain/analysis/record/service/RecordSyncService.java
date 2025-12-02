package nocomment.orato.domain.analysis.record.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RecordSyncService {

    public void syncFeedbackMd() {
        log.info("Feedback sync on startup is disabled.");
    }
}
