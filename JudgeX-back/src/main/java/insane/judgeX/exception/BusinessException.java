package insane.judgeX.exception;

import insane.judgeX.common.ErrorCode;

/**
 * 自定义业务异常类
 * 继承自RuntimeException，用于处理业务逻辑中的异常情况
 * 包含错误码和错误信息，便于统一异常处理和返回给前端
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     * 用于标识异常的类型，便于前端根据错误码进行不同的处理
     */
    private final int code;

    /**
     * 构造函数：根据错误码和错误信息创建业务异常
     *
     * @param code 错误码
     * @param message 错误信息描述
     */
    public BusinessException(int code, String message) {
        super(message);  // 调用父类构造函数设置异常信息
        this.code = code;  // 设置错误码
    }

    /**
     * 构造函数：根据ErrorCode枚举创建业务异常
     *
     * @param errorCode 错误码枚举对象，包含错误码和默认错误信息
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());  // 使用枚举中的默认错误信息
        this.code = errorCode.getCode();  // 使用枚举中的错误码
    }

    /**
     * 构造函数：根据ErrorCode枚举和自定义错误信息创建业务异常
     *
     * @param errorCode 错误码枚举对象，提供错误码
     * @param message 自定义错误信息，覆盖枚举中的默认信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);  // 使用自定义错误信息
        this.code = errorCode.getCode();  // 使用枚举中的错误码
    }

    /**
     * 获取错误码
     *
     * @return int 返回异常对应的错误码
     */
    public int getCode() {
        return code;
    }
}

