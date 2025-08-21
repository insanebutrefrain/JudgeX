package insane.judgeX.judge.codesandbox;

import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;

/**
 *  代码沙箱接口
 */
public interface CodeSandBox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest);
}
