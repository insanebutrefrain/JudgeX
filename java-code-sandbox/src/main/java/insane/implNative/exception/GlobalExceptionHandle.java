package insane.implNative.exception;

import insane.implNative.model.ExecuteCodeRespond;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(CodeException.class)
    public ExecuteCodeRespond handleCodeException(CodeException e) {
        return ExecuteCodeRespond.builder().status(e.code).message(e.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    public ExecuteCodeRespond handleSandboxException(Exception e) {
        return ExecuteCodeRespond.builder().status(ErrorCodeEnum.SYSTEM_ERROR.getCode()).message(ErrorCodeEnum.SYSTEM_ERROR.getMessage()).build();
    }
}
