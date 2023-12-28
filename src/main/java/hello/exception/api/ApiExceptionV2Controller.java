package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exeptionHandler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//MVC에선 잘 안쓰고 API통신에서 사용한다.
@RestController
@Slf4j
public class ApiExceptionV2Controller {

    /**
     * 웹 브라우저에 HTML 화면을 제공할 때는 오류가 발생하면 `BasicErrorController` 를 사용하는게 편하다.
     * 이때는 단순히 5xx, 4xx 관련된 오류 화면을 보여주면 된다. `BasicErrorController` 는 이런 메커니즘을 모두 구현해두었다.
     * 그런데 API는 각 시스템 마다 응답의 모양도 다르고, 스펙도 모두 다르다. 예외 상황에 단순히 오류 화면을 보여주는 것
     * 이 아니라, 예외에 따라서 각각 다른 데이터를 출력해야 할 수도 있다. 그리고 같은 예외라고 해도 어떤 컨트롤러에서 발
     * 생했는가에 따라서 다른 예외 응답을 내려주어야 할 수 있다. 한마디로 매우 세밀한 제어가 필요하다.
     * 앞서 이야기했지만, 예를 들어서 상품 API와 주문 API는 오류가 발생했을 때 응답의 모양이 완전히 다를 수 있다.
     *
     * **@ExceptionHandler**
     * 스프링은 API 예외 처리 문제를 해결하기 위해 `@ExceptionHandler` 라는 애노테이션을 사용하는 매우 편리한 예외
     * 처리 기능을 제공하는데, 이것이 바로 `ExceptionHandlerExceptionResolver` 이다. 스프링은
     * `ExceptionHandlerExceptionResolver` 를 기본으로 제공하고, 기본으로 제공하는 `ExceptionResolver`
     * 중에 우선순위도 가장 높다. 실무에서 API 예외 처리는 대부분 이 기능을 사용한다.
     */


    /**
     * 응답흐름은 왔다갔다 하는 것이 아닌 정상흐름으로 마무리된다.
     * 컨트롤러를 호출한 결과 `IllegalArgumentException` 예외가 컨트롤러 밖으로 던져진다.
     * 예외가 발생했으로 `ExceptionResolver` 가 작동한다. 가장 우선순위가 높은
     * `ExceptionHandlerExceptionResolver` 가 실행된다.
     * `ExceptionHandlerExceptionResolver` 는 해당 컨트롤러에 `IllegalArgumentException` 을 처리
     * 할 수 있는 `@ExceptionHandler` 가 있는지 확인한다.
     * `illegalExHandle()` 를 실행한다. `@RestController` 이므로 `illegalExHandle()` 에도
     * `@ResponseBody` 가 적용된다. 따라서 HTTP 컨버터가 사용되고, 응답이 다음과 같은 JSON으로 반환된다.
     * `@ResponseStatus(HttpStatus.BAD_REQUEST)` 를 지정했으므로 HTTP 상태 코드 400으로 응답한다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * 정상적으로 예외를 잡고 ErrorResult예외를 리턴하였으므로 @ResponseStatus가 없다면 상태코드는 200이다.
     * 다만 이떄 상태코드를 변경하고 싶으면 @ResponseStatus 어노테이션을 해주면 된다
     */
    public ErrorResult illegalArgument(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex : ", e);
        return new ErrorResult("bad", e.getMessage());
    }


    /**
     * `@ExceptionHandler` 에 예외를 지정하지 않으면 해당 메서드 파라미터 예외를 사용한다. 여기서는
     * `UserException` 을 사용한다.
     * `ResponseEntity` 를 사용해서 HTTP 메시지 바디에 직접 응답한다. 물론 HTTP 컨버터가 사용된다.
     * `ResponseEntity` 를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다. 앞서 살펴본
     * `@ResponseStatus` 는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다.
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userException(UserException e) {
        log.error("[exceptionHandler] ex : ", e);
        ErrorResult errorResult = new ErrorResult("user-exception", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exception(Exception e) {
        log.error("[exceptionHandler] ex : ", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }
        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }


}
