package hello.exception.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();




    /**
     * ExceptionResolver` 를 사용하면 컨트롤러에서 예외가 발생해도 `ExceptionResolver` 에서 예외를 처리해버린다.
     * 따라서 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고, 스프링 MVC에서 예외 처리는 끝이 난다.
     * 결과적으로 WAS 입장에서는 정상 처리가 된 것이다. 이렇게 예외를 이곳에서 모두 처리할 수 있다는 것이 핵심이다.
     * 서블릿 컨테이너까지 예외가 올라가면 복잡하고 지저분하게 추가 프로세스가 실행된다. 반면에 ExceptionResolver` 를 사용하면 예외처리가 상당히 깔끔해진다.
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof UserException) {
                log.info("userException resolve to 400");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //상태코드 변경 400으로
                String accept = request.getHeader("accept");

                if (accept.equals("application/json")) { //accept헤더가 JSON형식인 경우
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);//맵을 Json형식으로 변경
                    //객체를 문자로 바꾸어 준다
                    response.setContentType("application/json"); //에러에 관한 데이터를 쓴다
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(result);//HTTP응답 바디에 Json형식이 들어간다.

                    return new ModelAndView();//빈 ModelAndView를 반환하는 경우 정상적으로 서블릿 컨테이너까지 response가 정상적으로 전달된다.
                } else {//accept헤더가 JSON형식이 아닌 경우

                    return new ModelAndView("error/500");//viewResolver 가서 해당 뷰를 렌더링한다.
                }

            }
        } catch (Exception e) {
            log.error("resolver ex", e);
            throw new RuntimeException(e);
        }
        return null;
    }


}
