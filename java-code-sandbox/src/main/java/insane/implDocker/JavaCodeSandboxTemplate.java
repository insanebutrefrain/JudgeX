package insane.implDocker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import insane.implDocker.enums.ExecuteErrorMessageEnum;
import insane.implDocker.enums.ExecuteStatus;
import insane.implDocker.model.ExecuteCaseInfo;
import insane.implDocker.model.ExecuteCodeResponse;
import insane.implDocker.model.ExecuteMessage;
import insane.implDocker.utils.JavaCodeValidator;
import insane.implDocker.utils.JavaProcessUtil;
import insane.model.ExecuteCodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {
    private  static final String Temp_Code_Dir_Name = "tempCode";
    private static final String Java_File_Name = "safe/Main.java";

    /**
     * 1. 保存代码到文件
     * 文件在`Temp_Code_Dir_Name/uuid`目录下, 文件名为`Java_File_Name`
     *
     * @param code
     * @return 文件对象
     */
    private File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String tempCodeDir = userDir + File.separator + Temp_Code_Dir_Name;
        if (!FileUtil.exist(tempCodeDir)) {
            FileUtil.mkdir(tempCodeDir);
        }
        String userCodeDir = tempCodeDir + File.separator + UUID.randomUUID();
        String userCodePath = userCodeDir + File.separator + Java_File_Name;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 2. 编译Java代码
     * 编译后的文件在同一目录下, 文件名为Main.class
     *
     * @param userCodeFile
     * @return
     */
    private ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process complieProcess = Runtime.getRuntime().exec(compileCmd);
            return JavaProcessUtil.runAndGetMessage(complieProcess);
        } catch (Exception e) {
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExecuteStatus(ExecuteStatus.EXITED);
            executeMessage.setProcessExitCode(-1);
            executeMessage.addErrorMessage(e.getMessage());
            return executeMessage;
        }
    }

    /**
     * 3. 执行代码
     * @param inputList
     * @param userCodeFile
     * @return
     */
    protected abstract List<ExecuteMessage> runFile(List<String> inputList, File userCodeFile);

    /**
     * 4. 整理信息
     *
     * @param executeMessageList 每个测试用例的执行情况
     * @return
     */
    private List<ExecuteCaseInfo> organizeInformation(List<ExecuteMessage> executeMessageList) {
        List<ExecuteCaseInfo> executeCaseInfoList = new ArrayList<>();
        for (ExecuteMessage executeMessage : executeMessageList) {
            ExecuteCaseInfo executeCaseInfo = new ExecuteCaseInfo();
            String output = executeMessage.getOutput();
            if (output != null) {
                output = output.replaceAll("\\s+$", "");//去除末尾换行
            }
            executeCaseInfo.setOutput(output);
            executeCaseInfo.setTime(executeMessage.getTime());
            executeCaseInfo.setMemory(executeMessage.getMemory());
            executeCaseInfo.setErrorMessage(executeMessage.getErrorMessage());
            executeCaseInfoList.add(executeCaseInfo);
        }
        return executeCaseInfoList;
    }

    /**
     * 5. 清理用户临时代码文件
     *
     * @param userCodeFile
     * @return
     */
    private boolean clean(File userCodeFile) {
        if (userCodeFile.getParentFile().exists()) {
            return FileUtil.del(userCodeFile.getParentFile());
        }
        return true;
    }

    /**
     * 执行代码流程
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        String code = executeCodeRequest.getCode();
        // 1. 校验
        log.info("1.校验");
        boolean isValid = JavaCodeValidator.validateCode(code);
        if (!isValid) {
            executeCodeResponse.setExecuteErrorMessageEnum(ExecuteErrorMessageEnum.DANGEROUS_CODE.getValue());
            executeCodeResponse.setMessage("代码校验不通过");
            return executeCodeResponse;
        }
        // 2. 保存用户代码
        log.info("2.保存用户代码");
        File userCodeFile = saveCodeToFile(code);
        // 3. 编译
        log.info("3.编译");
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        if (!ExecuteStatus.SUCCESS.equals(compileFileExecuteMessage.getExecuteStatus())) {
            executeCodeResponse.setExecuteErrorMessageEnum(ExecuteErrorMessageEnum.COMPILE_ERROR.getValue());
            executeCodeResponse.setMessage("编译失败: " + compileFileExecuteMessage.getErrorMessage());
            return executeCodeResponse;
        }
        // 4. 运行
        log.info("4.运行");
        List<String> inputList = executeCodeRequest.getInputList();
        List<ExecuteMessage> executeMessageList = runFile(inputList, userCodeFile);
        // 5. 收集结果
        log.info("5.收集结果");
        List<ExecuteCaseInfo> executeCaseInfoList = organizeInformation(executeMessageList);
        executeCodeResponse.setExecuteCaseInfoList(executeCaseInfoList);
        // 6. 文件清理
        log.info("6.文件清理");
        boolean del = clean(userCodeFile);
        if (!del) System.out.println("文件清理失败");
        log.info("7.响应:{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
