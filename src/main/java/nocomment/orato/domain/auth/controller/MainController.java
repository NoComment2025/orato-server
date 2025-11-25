package nocomment.orato.domain.auth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "Main", description = "메인 페이지 API")
public class MainController {

    @Operation(summary = "메인 페이지", description = "애플리케이션의 메인 루트 페이지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 페이지 조회 성공")
    })
    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {

        return "main route";
    }
}
