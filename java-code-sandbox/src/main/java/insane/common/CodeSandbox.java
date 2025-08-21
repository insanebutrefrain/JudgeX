package insane.common;


import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeRespond;

public interface CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest);
}
