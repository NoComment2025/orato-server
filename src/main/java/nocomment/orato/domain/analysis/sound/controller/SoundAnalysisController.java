
package nocomment.orato.domain.analysis.sound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.sound.Dto.RequestDataDto;
import nocomment.orato.domain.analysis.dto.Status;
import nocomment.orato.domain.analysis.sound.entity.SoundAnalysis;
import nocomment.orato.domain.analysis.sound.repository.SoundAnalysisRepository;
import nocomment.orato.domain.analysis.sound.service.SoundAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
@RestController
@RequiredArgsConstructor
public class SoundAnalysisController {

    private final SoundAnalysisRepository soundAnalysisRepository;
    private final SoundAnalysisService soundAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/analyze/sound", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Status> uploadSound(
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

        // 2) DTO 데이터 파싱
        String topic = data.getTopic();
        String tags = data.getTag();
        Boolean hasTimeLimit = data.getHasTimeLimit();

        // 3) 사운드 분석 요청
        System.out.println("요청보냈음");
        Map<String, Object> response = soundAnalysisService.assessPronunciation(file);

        String response_uuid = (String) response.get("uuid");
        String response_feedbackMd = (String) response.get("feedback_md");


        // 4. DB 저장
        soundAnalysisService.save(data, response_uuid, response_feedbackMd);

        // 5) 응답 반환
        return ResponseEntity.ok(new Status(200));
    }


    @GetMapping("/analyze/sound/{id}")
    public ResponseEntity<SoundAnalysis> fetchRecords(@PathVariable Long id) {

        Optional<SoundAnalysis> sa = soundAnalysisRepository.findById(id);
        if (sa.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sa.get());
    }


}
