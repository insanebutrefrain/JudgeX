package insane.judgeX.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目提交状态枚举
 */
public enum QuestionSubmitStatusEnum {


    WAITING("等待中", 0),

    RUNNING("判题中", 1),

    SUCCEED("成功", 2),

    CompileError("编译错误", 3),

    TIME_LIMIT_EXCEEDED("运行超时", 4),

    MEMORY_LIMIT_EXCEEDED("内存超限", 5),

    STACK_LIMIT_EXCEEDED("堆栈超限", 6),

    WRONG_ANSWER("答案错误", 7),

    RUNTIME_ERROR("运行错误", 8);


    private final String text;

    private final Integer value;

    // 添加构造函数
    QuestionSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (QuestionSubmitStatusEnum anEnum : QuestionSubmitStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    // 添加getter方法
    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }

}
