package hello.exception.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class ErrorPageController {

    //RequestDispatcher 상수로 정의되어 있음
    public static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "jakarta.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";

    @RequestMapping("/error-page/404")//Get, Post등 한 번에 처리한다.
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage404");
        printErrorInfo(request);
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage500");
        printErrorInfo(request);
        return "error-page/500";
    }

    /**
     * response.sendError(404)` : `errorPage404` 호출
     * `response.sendError(500)` : `errorPage500` 호출
     * `RuntimeException` 또는 그 자식 타입의 예외: `errorPageEx` 호출
     * 500 예외가 서버 내부에서 발생한 오류라는 뜻을 포함하고 있기 때문에 여기서는 예외가 발생한 경우도 500 오류 화면
     * 으로 처리했다.
     */

    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION: ex= {}", request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE: {}", request.getAttribute(ERROR_EXCEPTION_TYPE));
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE)); //ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE));
        log.info("dispatchType={}", request.getDispatcherType());
    }

    /**
     * WAS는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라, 오류 정보를 `request` 의 `attribute` 에 추가해서 넘겨준다.
     * 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.
     * request.attribute에 서버가 담아준 정보**
     * `javax.servlet.error.exception` : 예외
     * `javax.servlet.error.exception_type` : 예외 타입
     * `javax.servlet.error.message` : 오류 메시지
     * `javax.servlet.error.request_uri` : 클라이언트 요청 URI
     * `javax.servlet.error.servlet_name` : 오류가 발생한 서블릿 이름
     * `javax.servlet.error.status_code` : HTTP 상태 코드
     */


    /**
     * produces = MediaType.APPLICATION_JSON_VALUE` 의 뜻은 클라이언트가 요청하는 HTTP Header의
     * `Accept` 의 값이 `application/json` 일 때 해당 메서드가 호출된다는 것이다. 결국 클라어인트가 받고 싶은 미디어
     * 타입이 json이면 이 컨트롤러의 메서드가 호출된다.
     * 응답 데이터를 위해서 `Map` 을 만들고 `status` , `message` 키에 값을 할당했다. Jackson 라이브러리는 `Map` 을
     * JSON 구조로 변환할 수 있다.
     * `ResponseEntity` 를 사용해서 응답하기 때문에 메시지 컨버터가 동작하면서 클라이언트에 JSON이 반환된다.
     */
    @RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage500Api");

        Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
        Map<String, Object> result = new HashMap<>();
        result.put("status", request.getAttribute(ERROR_STATUS_CODE));
        result.put("message", ex.getMessage());

        Integer statusCode = (Integer) request.getAttribute(ERROR_STATUS_CODE);
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }
    //객체를 사용하는 게 더 낫긴 한데 예제니까 맵을 사용..


}


