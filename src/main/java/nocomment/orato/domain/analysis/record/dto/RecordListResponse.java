package nocomment.orato.domain.analysis.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레코드 목록 응답 DTO")
public class RecordListResponse {

    @Schema(description = "레코드 목록")
    private List<RecordResponse> records;

    public static RecordListResponse from(List<RecordResponse> records) {
        return new RecordListResponse(records);
    }
}
