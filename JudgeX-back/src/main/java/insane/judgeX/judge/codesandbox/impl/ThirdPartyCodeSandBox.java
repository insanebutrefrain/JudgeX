package insane.judgeX.judge.codesandbox.impl;

import insane.judgeX.judge.codesandbox.CodeSandBox;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;

/**
 * 第三方代码吗沙箱（别人写的）
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱调用");
        return null;
    }
}
