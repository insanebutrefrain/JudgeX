package insane.judgeX.judge.strategy;

import insane.judgeX.judge.codesandbox.model.JudgeInfo;
import insane.judgeX.model.dto.question.JudgeCase;
import insane.judgeX.model.entity.Question;
import insane.judgeX.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 判题上下文
 */
@Data
public class JudgeContext {
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 输入列表
     */
    private List<String> inputList;

    /**
     * 输出列表
     */
    private List<String> outputList;

    /**
     * 判题用例列表
     */
    private List<JudgeCase> judgeCaseList;

    /**
     * 题目
     */
    private Question question;

    /**
     * 题目提交
     */
    private QuestionSubmit questionSubmit;
}
