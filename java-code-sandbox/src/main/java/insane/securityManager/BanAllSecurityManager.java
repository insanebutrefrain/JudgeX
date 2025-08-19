package insane.securityManager;

import java.security.Permission;

/**
 * 禁用所有权限管理器
 */
public class BanAllSecurityManager extends SecurityManager {
    // 检查所有权限
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("禁止所有权限: " + perm.toString());
    }
}
