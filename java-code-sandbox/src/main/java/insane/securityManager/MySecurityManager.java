package insane.securityManager;

import java.security.Permission;

/**
 * 安全管理器
 */
public class MySecurityManager extends SecurityManager {

    // 检查所有权限
    @Override
    public void checkPermission(Permission perm) {

//        super.checkPermission(perm);
    }

    // 检查是否可以执行
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec权限异常" + cmd);
    }

    // 检查是否可以读取
    @Override
    public void checkRead(String file, Object context) {
        throw new SecurityException("checkRead权限异常" + file);
    }


    // 检查是否可以写入
    @Override
    public void checkWrite(String file) {
        throw new SecurityException("checkWrite权限异常" + file);
    }

    // 检查是否可以删除
    @Override
    public void checkDelete(String file) {
        throw new SecurityException("checkDelete权限异常" + file);
    }


    // 检查是否可以连接网络
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("checkConnect权限异常" + host + ":" + port);
    }


}
