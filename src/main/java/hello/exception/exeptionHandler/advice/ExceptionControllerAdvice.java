package hello.exception.exeptionHandler.advice;


import hello.exception.exception.UserException;
import hello.exception.exeptionHandler.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * // Target all Controllers annotated with @RestController
     * @ControllerAdvice(annotations = RestController.class)
     * public class ExampleAdvice1 {}
     * // Target all Controllers within specific packages
     * @ControllerAdvice("org.example.controllers")
     * public class ExampleAdvice2 {}
     * // Target all Controllers assignable to specific classes
     * @ControllerAdvice(assignableTypes = {ControllerInterface.class,
     * AbstractController.class})
     * public class ExampleAdvice3 {}
     * 스프링 공식 문서 예제에서 보는 것 처럼 특정 애노테이션이 있는 컨트롤러를 지정할 수 있고, 특정 패키지를 직접 지정
     * 할 수도 있다. 패키지 지정의 경우 해당 패키지와 그 하위에 있는 컨트롤러가 대상이 된다. 그리고 특정 클래스를 지정할 수도 있다.
     * 클래스 지정할 경우 하위의 클래스까지 모두 적용된다.
     * 대상 컨트롤러 지정을 생략하면 모든 컨트롤러에 적용된다.
     */

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult illegalExceptionHandle(IllegalArgumentException e) {
        return new ErrorResult("BAD REQUEST", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExceptionHandle(UserException e) {
        ErrorResult errorResult = new ErrorResult("BAD REQUEST", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ErrorResult exceptionHandle(Exception e) {
        return new ErrorResult("Exception", "내부 오류");
    }

}
