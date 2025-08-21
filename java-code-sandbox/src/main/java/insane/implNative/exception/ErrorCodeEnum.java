package insane.implNative.exception;

import lombok.Getter;

/**
 * 判题状态枚举
 */
@Getter
public enum ErrorCodeEnum {

    WAITING("等待中", 0),

    RUNNING("判题中", 1),

    SUCCEED("成功", 2),

    CompileError("编译错误", 3),

    DANGEROUS_OPERATION("危险操作", 4),

    TIME_LIMIT_EXCEEDED("运行超时", 5),

    MEMORY_LIMIT_EXCEEDED("内存超限", 6),

    STACK_LIMIT_EXCEEDED("堆栈超限", 7),

    RUNTIME_ERROR("运行错误", 8),

    WRONG_ANSWER("答案错误", 9),

    SYSTEM_ERROR("系统错误", 10);


    private final String message;

    private final Integer code;

    /**
     * 构造函数
     *
     * @param message
     * @param code
     */
    ErrorCodeEnum(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}