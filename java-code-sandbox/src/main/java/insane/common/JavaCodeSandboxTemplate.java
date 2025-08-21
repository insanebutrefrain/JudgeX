package insane.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import insane.exception.CodeException;
import insane.exception.ErrorCodeEnum;
import insane.implNative.ProcessUtils;
import insane.model.ExecuteCodeRequest;
import insane.model.ExecuteCodeRespond;
import insane.model.ExecuteMessage;
import insane.model.JudgeInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Java代码沙箱模板
 */
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    public static final long TIME_OUT = 5000L;

    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final String USER_DIR = JavaCodeSandboxTemplate.class.getProtectionDomain().getCodeSource().getLocation().getPath();


    private static final String SECURITY_MANAGER_PATH = USER_DIR;

    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    // 安全策略文件路径
    private static final String POLICY_FILE_PATH = "file" + SECURITY_MANAGER_PATH + File.separator + "security.policy";

    /**
     * 完整流程
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeRespond executeCode(ExecuteCodeRequest executeCodeRequest) {        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

        // 1. 把用户的代码保存为文件
        File userCodefile = saveCodeToFile(code);

        // 2. 编译代码，得到.class文件
        try {
            ExecuteMessage compileFileExecuteMessage = compileFile(userCodefile);
        } catch (Exception e) {
            System.out.println("编译错误：" + e.getMessage());
            throw new CodeException(ErrorCodeEnum.CompileError);
        }

        // 3. 运行代码，得到输出
        List<ExecuteMessage> executeMessageList = runFile(userCodefile, inputList);

        // 4. 收集整理输出结果
        ExecuteCodeRespond executeCodeRespond = getOutputRespond(executeMessageList);


        // 5. 文件清理，防止服务器空间不足
        deleteFile(userCodefile);

        return executeCodeRespond;
    }

    /**
     * 1. 把用户的代码保存为文件
     *
     * @param code
     * @return File
     */
    public File saveCodeToFile(String code) {
        // 1. 把用户的代码保存为文件
        try {
            String globalCodePathName = USER_DIR + File.separator + GLOBAL_CODE_DIR_NAME;
            // 判断全局代码目录是否存在，不存在则新建
            if (!FileUtil.exist(globalCodePathName)) {
                FileUtil.mkdir(globalCodePathName);
            }
            // 把用户代码隔离存放
            String userCodeParentPath = globalCodePathName + File.separator + System.currentTimeMillis();
            String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
            File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
            System.out.println("保存用户代码：" + userCodeFile.getAbsolutePath());
            return userCodeFile;
        } catch (IORuntimeException e) {
            System.out.println(("保存用户代码异常：" + e.getMessage()));
            throw new CodeException(ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 2. 编译代码文件
     *
     * @param userCodeFile
     * @return ExecuteMessage
     */
    public ExecuteMessage compileFile(File userCodeFile) throws IOException {
        String compiledCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        Process compileProcess = Runtime.getRuntime().exec(compiledCmd);
        ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
        System.out.println("编译信息" + executeMessage);
        if (executeMessage.getProcessExitCode() != 0) {
            throw new RuntimeException("编译错误");
        }
        return executeMessage;
    }

    /**
     * 3. 执行文件，获得输出结果
     *
     * @param userCodeFile
     * @param inputList
     * @return List<ExecuteMessage>
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        System.out.println("开始执行");
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // 使用-Xmx256m，限制最大内存为256M
            // jvm 参数的限制，堆内存限制，不等同于系统实际占用内存
            /**
             *  运行命令行参数
             */
//            String runCmd = String.format("java -Xmx512m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
//            String runCmd = String.format("java -Xmx512m -Dfile.encoding=UTF-8 -cp \"%s;%s\" -Djava.security.manager=%s Main %s",
//                    userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, inputArgs);
            /**
             * 交互执行命令
             */
            // 设置安全管理器
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp \"%s;%s\" -Djava.security.manager=%s Main",
//                    userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            String runCmd = String.format(
                    "java -Xmx256m -Dfile.encoding=UTF-8 -Djava.security.manager=%s -Djava.security.policy=%s -cp \"%s;%s\" Main",
                    SECURITY_MANAGER_CLASS_NAME, POLICY_FILE_PATH, userCodeParentPath, SECURITY_MANAGER_PATH);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 运行时间限制
                // 创建一个线程，如果运行时间超过TIME_OUT，则强制杀死进程
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        if (runProcess.isAlive()) {
                            runProcess.destroy();
                            System.out.println("运行时间超时");
                            throw new CodeException(ErrorCodeEnum.TIME_LIMIT_EXCEEDED);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("超时线程异常");
                        throw new CodeException(ErrorCodeEnum.SYSTEM_ERROR);
                    }
                }).start();
//                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "命令行参数运行");
                ExecuteMessage executeMessage = ProcessUtils.runInterProcessAndGetMessage(runProcess, "交互参数运行", inputArgs);
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                System.out.println(("运行错误：" + e.getMessage()));
                throw new CodeException(ErrorCodeEnum.RUNTIME_ERROR);
            }
        }
        return executeMessageList;
    }

    /**
     * 4. 整理输出结果
     *
     * @param executeMessageList
     * @return ExecuteCodeRespond
     */
    public ExecuteCodeRespond getOutputRespond(List<ExecuteMessage> executeMessageList) {
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

        return executeCodeRespond;
    }

    /**
     * 5. 删除文件
     *
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除 " + userCodeParentPath + " " + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }
}
