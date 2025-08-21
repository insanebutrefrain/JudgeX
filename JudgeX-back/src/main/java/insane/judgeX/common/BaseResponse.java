package insane.judgeX.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * 用于统一API接口的返回格式，包含状态码、数据和消息
 *
 * @param <T> 泛型参数，表示返回的数据类型
 */
@Data  // Lombok注解，自动生成getter、setter、toString等方法
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;  // 序列化版本号

    /**
     * 状态码
     * 用于表示请求处理的结果状态，如成功(0)、失败(非0)
     */
    private int code;

    /**
     * 返回数据
     * 实际的业务数据，类型由泛型T决定
     */
    private T data;

    /**
     * 返回消息
     * 对请求处理结果的描述信息
     */
    private String message;

    /**
     * 全参数构造方法
     *
     * @param code 状态码
     * @param data 返回数据
     * @param message 返回消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 两参数构造方法
     * 消息默认为空字符串
     *
     * @param code 状态码
     * @param data 返回数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");  // 调用三参数构造方法，消息设为空字符串
    }

    /**
     * 错误码构造方法
     * 根据错误码枚举创建返回对象，数据为null
     *
     * @param errorCode 错误码枚举
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());  // 调用三参数构造方法
    }
}
