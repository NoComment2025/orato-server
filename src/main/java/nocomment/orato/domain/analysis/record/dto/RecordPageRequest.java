package nocomment.orato.domain.analysis.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레코드 페이지 요청 DTO")
public class RecordPageRequest {

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    private int page = 0;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    private int size = 10;

    @Schema(description = "정렬 기준", example = "createdDate", defaultValue = "createdDate")
    private String sort = "createdDate";

    @Schema(description = "정렬 방향 (asc/desc)", example = "desc", defaultValue = "desc")
    private String direction = "desc";

    // DTO to Pageable 변환
    public Pageable toPageable() {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
