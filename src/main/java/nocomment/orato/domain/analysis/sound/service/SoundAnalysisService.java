package nocomment.orato.domain.analysis.sound.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.domain.analysis.sound.Dto.RequestDataDto;
import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import nocomment.orato.domain.analysis.sound.repository.SoundAnalysisRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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

    private final WebClient client = WebClient.builder()
            .baseUrl("http://58.237.36.59:8000")
            .build();

    public Map<String, Object> assessPronunciation(MultipartFile file) {
        try {
            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();
            
            // 외부 API URL 설정
//            String url = "http://localhost:8000/sound/analyze";
            String url = "http://58.237.36.59:8000/sound/analyze";

            // 3단계: HTTP 요청 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 요청 바디(Body) 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // MultipartFile을 ByteArrayResource로 변환
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    // 원본 파일명 반환 (예: "절실히.wav")
                    return file.getOriginalFilename();
                }
            };
            
            // 요청 바디에 파일 추가
            body.add("file", resource);
            
            // HTTP 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // POST 요청 전송 및 응답 받기
            Map<String, Object> response = client.post()
                    .uri("/sound/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            return response;

        } catch (Exception e) {
            // 예외 처리
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
}
