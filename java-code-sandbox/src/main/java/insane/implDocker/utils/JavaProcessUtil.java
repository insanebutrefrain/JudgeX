package insane.implDocker.utils;


import insane.implDocker.enums.ExecuteStatus;
import insane.implDocker.model.ExecuteMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaProcessUtil {

    private JavaProcessUtil() {}

    /**
     编译Java文件

     @param process
     @return
     @throws Exception
     */
    public static ExecuteMessage runAndGetMessage(Process process) throws Exception {
        ExecuteMessage executeMessage = new ExecuteMessage();
        int exitCode = process.waitFor();
        executeMessage.setProcessExitCode(exitCode);
        if (exitCode == 0) {
            String output = readStream(process.getInputStream());
            executeMessage.setOutput(output);
            executeMessage.setExecuteStatus(ExecuteStatus.SUCCESS);
        } else {
            String errorMessage = readStream(process.getErrorStream());
            executeMessage.addErrorMessage(errorMessage);
            executeMessage.setExecuteStatus(ExecuteStatus.EXITED);
        }
        return executeMessage;
    }


    /**
     获取输出

     @param inputStream
     @return
     @throws IOException
     */
    static String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder ans = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            ans.append(line).append("\n");
        }
        return ans.toString();
    }

}
