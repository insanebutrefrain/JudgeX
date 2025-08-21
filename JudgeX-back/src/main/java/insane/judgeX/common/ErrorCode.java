package insane.judgeX.common;

/**
 * 自定义错误码枚举类
 * 用于统一管理系统中各种业务异常的状态码和错误信息
 */
public enum ErrorCode {

    /**
     * 操作成功
     */
    SUCCESS(0, "ok"),

    /**
     * 请求参数错误
     */
    PARAMS_ERROR(40000, "请求参数错误"),

    /**
     * 用户未登录错误
     */
    NOT_LOGIN_ERROR(40100, "未登录"),

    /**
     * 用户无权限错误
     */
    NO_AUTH_ERROR(40101, "无权限"),

    /**
     * 请求数据不存在错误
     */
    NOT_FOUND_ERROR(40400, "请求数据不存在"),

    /**
     * 禁止访问错误
     */
    FORBIDDEN_ERROR(40300, "禁止访问"),

    /**
     * 系统内部异常
     */
    SYSTEM_ERROR(50000, "系统内部异常"),

    /**
     * 操作失败
     */
    OPERATION_ERROR(50001, "操作失败"),

    /**
     * 调用外部接口错误
     */
    API_REQUEST_ERROR(50005, "调用接口错误");

    /**
     * 错误状态码
     * 用于标识错误类型，便于前端处理和用户识别
     */
    private final int code;

    /**
     * 错误信息描述
     * 提供对错误的详细说明，便于调试和用户理解
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code 错误状态码
     * @param message 错误信息描述
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误状态码
     *
     * @return 错误状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误信息描述
     *
     * @return 错误信息描述
     */
    public String getMessage() {
        return message;
    }

}
