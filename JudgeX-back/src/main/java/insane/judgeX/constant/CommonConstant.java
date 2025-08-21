package insane.judgeX.constant;

/**
 * 通用常量接口
 * 定义项目中通用的常量值，避免硬编码，提高代码可维护性
 */
public interface CommonConstant {

    /**
     * 排序方式：升序
     * 用于数据库查询中的ORDER BY子句，表示按升序排列
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 排序方式：降序
     * 用于数据库查询中的ORDER BY子句，表示按降序排列
     * 注意：值前后有空格，可能存在拼写错误，应为"descend"
     */
    String SORT_ORDER_DESC = " descend";

}

