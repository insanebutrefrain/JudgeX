package insane.judgeX.common;

import insane.judgeX.constant.CommonConstant;
import lombok.Data;

/**
 * 分页请求参数封装类
 * 用于处理分页查询相关的参数信息
 */
@Data
public class PageRequest {

    /**
     * 当前页号，默认为第1页
     * 用于指定要查询的数据页码
     */
    private long current = 1;

    /**
     * 页面大小，默认每页显示10条数据
     * 用于指定每页返回的数据条数
     */
    private long pageSize = 10;

    /**
     * 排序字段
     * 用于指定按照哪个字段进行排序，可为空
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     * 用于指定排序方式，升序(ASC)或降序(DESC)
     * @see insane.judgeX.constant.CommonConstant#SORT_ORDER_ASC
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}

