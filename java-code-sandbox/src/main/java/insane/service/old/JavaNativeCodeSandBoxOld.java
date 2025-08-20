package insane.service.old;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeRespond;
import insane.model.ExecuteMessage;
import insane.model.JudgeInfo;
import insane.service.CodeSandBox;
import insane.utils.ProcessUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaNativeCodeSandBoxOld implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 5000L;

    private static final List<String> blackList = Arrays.asList("Files", "exec");

    private static final String SECURITY_MANAGER_PATH = "P:\\IDEA\\java-code-sandbox\\src\\main\\resources\\security";

    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager.java";

    private static final WordTree WORD_TREE = new WordTree();

    static {
        WORD_TREE.addWords(blackList);
    }

    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {
        // 设置安全管理器
//        System.setSecurityManager(new DefaultSecurityManager());

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        // 校验代码
        // 防止代码中出现黑名单命令
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            return null;
        }


        // 1. 把用户的代码保存为文件
        String userDir = System.getProperty("user.dir");// 获取项目路径
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，不存在则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + System.currentTimeMillis();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 2. 编译代码，得到.class文件
        String compiledCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compiledCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        } catch (Exception e) {
            return getErrorRespond(e);
        }

        // 3. 运行代码，得到输出
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 使用-Xmx256m，限制最大内存为256M
            // jvm 参数的限制，堆内存限制，不等同于系统实际占用内存
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s;%s -Djava.security.manager=%s Main %s", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 运行时间限制
                // 创建一个线程，如果运行时间超过TIME_OUT，则强制杀死进程
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("运行时间超时");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
//               ExecuteMessage executeMessage = ProcessUtils.runInterProcessAndGetMessage(runProcess, "交互运行", inputArgs);
                System.out.println(executeMessage);
            } catch (Exception e) {
                return getErrorRespond(e);
            }
        }

        // 4. 收集整理输出结果
        ExecuteCodeRespond executeCodeRespond = new ExecuteCodeRespond();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;  // 最大执行时间

        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeRespond.setMessage(errorMessage);
                // 执行中存在错误
                executeCodeRespond.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }
        if (outputList.size() == executeMessageList.size()) {
            executeCodeRespond.setStatus(1);
        }
        executeCodeRespond.setOutput(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);

        // 要借助第三方库获取内存消耗，太麻烦了，这里暂时不处理
        judgeInfo.setMemory(0L);

        executeCodeRespond.setJudgeInfo(judgeInfo);

        // 5. 文件清理，防止服务器空间不足
        if (FileUtil.exist(userCodeParentPath)) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除 " + userCodeParentPath + " " + (del ? "成功" : "失败"));
        }


        return executeCodeRespond;
    }

    private ExecuteCodeRespond getErrorRespond(Throwable e) {
        ExecuteCodeRespond executeCodeRespond = new ExecuteCodeRespond();
        executeCodeRespond.setOutput(new ArrayList<>());
        executeCodeRespond.setMessage(e.getMessage());

        // 表示代码沙箱错误
        executeCodeRespond.setStatus(2);
        executeCodeRespond.setJudgeInfo(new JudgeInfo());
        return executeCodeRespond;
    }
}
