
package nocomment.orato.domain.analysis.video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.dto.Status;
import nocomment.orato.domain.analysis.video.Dto.RequestDataDto;
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
@Tag(name = "Video Analysis", description = "비디오 분석 API")
public class VideoAnalysisController {

    private final VideoAnalysisRepository videoAnalysisRepository;
    private final VideoAnalysisService videoAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(
            summary = "비디오 분석 요청",
            description = "비디오 파일과 메타데이터를 업로드하여 발음 분석을 수행합니다. JWT 인증이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "분석 성공", 
                    content = @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (data 파트 누락 또는 JSON 파싱 오류)"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping(value = "/analyze/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Status> uploadVideo(
            @Parameter(description = "분석할 비디오 파일", required = true)
            @RequestPart(value = "file", required = true) MultipartFile file,
            @Parameter(description = "분석 메타데이터 (JSON 형식)", schema = @Schema(implementation = RequestDataDto.class))
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


    @Operation(
            summary = "비디오 분석 결과 조회",
            description = "ID를 통해 저장된 비디오 분석 결과를 조회합니다. JWT 인증이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = VideoAnalysis.class))),
            @ApiResponse(responseCode = "404", description = "분석 결과를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/analyze/video/{id}")
    public ResponseEntity<VideoAnalysis> fetchRecords(
            @Parameter(description = "조회할 분석 결과 ID", required = true)
            @PathVariable Long id) {

        Optional<VideoAnalysis> sa = videoAnalysisRepository.findById(id);
        if (sa.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(sa.get());
    }


}
