package nocomment.orato.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "My", description = "사용자 개인 페이지 API")
public class MyController {

    @Operation(
            summary = "마이 페이지", 
            description = "인증된 사용자의 개인 페이지를 반환합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이 페이지 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰이 유효하지 않음")
    })
    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {

        return "my route";
    }

}
