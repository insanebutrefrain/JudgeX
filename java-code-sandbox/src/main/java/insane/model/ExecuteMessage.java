package insane.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 进程执行信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMessage {
    /**
     * 进程退出值
     */
    private Integer exitValue;

    /**
     * 进程执行信息
     */
    private String message;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行时间
     */
    private Long time;

    /**
     * 执行内存
     */
    private Long memory;

}
