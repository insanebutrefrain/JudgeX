package insane.implDocker;


import insane.implDocker.model.ExecuteCodeResponse;
import insane.common.ExecuteCodeRequest;

/**
 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
