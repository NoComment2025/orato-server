package nocomment.orato.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "orato")
public class OratoProperties {

    private final Analysis analysis = new Analysis();
    private final Frontend frontend = new Frontend();

    @Getter
    @Setter
    public static class Analysis {
        private String baseUrl = "http://localhost:8000";
    }

    @Getter
    @Setter
    public static class Frontend {
        private String redirectUrl = "http://localhost:5173/";
    }
}
