package insane.implNative.exception;

public class CodeException extends RuntimeException {

    public Integer code;

    public CodeException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
