package insane.service;


import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeRespond;

public interface CodeSandBox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest);
}
