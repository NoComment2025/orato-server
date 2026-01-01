package nocomment.orato.domain.analysis.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.dto.Status;
import nocomment.orato.domain.analysis.record.dto.PageResponse;
import nocomment.orato.domain.analysis.record.dto.RecordListResponse;
import nocomment.orato.domain.analysis.record.dto.RecordPageRequest;
import nocomment.orato.domain.analysis.record.dto.RecordResponse;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.global.auth.CurrentUserResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Record", description = "분석 결과 레코드 API")
public class RecordController {
    private static final int MAX_FULL_RECORDS = 100;

    private final RecordRepository recordRepository;
    private final CurrentUserResolver currentUserResolver;

    @Operation(
            summary = "분석 결과 레코드 목록 조회 (전체)",
            description = "현재 로그인한 사용자의 분석 결과(Sound/Video)가 100건 이하일 때 전체 조회합니다. 100건을 초과하면 /records/page를 사용해야 합니다. JWT 인증이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "전체 조회 한도 초과"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/records")
    public ResponseEntity<?> getRecordList() {
        String username = currentUserResolver.getCurrentUsername();

        Page<Record> records = recordRepository.findByUsername(username,
                PageRequest.of(0, MAX_FULL_RECORDS + 1, Sort.by(Sort.Direction.DESC, "createdDate", "id")));
        if (records.getTotalElements() > MAX_FULL_RECORDS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Status(400, "전체 조회는 100건까지 가능합니다. /records/page를 사용하세요."));
        }

        // Entity를 DTO로 변환
        List<RecordResponse> recordResponses = records.getContent().stream()
                .map(RecordResponse::from)
                .collect(Collectors.toList());

        // RecordListResponse로 변환하여 반환
        RecordListResponse response = RecordListResponse.from(recordResponses);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "분석 결과 레코드 목록 조회 (페이지네이션)",
            description = "현재 로그인한 사용자의 모든 분석 결과(Sound/Video)를 페이지네이션하여 조회합니다. 서버 사이드 페이지네이션을 사용합니다. JWT 인증이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/records/page")
    public ResponseEntity<PageResponse<RecordResponse>> getRecordListPaged(
            @ModelAttribute RecordPageRequest request) {
        String username = currentUserResolver.getCurrentUsername();

        // username으로 필터링하여 페이지네이션 조회
        Page<Record> recordPage = recordRepository.findByUsername(username, request.toPageable());

        // Entity를 DTO로 변환
        Page<RecordResponse> responsePage = recordPage.map(RecordResponse::from);

        // PageResponse로 변환하여 반환
        PageResponse<RecordResponse> response = PageResponse.from(responsePage);

        return ResponseEntity.ok(response);
    }

}
