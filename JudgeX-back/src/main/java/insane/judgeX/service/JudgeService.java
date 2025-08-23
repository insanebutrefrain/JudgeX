package insane.judgeX.service;


import insane.judgeX.model.entity.QuestionSubmit;

public interface JudgeService {
    /**
     判题服务
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
