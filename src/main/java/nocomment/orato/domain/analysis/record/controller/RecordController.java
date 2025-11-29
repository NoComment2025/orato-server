package nocomment.orato.domain.analysis.record.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.analysis.record.dto.PageResponse;
import nocomment.orato.domain.analysis.record.dto.RecordListResponse;
import nocomment.orato.domain.analysis.record.dto.RecordPageRequest;
import nocomment.orato.domain.analysis.record.dto.RecordResponse;
import nocomment.orato.domain.analysis.record.entity.Record;
import nocomment.orato.domain.analysis.record.repository.RecordRepository;
import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Record", description = "분석 결과 레코드 API")
public class RecordController {
    private final RecordRepository recordRepository;

    @Operation(
            summary = "분석 결과 레코드 목록 조회 (전체)",
            description = "현재 로그인한 사용자의 모든 분석 결과(Sound/Video)를 조회합니다. 클라이언트에서 필터링/정렬/페이지네이션을 처리합니다. JWT 인증이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/records")
    public ResponseEntity<RecordListResponse> getRecordList() {

        // SecurityContext에서 username 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOAuth2User)) {
            return ResponseEntity.status(401).build();
        }
        
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String username = customOAuth2User.getUsername();

        // username으로 필터링하여 전체 조회
        List<Record> records = recordRepository.findByUsername(username);

        // Entity를 DTO로 변환
        List<RecordResponse> recordResponses = records.stream()
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

        // SecurityContext에서 username 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOAuth2User)) {
            return ResponseEntity.status(401).build();
        }
        
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String username = customOAuth2User.getUsername();

        // username으로 필터링하여 페이지네이션 조회
        Page<Record> recordPage = recordRepository.findByUsername(username, request.toPageable());

        // Entity를 DTO로 변환
        Page<RecordResponse> responsePage = recordPage.map(RecordResponse::from);

        // PageResponse로 변환하여 반환
        PageResponse<RecordResponse> response = PageResponse.from(responsePage);

        return ResponseEntity.ok(response);
    }

}
