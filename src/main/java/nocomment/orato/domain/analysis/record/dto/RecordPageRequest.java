package nocomment.orato.domain.analysis.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;

    @Schema(description = "페이지 크기 (1~100)", example = "10", defaultValue = "10")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    private int size = 10;

    @Schema(description = "정렬 기준", example = "createdDate", defaultValue = "createdDate")
    @Pattern(regexp = "id|createdDate|updatedDate|type|topic|tags", message = "지원하지 않는 정렬 기준입니다.")
    private String sort = "createdDate";

    @Schema(description = "정렬 방향 (asc/desc)", example = "desc", defaultValue = "desc")
    @Pattern(regexp = "(?i)(asc|desc)", message = "정렬 방향은 asc 또는 desc여야 합니다.")
    private String direction = "desc";

    // DTO to Pageable 변환
    public Pageable toPageable() {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }
}
