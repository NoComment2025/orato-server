package nocomment.orato.domain.analysis.sound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.domain.analysis.sound.Dto.RequestDataDto;
import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import nocomment.orato.domain.analysis.sound.repository.SoundAnalysisRepository;
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
public class SoundAnalysisService {

    // 음성 분석 데이터를 데이터베이스에 저장하기 위한 Repository
    private final SoundAnalysisRepository soundAnalysisRepository;
    private final RecordRepository recordRepository;
    private final OratoProperties oratoProperties;

    public Map<String, Object> assessPronunciation(MultipartFile file) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl(oratoProperties.getAnalysis().getBaseUrl())
                    .build();

            return client.post()
                    .uri("/sound/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(createMultipartBody(file)))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("Failed to assess pronunciation: " + e.getMessage(), e);
        }
    }

    public void save(RequestDataDto data, String uuid, String feedbackMd, String username) {
        SoundAnalysis sa = new SoundAnalysis(
                data.getTopic(),
                data.getTag(),
                feedbackMd,
                data.getHasTimeLimit(),
                uuid,
                username
        );


        if (data.getHasTimeLimit()) {
            sa.setAnalyzeTime(data.getTimeLimit());
        }

        SoundAnalysis savedSa = soundAnalysisRepository.save(sa);
        Long savedId = savedSa.getId();

        Record rec = new Record("sound", data.getTopic(), data.getTag(), savedId, username, feedbackMd);
        recordRepository.save(rec);

    }

    private MultiValueMap<String, Object> createMultipartBody(MultipartFile file) throws java.io.IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        return body;
    }
}
