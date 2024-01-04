package com.github;

import com.alibaba.fastjson.JSON;
import com.github.easy.rpc.core.bean.EasyRpcRequest;
import com.github.easy.rpc.core.bean.EasyRpcRequestConfig;
import com.github.easy.rpc.core.bean.EasyRpcResponse;
import com.github.easy.rpc.core.config.EasyRpcProperties;
import com.github.easy.rpc.core.config.RegistryConfig;
import com.github.easy.rpc.core.enums.ProtocolEnum;
import com.github.easy.rpc.core.netty.http.server.EasyRpcHttpServer;
import com.github.easy.rpc.core.netty.tcp.manage.registries.ServiceProviderInstanceRegistry;
import com.github.easy.rpc.core.netty.tcp.pool.EasyRpcClientPool;
import com.github.easy.rpc.core.netty.tcp.server.EasyRpcServer;
import com.github.easy.rpc.core.proxy.EasyRpcInvoker;
import org.junit.Before;
import org.junit.Test;
import java.util.*;


/**
 * Unit test for simple App.
 */
public class EasyRpcCoreTest {
    private EasyRpcClientPool easyRpcClientPool;

    @Before
    public void before() {
        easyRpcClientPool = new EasyRpcClientPool(new EasyRpcProperties());
    }

    @Test
    public void testServer() {
        ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
        registry.register(new HelloServiceImpl());

        EasyRpcServer easyRpcServer = new EasyRpcServer(registry);
        easyRpcServer.openSync();
    }


    @Test
    public void testClient() throws Exception {
        EasyRpcRequestConfig requestConfig = EasyRpcRequestConfig.EasyRpcRequestConfigBuilder.builder().host("localhost").port(8888).timeout(3000).build();
        for (int i = 0; i < 10; i++) {
            EasyRpcRequest easyRpcRequest = buildRequest("zh" + i);
            EasyRpcResponse response = easyRpcClientPool.sendRequest(easyRpcRequest, requestConfig);
            System.out.println(JSON.toJSONString(response));
        }
    }

    @Test
    public void testHttpServer() {
        ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
        registry.register(new HelloServiceImpl());

        EasyRpcHttpServer easyRpcServer = new EasyRpcHttpServer(registry);
        easyRpcServer.openSync();
    }


    @Test
    public void testHttpClient() throws Exception {
        EasyRpcRequestConfig requestConfig = EasyRpcRequestConfig.EasyRpcRequestConfigBuilder.builder()
                .protocol(ProtocolEnum.HTTP).host("localhost").port(8888).timeout(10000).build();
        for (int i = 0; i < 10; i++) {
            EasyRpcRequest easyRpcRequest = buildRequest("zh" + i);
            EasyRpcResponse response = easyRpcClientPool.sendRequest(easyRpcRequest, requestConfig);
            System.out.println(JSON.toJSONString(response));
        }
    }

    private static EasyRpcRequest buildRequest(String name) {
        EasyRpcRequest easyRpcRequest = new EasyRpcRequest();
        String reqId = UUID.randomUUID().toString();
        easyRpcRequest.setRequestId(reqId);
        easyRpcRequest.setParameters(new Object[]{name});
        easyRpcRequest.setParameterTypes(new Class[]{String.class});
        easyRpcRequest.setClassName(HelloServiceImpl.class.getName());
        easyRpcRequest.setMethodName("sayHello");
        return easyRpcRequest;
    }

    /**
     * 测试代理对象
     *
     * @throws Exception
     */
    @Test
    public void testProxyInvoker() throws Exception {
        // 注册provider对象
        ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
        registry.register(new HelloServiceImpl());

        // 启动服务
        EasyRpcServer easyRpcServer = new EasyRpcServer(registry);
        easyRpcServer.openAsync();

        EasyRpcInvoker easyRpcInvoker = new EasyRpcInvoker(easyRpcClientPool, null);
        // 获取代理对象
        HelloService helloService = easyRpcInvoker.getProxy(HelloService.class);
        String result = helloService.sayHello("刘亦菲");
        System.out.println(result);
    }


    @Test
    public void testLb() throws Exception {
        // 注册provider对象
        ServiceProviderInstanceRegistry registry = new ServiceProviderInstanceRegistry();
        registry.register(new HelloServiceImpl());

        EasyRpcProperties p1 = new EasyRpcProperties();
        p1.setServerPort(8888);
        new EasyRpcServer(registry, p1).openAsync();

        EasyRpcProperties p2 = new EasyRpcProperties();
        p2.setServerPort(8889);
        new EasyRpcServer(registry, p2).openAsync();

        RegistryConfig registryConfig1 = RegistryConfig.RegistryConfigBuilder.builder().host("localhost").port(8888).build();
        RegistryConfig registryConfig2 = RegistryConfig.RegistryConfigBuilder.builder().host("localhost").port(8889).build();
        List<RegistryConfig> configs = Arrays.asList(registryConfig1, registryConfig2);

        EasyRpcInvoker invoker = new EasyRpcInvoker(easyRpcClientPool, configs);
        HelloService helloService = invoker.getProxy(HelloService.class);
        for (int i = 0; i < 10; i++) {
            String result = helloService.sayHello("刘亦菲" + i);
            System.out.println(result);
        }
    }

}
