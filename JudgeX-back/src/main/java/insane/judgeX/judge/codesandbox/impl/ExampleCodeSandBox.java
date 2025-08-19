package insane.judgeX.judge.codesandbox.impl;

import insane.judgeX.judge.codesandbox.CodeSandBox;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;
import insane.judgeX.judge.codesandbox.model.JudgeInfo;
import insane.judgeX.model.enums.JudgeInfoMessageEnum;
import insane.judgeX.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱（只为跑通程序流程）
 */
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeRespond executeCodeRespond = new ExecuteCodeRespond();
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        executeCodeRespond.setOutput(inputList);
        executeCodeRespond.setMessage("测试执行成功");
        executeCodeRespond.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeRespond.setJudgeInfo(judgeInfo);

        return executeCodeRespond;
    }
}
