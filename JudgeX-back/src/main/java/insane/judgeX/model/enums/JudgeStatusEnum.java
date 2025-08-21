package insane.judgeX.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 判题状态枚举
 */
@Getter
public enum JudgeStatusEnum {


    WAITING("等待中", 0),

    RUNNING("判题中", 1),

    SUCCEED("成功", 2),

    CompileError("编译错误", 3),

    DANGEROUS_OPERATION("危险操作", 4),

    TIME_LIMIT_EXCEEDED("运行超时", 5),

    MEMORY_LIMIT_EXCEEDED("内存超限", 6),

    STACK_LIMIT_EXCEEDED("堆栈超限", 7),

    RUNTIME_ERROR("运行错误", 8),

    WRONG_ANSWER("答案错误", 9);


    private final String text;

    private final Integer code;

    /**
     * 构造函数
     *
     * @param text
     * @param code
     */
    JudgeStatusEnum(String text, Integer code) {
        this.text = text;
        this.code = code;
    }

    /**
     * 根据code获取text
     *
     * @param code 状态码
     * @return 对应的文本描述
     */
    public static String getTextByCode(Integer code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }
        for (JudgeStatusEnum statusEnum : JudgeStatusEnum.values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum.text;
            }
        }
        return null;
    }


    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeStatusEnum anEnum : JudgeStatusEnum.values()) {
            if (anEnum.code.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
