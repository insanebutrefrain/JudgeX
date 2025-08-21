package insane.judgeX.common;

/**
 * 返回工具类
 * 提供统一的API响应结果封装方法，用于生成标准化的接口返回结果
 */
public class ResultUtils {

    /**
     * 构建成功的响应结果
     *
     * @param data 响应数据，可以是任意类型的数据对象
     * @param <T>  泛型参数，表示数据的具体类型
     * @return BaseResponse<T> 成功的响应对象，状态码为0，消息为"ok"
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 构建失败的响应结果（基于错误码枚举）
     *
     * @param errorCode 错误码枚举对象，包含错误码和错误信息
     * @return BaseResponse 失败的响应对象，使用错误码中的状态码和消息
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 构建失败的响应结果（基于状态码和消息）
     *
     * @param code    自定义错误状态码
     * @param message 自定义错误消息
     * @return BaseResponse 失败的响应对象，数据部分为null
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 构建失败的响应结果（基于错误码枚举和自定义消息）
     *
     * @param errorCode 错误码枚举对象，用于获取状态码
     * @param message   自定义错误消息，覆盖错误码中的默认消息
     * @return BaseResponse 失败的响应对象，使用错误码的状态码和自定义消息
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}

