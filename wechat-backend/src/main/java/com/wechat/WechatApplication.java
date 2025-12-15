package com.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 微信聊天后端应用的入口类。
 * <p>
 * 该类负责启动 Spring Boot 应用，加载所有组件与配置（安全、WebSocket、持久层等）。
 * 一般通过运行 {@link #main(String[])} 方法来启动服务。
 * </p>
 * 使用说明：
 * - 本地开发：运行 main 方法或使用 Maven 命令启动；
 * - 部署运行：打包为可执行 JAR 后在服务器上运行。
 */
@SpringBootApplication
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }

}
