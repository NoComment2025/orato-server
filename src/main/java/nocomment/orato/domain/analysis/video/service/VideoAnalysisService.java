
package nocomment.orato.domain.analysis.video.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.domain.analysis.video.Dto.RequestDataDto;
import nocomment.orato.domain.analysis.video.entity.VideoAnalysis;
import nocomment.orato.domain.analysis.video.repository.VideoAnalysisRepository;
import nocomment.orato.global.config.OratoProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoAnalysisService {

    // 음성 분석 데이터를 데이터베이스에 저장하기 위한 Repository
    private final VideoAnalysisRepository videoAnalysisRepository;
    private final RecordRepository recordRepository;
    private final OratoProperties oratoProperties;

    public Map<String, Object> assessPronunciation(MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            
            body.add("file", resource);

            WebClient client = WebClient.builder()
                    .baseUrl(oratoProperties.getAnalysis().getBaseUrl())
                    .build();

            return client.post()
                    .uri("/video/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Failed to assess pronunciation: " + e.getMessage(), e);
        }
    }

    public void save(RequestDataDto data, String feedbackMd, String username){
        VideoAnalysis va = new VideoAnalysis(
                data.getTopic(),
                data.getTag(),
                feedbackMd,
                data.getHasTimeLimit(),
                username
        );

        if (data.getHasTimeLimit()) {
            va.setAnalyzeTime(data.getTimeLimit());
        }


        VideoAnalysis savedVa = videoAnalysisRepository.save(va);
        Long savedId = savedVa.getId();

        nocomment.orato.domain.analysis.record.entity.Record rec = new Record("video", data.getTopic(), data.getTag(), savedId, username, feedbackMd);
        recordRepository.save(rec);

    }
}
