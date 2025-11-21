package nocomment.orato.domain.analysis.video.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDataDto {
    private String topic;
    private String tag;
    private Boolean hasTimeLimit;
    private int timeLimit;
}
