package insane.securityManager;

import java.security.Permission;

/**
 * 默认安全管理器
 */
public class DefaultSecurityManager extends SecurityManager {
    // 检查所有权限
    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认不对权限进行校验: " + perm.toString());
//        super.checkPermission(perm);
    }

}
