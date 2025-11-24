
package nocomment.orato.domain.analysis.video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.video.Dto.RequestDataDto;
import nocomment.orato.domain.analysis.video.Dto.Status;
import nocomment.orato.domain.analysis.video.entity.VideoAnalysis;
import nocomment.orato.domain.analysis.video.repository.VideoAnalysisRepository;
import nocomment.orato.domain.analysis.video.service.VideoAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class VideoAnalysisController {

    private final VideoAnalysisRepository videoAnalysisRepository;
    private final VideoAnalysisService videoAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/analyze/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Status> uploaVideo(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "data", required = false) String dataJson
    ) {

        System.out.println("요청들어옴");

        // 1) JSON → DTO 변환
        RequestDataDto data;
        try {
            if (dataJson == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Status(400, "data 파트가 비어있습니다."));
            }

            data = objectMapper.readValue(dataJson, RequestDataDto.class);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Status(400, "JSON 파싱 오류"));
        }

        // 3) 사운드 분석 요청
        System.out.println("요청보냈음");
        Map<String, Object> response = videoAnalysisService.assessPronunciation(file);

        String response_feedbackMd = (String) response.get("feedback_md");

        // 4) DB 저장
        videoAnalysisService.save(data, response_feedbackMd);

        // 5) 응답 반환
        return ResponseEntity.ok(new Status(200));
    }


    @GetMapping("/analyze/video/{id}")
    public ResponseEntity<VideoAnalysis> fetchRecords(@PathVariable Long id) {

        Optional<VideoAnalysis> sa = videoAnalysisRepository.findById(id);
        if (sa.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sa.get());
    }


}
