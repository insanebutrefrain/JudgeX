package insane.judgeX.judge;

import cn.hutool.json.JSONUtil;
import insane.judgeX.common.ErrorCode;
import insane.judgeX.exception.BusinessException;
import insane.judgeX.judge.codesandbox.CodeSandBox;
import insane.judgeX.judge.codesandbox.CodeSandBoxFactory;
import insane.judgeX.judge.codesandbox.CodeSandBoxProxy;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRequest;
import insane.judgeX.judge.codesandbox.model.ExecuteCodeRespond;
import insane.judgeX.judge.codesandbox.model.JudgeInfo;
import insane.judgeX.judge.strategy.JudgeContext;
import insane.judgeX.judge.strategy.JudgeManager;
import insane.judgeX.model.dto.question.JudgeCase;
import insane.judgeX.model.entity.Question;
import insane.judgeX.model.entity.QuestionSubmit;
import insane.judgeX.model.enums.QuestionSubmitStatusEnum;
import insane.judgeX.service.QuestionService;
import insane.judgeX.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Value("${code-sand-box.type}")
    private String type;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 传入题目的提交id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2. 如果不为等待状态
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3. 更改判题的状态"判题中"，防止重复执行
        // 我有点问题，为什么直接new了一个新的对象，而不是从原来的提交对象中更改
        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isUpdateOk = questionSubmitService.updateById(questionSubmit);
        if (!isUpdateOk) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4. 调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeRespond executeCodeRespond = codeSandBox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeRespond.getOutput();
        // 5. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeRespond.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6. 修改数据库中的判题结果
        questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));

        isUpdateOk = questionSubmitService.updateById(questionSubmit);
        if (!isUpdateOk) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交更新失败");
        }
        return questionSubmit;
    }
}
