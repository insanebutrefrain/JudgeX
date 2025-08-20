#  JudgeX 在线判题服务后台项目

## 项目简介

JudgeX 是一个在线判题服务的后台系统，支持用户提交代码并进行自动判题，主要用于编程练习、算法竞赛等场景。系统能够接收用户提交的代码，调用代码沙箱执行，并根据预设的测试用例和判题规则对代码进行评判，返回判题结果。

## 技术栈

### 核心框架

- **Spring Boot 2.7.2**：用于快速开发 Java 后端应用，提供自动配置、依赖注入等核心功能
- **Spring MVC**：处理 HTTP 请求，实现 RESTful API 接口

### 数据访问

- **MyBatis-Plus 3.5.2**：基于 MyBatis 的增强工具，简化数据库操作，提供 CRUD 接口、分页等功能
- **MySQL**：关系型数据库，用于存储用户信息、题目数据、提交记录等

### 接口文档

- **Knife4j 3.0.3**：基于 Swagger 的 API 文档生成工具，方便接口调试和管理

### 工具类

- **Hutool**：Java 工具类库，提供 JSON 处理等功能
- **Apache Commons**：提供字符串处理、集合操作等工具类

### 其他

- **Lombok**：通过注解简化 Java 代码，减少 getter/setter 等模板代码
- **AOP**：用于实现日志、权限等横切关注点
- **异步编程**：使用 CompletableFuture 实现判题过程的异步处理

## 架构设计

### 核心模块

1. **用户模块**：处理用户注册、登录、权限管理等功能
2. **题目模块**：管理题目信息、测试用例、判题配置等
3. **提交模块**：处理用户代码提交，记录提交状态
4. **判题模块**：调用代码沙箱执行代码，实现判题逻辑
5. **代码沙箱**：安全执行用户提交的代码，返回执行结果

### 流程设计

1. 用户提交代码到系统
2. 系统记录提交信息，设置状态为 "等待判题"
3. 异步调用判题服务进行判题
4. 判题服务调用代码沙箱执行用户代码
5. 根据执行结果和预设规则进行判题
6. 更新提交记录的判题结果和状态

## 设计模式应用

### 工厂模式

- **代码沙箱工厂（CodeSandBoxFactory）**：根据配置创建不同类型的代码沙箱实例，便于扩展支持多种沙箱实现

```java
CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
```

### 代理模式

- **代码沙箱代理（CodeSandBoxProxy）**：对代码沙箱的调用进行代理，可以添加日志、监控等额外功能

```java
codeSandBox = new CodeSandBoxProxy(codeSandBox);
```

### 策略模式

- **判题策略（JudgeStrategy）**：定义判题接口，针对不同编程语言实现不同的判题策略
- **策略管理器（JudgeManager）**：根据编程语言选择对应的判题策略

```java
JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
if ("java".equals(language)) {
    judgeStrategy = new JavaLanguageJudgeStrategy();
}
return judgeStrategy.doJudge(judgeContext);
```

### 建造者模式

- **执行代码请求（ExecuteCodeRequest）**：使用建造者模式构建代码执行请求对象，简化对象创建过程

```java
ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
        .code(code)
        .language(language)
        .inputList(inputList)
        .build();
```

## 核心功能实现

### 判题流程（JudgeServiceImpl）

1. 获取提交信息和题目信息
2. 更新提交状态为 "判题中"
3. 调用代码沙箱执行代码
4. 构建判题上下文，调用判题管理器进行判题
5. 更新判题结果和状态

### 代码提交（QuestionSubmitServiceImpl）

1. 校验提交信息和用户权限
2. 保存提交记录，设置初始状态为 "等待判题"
3. 异步调用判题服务，避免阻塞用户请求

```java
CompletableFuture.runAsync(() -> {
    judgeService.doJudge(questionSubmitId);
});
```

## 配置说明

系统核心配置文件为 `application.yml`，主要配置项包括：

- 数据库连接信息
- 服务器端口和上下文路径
- 会话配置
- 代码沙箱类型配置
- 文件上传大小限制

## 启动说明

1. 配置 MySQL 数据库，创建名为 `judgex` 的数据库
2. 修改 `application.yml` 中的数据库连接信息
3. 运行 `JudgeXBackApplication` 主类启动项目
4. 访问 `http://localhost:8121/api/doc.html` 查看接口文档

## 扩展建议

1. 增加 Redis 缓存，提高热门题目和用户信息的访问速度
2. 实现分布式判题任务调度，支持大规模并发判题
3. 扩展代码沙箱支持更多编程语言
4. 增加代码查重功能，检测抄袭行为
5. 实现更详细的代码性能分析，提供更多优化建议