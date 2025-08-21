package insane.implNative.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRespond {
    /**
     * 执行结果
     */
    private List<String> output;
    /**
     * 执行信息
     */
    private String message;

    /**
     * 执行状态
     * 0 表示正常退出 1 表示编译失败 2 运行错误 3 运行超时 4 运行内存超限 5 堆栈超限 6 执行错误
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
