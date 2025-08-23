package insane.judgeX.judge.impl;


import insane.judgeX.judge.CodeSandbox;
import insane.judgeX.judge.model.ExecuteCodeRequest;
import insane.judgeX.judge.model.ExecuteCodeResponse;

/**
 第三方代码沙箱(非自己编写的代码沙箱)
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return null;
    }
}
