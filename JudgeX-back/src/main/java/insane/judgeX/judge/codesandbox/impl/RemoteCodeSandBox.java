package insane.judgeX.judge.codesandbox.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import insane.judgeX.common.ErrorCode;
import insane.judgeX.exception.BusinessException;
import insane.judgeX.judge.codesandbox.CodeSandBox;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandBox implements CodeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secret";

    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱调用");
        String url = "http://localhost:8888/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        HttpResponse response = HttpUtil.createPost(url)
                .body(json)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .execute();
        String responseStr = response.body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用代码沙箱接口错误！");
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeRespond.class);
    }
}
