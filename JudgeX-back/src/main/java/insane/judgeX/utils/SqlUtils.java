package insane.judgeX.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL工具类
 * 提供SQL相关的工具方法，主要用于防止SQL注入等数据库安全问题
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     * 该方法用于验证传入的排序字段是否包含可能引起SQL注入的危险字符
     *
     * @param sortField 待校验的排序字段名，如"id"、"createTime"等
     * @return boolean 返回true表示字段合法，false表示字段不合法
     */
    public static boolean validSortField(String sortField) {
        // 如果排序字段为空或仅包含空白字符，则不合法
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        // 检查排序字段是否包含SQL危险字符：等号(=)、左括号(、右括号)、空格( )
        // 这些字符可能被用于SQL注入攻击，如构造恶意的排序表达式
        // 如果包含任意危险字符则返回false，否则返回true
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
