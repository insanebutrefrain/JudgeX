package insane.judgeX.constant;

/**
 * 用户相关常量接口
 * 定义用户模块中使用的各种常量值，包括登录状态、用户角色等
 */
public interface UserConstant {

    /**
     * 用户登录状态在Session中的键名
     * 用于存储和获取当前登录用户的信息
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认用户角色
     * 普通用户的角色标识
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     * 系统管理员的角色标识，拥有最高权限
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封禁用户角色
     * 被封禁用户的角色标识，限制其系统访问权限
     */
    String BAN_ROLE = "ban";

}
