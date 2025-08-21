package insane.implNative;


import insane.common.ExecuteCodeRequest;
import insane.implNative.model.ExecuteCodeRespond;

public interface CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest);
}
