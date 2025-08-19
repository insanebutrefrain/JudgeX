package insane.judgeX.judge.strategy;

import cn.hutool.json.JSONUtil;
import insane.judgeX.judge.codesandbox.model.JudgeInfo;
import insane.judgeX.model.dto.question.JudgeCase;
import insane.judgeX.model.dto.question.JudgeConfig;
import insane.judgeX.model.entity.Question;
import insane.judgeX.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {

    /**
     * 判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();


        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;

        JudgeInfo judgeInfoRespond = new JudgeInfo();
        judgeInfoRespond.setMemory(memory);
        judgeInfoRespond.setTime(time);

        // 先判断沙箱执行的结果输出数量和预期的输出数量是否相同
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoRespond.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoRespond;
        }
        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < outputList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoRespond.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoRespond;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long memoryLimit = judgeConfig.getMemoryLimit();
        Long timeLimit = judgeConfig.getTimeLimit();
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoRespond.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoRespond;
        }
        if (time > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoRespond.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoRespond;
        }
        judgeInfoRespond.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoRespond;
    }
}
