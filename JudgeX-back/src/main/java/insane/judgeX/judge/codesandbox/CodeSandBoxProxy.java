package insane.judgeX.judge.codesandbox;

import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理
 */
@Slf4j
public class CodeSandBoxProxy implements CodeSandBox {

    private CodeSandBox codeSandBox;

    public CodeSandBoxProxy(CodeSandBox codeSandBox) {
        this.codeSandBox = codeSandBox;
    }

    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest);
        ExecuteCodeRespond executeCodeRespond = codeSandBox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeRespond);
        return executeCodeRespond;
    }
}
