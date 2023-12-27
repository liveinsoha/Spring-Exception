package hello.exception.servlet;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * 오류 페이지 요청 흐름**
 * WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500)-> View
 *
 * **예외 발생과 오류 페이지 요청 흐름**
 * 1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
 * 2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View
 *
 * **중요한 점은 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다. 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.**
 */

@Controller
@Slf4j
public class ServletExController {

    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        log.info("error404");
        response.sendError(404,"404 에러");
    }

    @GetMapping("/error-400")
    public void error400(HttpServletResponse response) throws IOException {
        log.info("error400");
        response.sendError(400,"400 에러");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        log.info("error500");
        response.sendError(500,"500 에러");
    }
}

