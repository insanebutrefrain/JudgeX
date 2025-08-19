package insane.judgeX.judge.strategy;

import insane.judgeX.judge.codesandbox.model.JudgeInfo;
import insane.judgeX.model.dto.question.JudgeCase;
import insane.judgeX.model.entity.Question;
import insane.judgeX.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
