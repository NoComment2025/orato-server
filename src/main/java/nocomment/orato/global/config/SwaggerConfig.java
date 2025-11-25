package nocomment.orato.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.COOKIE)
                        .description("JWT 토큰을 입력하세요 (Cookie: Authorization)"));

        return new OpenAPI()
                .info(new Info()
                        .title("Orato API")
                        .description("Orato 프로젝트 API 명세서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
