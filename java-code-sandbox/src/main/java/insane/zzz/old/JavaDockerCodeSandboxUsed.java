package insane.zzz.old;

import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import insane.implNative.model.ExecuteMessage;
import insane.implNative.JavaCodeSandboxTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 复用模板方法：创建docker容器执行代码
 */
@Service
public class JavaDockerCodeSandboxUsed extends JavaCodeSandboxTemplate {

    // 设置代码执行超时时间（毫秒）
    private static final long TIME_OUT = 5000L;

    // 标记是否首次初始化，用于决定是否需要拉取镜像
    private static final Boolean FIRST_INIT = true;

    /**
     * 执行用户提交的Java代码文件
     * @param userCodeFile 用户代码文件
     * @param inputList 输入参数列表，每个元素代表一组测试用例的输入
     * @return 执行结果列表，包含每组输入对应的执行信息
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        // 获取用户代码文件所在目录的绝对路径，用于后续挂载到容器中
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 创建Docker客户端实例，用于与Docker守护进程通信
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        // 指定要使用的Docker镜像
        String image = "openjdk:8-alpine";

        // 首次使用时需要拉取镜像
        if (FIRST_INIT) {
            // 创建拉取镜像命令
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);

            // 创建拉取镜像结果回调处理类
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                // 重写onNext方法，用于处理拉取过程中的事件
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像");
                    super.onNext(item);
                }
            };

            try {
                // 执行拉取镜像命令并等待完成
                pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("下载完成");
        }

        // 创建容器
        System.out.println("创建容器");
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);

        // 配置容器参数
        HostConfig hostConfig = new HostConfig();
        // 限制容器内存使用为256MB
        hostConfig.withMemory(256 * 1000 * 1000L);
        // 禁用交换内存
        hostConfig.withMemorySwap(0L);
        // 限制容器只能使用1个CPU核心
        hostConfig.withCpuCount(1L);

        // Linux内核安全配置文件 - 限制系统调用，只允许基本的文件读写和程序退出操作
        String seccompProfile = "{\n" +
                "  \"defaultAction\": \"SCMP_ACT_ERRNO\",\n" +
                "  \"syscalls\": [\n" +
                "    {\n" +
                "      \"name\": \"read\",\n" +
                "      \"action\": \"SCMP_ACT_ALLOW\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"write\",\n" +
                "      \"action\": \"SCMP_ACT_ALLOW\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"exit\",\n" +
                "      \"action\": \"SCMP_ACT_ALLOW\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"exit_group\",\n" +
                "      \"action\": \"SCMP_ACT_ALLOW\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 应用安全配置
        hostConfig.withSecurityOpts(Arrays.asList("seccomp=" + seccompProfile));
        // 设置文件路径映射，将用户代码目录挂载到容器的/app目录
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));

        // 创建容器并应用所有配置
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig) // 应用主机配置
                .withNetworkDisabled(true) // 禁用网络连接
                .withReadonlyRootfs(true) // 设置根文件系统为只读
                .withAttachStdin(true) // 连接标准输入
                .withAttachStdout(true) // 连接标准输出
                .withAttachStderr(true) // 连接标准错误输出
                .withTty(true) // 启用交互式终端
                .exec();

        System.out.println(createContainerResponse);
        // 获取创建的容器ID
        String containerId = createContainerResponse.getId();

        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // 执行命令并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();

        // 遍历每个输入参数，分别执行代码
        for (String inputArgs : inputList) {
            // 创建计时器用于记录执行时间
            StopWatch stopWatch = new StopWatch();

            // 将输入参数字符串分割为参数数组
            String[] inputArgsArray = inputArgs.split(" ");

            // 构建完整的执行命令: java -cp /app Main [inputArgs]
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);

            // 创建在容器中执行命令的实例
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();
            System.out.println("创建执行命令：" + execCreateCmdResponse);

            // 创建执行结果对象
            ExecuteMessage executeMessage = new ExecuteMessage();
            // 用于存储正常输出结果
            final String[] message = {null};
            // 用于存储错误输出结果
            final String[] errorMessage = {null};
            // 执行时间
            long time = 0L;
            // 超时标记
            final boolean[] timeout = {true};

            // 获取执行命令的ID
            String execId = execCreateCmdResponse.getId();

            // 创建执行结果回调处理类
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                // 执行完成时调用，表示没有超时
                @Override
                public void onComplete() {
                    timeout[0] = false;
                    super.onComplete();
                }

                // 处理执行过程中的输出数据
                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    // 判断是错误输出还是标准输出
                    if (streamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("结果错误：" + errorMessage[0]);
                    } else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果" + message[0]);
                    }
                    super.onNext(frame);
                }
            };

            // 获取容器内存占用统计信息
            final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                // 处理内存统计信息
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    // 记录最大内存使用量
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                // 以下为ResultCallback接口必须实现的方法，但此处不需要特殊处理
                @Override
                public void onStart(Closeable closeable) {
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void close() throws IOException {
                }
            });
            statsCmd.exec(statisticsResultCallback);

            // 执行命令并等待结果
            try {
                stopWatch.start(); // 开始计时
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS); // 等待执行完成或超时
                stopWatch.stop(); // 停止计时
                time = stopWatch.getLastTaskTimeMillis(); // 获取执行时间
                statsCmd.close(); // 关闭内存统计
            } catch (InterruptedException e) {
                System.out.println("指令执行异常！");
                throw new RuntimeException(e);
            }

            // 将执行结果封装到ExecuteMessage对象中
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }

        // 返回所有测试用例的执行结果
        return executeMessageList;
    }
}
