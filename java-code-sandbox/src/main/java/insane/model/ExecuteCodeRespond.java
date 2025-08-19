package insane.model;

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
    private List<String> output;
    /**
     * 执行信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
