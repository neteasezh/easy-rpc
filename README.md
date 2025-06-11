![[[image1](image1)](https://github.com/github-copilot/chat/attachments/1656484)](https://copilotprodattachments.blob.core.windows.net/github-production-copilot-attachments/49762189/c4728816-b3e3-4343-8052-6dc62f946d0c?sp=r&sv=2018-11-09&sr=b&spr=https&se=2025-06-11T07%3A00%3A34Z&skoid=96c2d410-5711-43a1-aedd-ab1947aa7ab0&sktid=398a6654-997b-47e9-b12b-9515b896b4de&skt=2025-06-11T06%3A55%3A34Z&ske=2025-06-11T07%3A55%3A37Z&sks=b&skv=2018-11-09&sig=2KT%2B9%2BY3mxSrUlPntwmvD2l7g1P6%2FV1eX32HnJ1LLzk%3D)


# easy-rpc

[![License](https://img.shields.io/github/license/neteasezh/easy-rpc)](./LICENSE)
[![Stars](https://img.shields.io/github/stars/neteasezh/easy-rpc)](https://github.com/neteasezh/easy-rpc/stargazers)
[![Issues](https://img.shields.io/github/issues/neteasezh/easy-rpc)](https://github.com/neteasezh/easy-rpc/issues)

## 项目简介

**easy-rpc** 是一款基于 Netty 实现的高性能、轻量级远程过程调用（RPC）框架，支持 TCP/HTTP 协议，提供灵活的 Java 原生 API 以及对 Spring（XML 配置）、Spring Boot 的无缝集成。适用于微服务、分布式系统等多种场景，助力高效、稳定的分布式服务开发。

---

## 核心模块与使用示例

### 1. Core 原生核心模块

#### 功能特色

- 基于 Netty 的高性能异步通信，支持 TCP/HTTP 多协议。
- 服务端/客户端抽象，统一生命周期与资源管理。
- 内置高效编解码器，支持自定义协议扩展。
- 灵活配置，支持线程数、端口、心跳、内容长度等参数。
- 内建服务注册与实例管理、多线程池、健康检查等机制。

#### 使用示例

**服务端（TCP）启动：**
```java
import io.github.easy.rpc.core.netty.tcp.server.EasyRpcServer;
import io.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;

ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
// 注册自定义服务到 registry
EasyRpcServer server = new EasyRpcServer(registry);
server.openSync(); // 同步启动
```

**客户端（TCP）调用：**
```java
import io.github.easy.rpc.core.netty.tcp.client.EasyRpcClient;
import io.github.easy.rpc.core.bean.EasyRpcRequest;
import io.github.easy.rpc.core.bean.EasyRpcResponse;

EasyRpcClient client = new EasyRpcClient();
client.open("localhost", 8888); // 连接服务端
EasyRpcRequest request = new EasyRpcRequest();
// 填充 request 参数
EasyRpcResponse response = client.sendRequest(request, 3000L); // 超时3秒
```

**服务端（HTTP）启动：**
```java
import io.github.easy.rpc.core.netty.http.server.EasyRpcHttpServer;
import io.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;

ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
EasyRpcHttpServer httpServer = new EasyRpcHttpServer(registry);
httpServer.openSync();
```

---

### 2. Spring 集成模块（基于 XML 配置）

#### 功能特色

- 使用 XML 配置文件完成 easy-rpc 的所有组件装配。
- 提供 Bean 方式的服务端、客户端、注册中心、连接池等灵活配置。
- 支持基于包扫描的服务暴露与客户端远程调用。

#### XML 配置示例

**application-easy-rpc.xml**（完整案例，详见 [源码](https://github.com/neteasezh/easy-rpc/blob/master/easy-rpc-spring/src/test/resources/application-easy-rpc.xml)）：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
        ">

    <!-- 基础配置 -->
    <bean id="easyRpcProperties" class="com.github.easy.rpc.core.config.EasyRpcProperties">
        <property name="connectTimeout" value="1000"/>
        <property name="serverPort" value="9999"/>
    </bean>

    <!-- Netty客户端连接池 -->
    <bean id="easyRpcClientPool" class="com.github.easy.rpc.core.netty.tcp.pool.EasyRpcClientPool">
        <constructor-arg index="0" ref="easyRpcProperties"/>
    </bean>

    <!-- 服务注册 -->
    <bean id="serviceProviderInstanceRegistry" class="io.github.easy.rpc.spring.config.AnnotationServiceProviderInstanceRegistry"/>

    <!-- Netty服务端 -->
    <bean id="easyRpcServer" class="com.github.easy.rpc.core.netty.tcp.server.EasyRpcServer" init-method="init">
        <constructor-arg index="0" ref="serviceProviderInstanceRegistry"/>
        <property name="properties" ref="easyRpcProperties"/>
    </bean>

    <!-- RPC调用器 -->
    <bean id="easyRpcInvoker" class="com.github.easy.rpc.core.proxy.EasyRpcInvoker">
        <property name="pool" ref="easyRpcClientPool"/>
    </bean>

    <!-- 服务实现 -->
    <bean class="io.github.HelloServiceImpl"/>

    <!-- 包扫描与服务注册 -->
    <bean id="easyRpcScannerConfigure" class="io.github.easy.rpc.spring.config.EasyRpcScannerConfigurer">
        <property name="basePackage" value="com.github,com.github.rpc"/>
    </bean>
</beans>
```

**启动与调用测试代码：**

```java
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppTest {
    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-easy-rpc.xml");
        HelloService helloService = (HelloService) applicationContext.getBean("helloService");
        String result = helloService.sayHello("zhuhai");
        System.out.println(result);
    }
}
```
> 完整用例和配置请参考 [application-easy-rpc.xml](https://github.com/neteasezh/easy-rpc/blob/master/easy-rpc-spring/src/test/resources/application-easy-rpc.xml) 及 [AppTest.java](https://github.com/neteasezh/easy-rpc/blob/master/easy-rpc-spring/src/test/java/io/github/AppTest.java)。

---

### 3. Spring Boot Starter 模块

#### 功能特色

- 提供 `easy-rpc-spring-boot-starter` Starter，自动装配，零侵入集成（`easy-rpc-spring-boot-client` 和 `easy-rpc-spring-boot-server` 仅为示例应用，实际使用只需引入 starter）。
- 支持 application.yml/application.properties 配置，简化环境部署。
- Starter 自动根据配置选择 TCP/HTTP 协议，无需手动实例化对象。
- 服务端支持自动异步启动，参数灵活可调。

#### 使用示例

**Maven 依赖：**
```xml
<dependency>
    <groupId>io.github.easy-rpc</groupId>
    <artifactId>easy-rpc-spring-boot-starter</artifactId>
    <version>最新版本</version>
</dependency>
```
> 注意：`easy-rpc-spring-boot-client` 和 `easy-rpc-spring-boot-server` 仅为演示和示例模块，实际开发请直接引入 `easy-rpc-spring-boot-starter`。

**配置文件（application.yml）：**
```yaml
easy-rpc:
  protocol: tcp                  # 支持 tcp 或 http
  server-port: 8888
  enable-server: true
  connect-timeout: 3000
  heartbeat-interval: 30
```

**一键启动（服务端/客户端通用）：**
```java
@SpringBootApplication
@EasyRpcScan
public class RpcApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpcApplication.class, args);
    }
}
```

**服务提供者：**
```java
@EasyRpcProvider
public class HelloService {
    public String sayHello(String name) {
        return "hello " + name;
    }
}
```

**服务调用方：**
```java
public interface HelloService {
    @EasyRpcApi(host = "localhost", port = 9998, provider = "com.github.easy.rpc.service.HelloService")
    String sayHello(String name);

}

@RestController
public class HelloController {
    @Autowired
    private HelloService helloService;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(String name) {
        return helloService.sayHello(name);
    }
}
```

---

更多详细用法和进阶特性，请参考源码与文档，或在 [Issue 区](https://github.com/neteasezh/easy-rpc/issues) 交流反馈。

---
