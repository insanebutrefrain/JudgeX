package insane.judgeX.judge;


import insane.judgeX.judge.model.ExecuteCodeRequest;
import insane.judgeX.judge.model.ExecuteCodeResponse;

/**
 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     执行代码
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
