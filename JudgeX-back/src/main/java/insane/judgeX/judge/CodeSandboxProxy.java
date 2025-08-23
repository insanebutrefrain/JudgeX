package insane.judgeX.judge;

import insane.judgeX.judge.model.ExecuteCodeRequest;
import insane.judgeX.judge.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {
    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("[代理日志]代码沙箱请求: {}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("[代理日志]代码沙箱响应: {}", executeCodeResponse);
        return executeCodeResponse;
    }
}
