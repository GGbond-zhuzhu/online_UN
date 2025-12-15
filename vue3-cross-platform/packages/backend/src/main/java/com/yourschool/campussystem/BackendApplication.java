package com.yourschool.campussystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableScheduling
@MapperScan("com.yourschool.campussystem.mapper")  // 添加Mapper扫描
public class BackendApplication {

    public static void main(String[] args) {
        // #region agent log
        try {
            // 使用相对路径，兼容不同环境
            String userDir = System.getProperty("user.dir");
            String logPath = java.nio.file.Paths.get(userDir, ".cursor", "debug.log").toString();
            java.io.File logFile = new java.io.File(logPath);
            logFile.getParentFile().mkdirs(); // 确保目录存在
            java.io.FileWriter fw = new java.io.FileWriter(logFile, true);
            fw.write("{\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"BackendApplication.java:18\",\"message\":\"应用启动开始\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"startup\",\"hypothesisId\":\"A\"}\n");
            fw.close();
        } catch (Exception e) {
            // 静默处理日志写入失败，不影响应用启动
        }
        // #endregion
        SpringApplication.run(BackendApplication.class, args);
        // #region agent log
        try {
            // 使用相对路径，兼容不同环境
            String userDir = System.getProperty("user.dir");
            String logPath = java.nio.file.Paths.get(userDir, ".cursor", "debug.log").toString();
            java.io.FileWriter fw = new java.io.FileWriter(logPath, true);
            fw.write("{\"timestamp\":" + System.currentTimeMillis() + ",\"location\":\"BackendApplication.java:25\",\"message\":\"SpringApplication.run调用完成\",\"data\":{},\"sessionId\":\"debug-session\",\"runId\":\"startup\",\"hypothesisId\":\"A\"}\n");
            fw.close();
        } catch (Exception e) {
            // 静默处理日志写入失败，不影响应用启动
        }
        // #endregion
    }
}