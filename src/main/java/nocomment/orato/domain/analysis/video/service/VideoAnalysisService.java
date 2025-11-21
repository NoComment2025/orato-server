package nocomment.orato.domain.analysis.video.service;

import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.video.repository.VideoAnalysisRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
public class VideoAnalysisService {

    // 음성 분석 데이터를 데이터베이스에 저장하기 위한 Repository
    private final VideoAnalysisRepository videoAnalysisRepository;

    private final WebClient client = WebClient.builder()
            .baseUrl("http://127.0.0.1:8002")
            .build();

    public Map<String, Object> assessPronunciation(MultipartFile file) {
        try {
            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();
            
            // 2단계: 외부 API URL 설정
            String url = "http://localhost:8002/video/analyze";

            // HTTP 요청 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 요청 바디(Body) 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // MultipartFile을 ByteArrayResource로 변환
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            
            // 요청 바디에 파일 추가
            body.add("file", resource);
            
            // HTTP 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // POST 요청 전송 및 응답 받기
            Map<String, Object> response = client.post()
                    .uri("/video/analyze")
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
}
