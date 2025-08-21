package insane.implNative;

import cn.hutool.core.util.StrUtil;
import insane.implNative.exception.CodeException;
import insane.implNative.exception.ErrorCodeEnum;
import insane.implNative.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtils {

    /**
     * 执行交互性进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @param args
     * @return
     */
    public static ExecuteMessage runInterProcessAndGetMessage(Process runProcess, String opName, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {

            // 正常退出
            System.out.println(opName);
            // 向控制台输入程序
            InputStream inputStream = runProcess.getInputStream();
            OutputStream outputStream = runProcess.getOutputStream();
            InputStream errorStream = runProcess.getErrorStream();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            // 记得要加空格和回车
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            // 相当于按了回车，执行输入的发送
            outputStreamWriter.flush();

            // 分批获取进程的输出
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> outputList = new ArrayList<>();
            // 逐行读取
            String compileOutputLine;
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                outputList.add(compileOutputLine);
            }
            executeMessage.setMessage(StringUtils.join(outputList, "\n"));

            // 分批获取进程的异常输出
            BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(errorStream));
            List<String> errorOutputList = new ArrayList<>();
            // 逐行读取
            String errorCompileOutputLine = null;
            while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                errorOutputList.add(errorCompileOutputLine);
            }
            executeMessage.setErrorMessage(StringUtils.join(errorOutputList, "\n"));
            // 退出，记得资源的释放，否则会卡死
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
            if (runProcess.exitValue() != 0) {
                System.out.println(executeMessage);
                throw new CodeException(ErrorCodeEnum.RUNTIME_ERROR);
            }
            // 正常退出
            System.out.println(opName + "完成");
        } catch (Exception e) {
            runProcess.destroy();
            throw new CodeException(ErrorCodeEnum.RUNTIME_ERROR);
        }
        return executeMessage;
    }

    /**
     * 执行进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        // 开始计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            // 等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setProcessExitCode(exitValue);

            // 正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                // 分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputList, "\n"));
            } else {
                throw new CodeException(ErrorCodeEnum.RUNTIME_ERROR);
//                // 异常退出
//                System.out.println(opName + "失败，错误码：" + exitValue);
//                // 分批获取进程的输出
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
//                List<String> outputList = new ArrayList<>();
//                // 逐行读取
//                String compileOutputLine = null;
//                while ((compileOutputLine = bufferedReader.readLine()) != null) {
//                    outputList.add(compileOutputLine);
//                }
//                executeMessage.setMessage(StringUtils.join(outputList, "\n"));
//
//                // 分批获取进程的异常输出
//                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
//                List<String> errorOutputList = new ArrayList<>();
//                // 逐行读取
//                String errorCompileOutputLine = null;
//                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
//                    errorOutputList.add(errorCompileOutputLine);
//                }
//                executeMessage.setErrorMessage(StringUtils.join(errorOutputList, "\n"));
            }
        } catch (Exception e) {
            throw new CodeException(ErrorCodeEnum.RUNTIME_ERROR);
        } finally {
            // 停止计时
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
        }
        return executeMessage;
    }
}
