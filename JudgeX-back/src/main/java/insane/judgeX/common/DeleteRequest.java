package insane.judgeX.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求数据传输对象
 * 用于封装删除操作所需的参数信息
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 要删除记录的唯一标识符
     * 通常对应数据库表中的主键ID
     */
    private Long id;
}
