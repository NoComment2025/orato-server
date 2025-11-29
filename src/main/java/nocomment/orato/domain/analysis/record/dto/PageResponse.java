package nocomment.orato.domain.analysis.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지네이션 응답 DTO")
public class PageResponse<T> {

    @Schema(description = "현재 페이지 데이터 목록")
    private List<T> content;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int pageNumber;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;

    @Schema(description = "전체 데이터 개수", example = "50")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    @Schema(description = "비어있는 페이지 여부", example = "false")
    private boolean empty;

    // Page to PageResponse 변환
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
