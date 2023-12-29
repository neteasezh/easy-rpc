package com.netease;

import com.alibaba.fastjson.JSON;
import com.netease.easy.rpc.core.bean.EasyRpcRequest;
import com.netease.easy.rpc.core.bean.EasyRpcResponse;
import com.netease.easy.rpc.core.config.EasyRpcProperties;
import com.netease.easy.rpc.core.config.RegistryConfig;
import com.netease.easy.rpc.core.netty.pool.EasyRpcClientPool;
import com.netease.easy.rpc.core.netty.manage.registries.ServiceProviderRegistry;
import com.netease.easy.rpc.core.netty.server.EasyRpcServer;
import com.netease.easy.rpc.core.proxy.EasyRpcInvoker;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private EasyRpcClientPool easyRpcClientPool;

    @Before
    public void before() {
        easyRpcClientPool = new EasyRpcClientPool(new EasyRpcProperties());
    }

    @Test
    public void testServer() {
        ServiceProviderRegistry context = new ServiceProviderRegistry();
        context.register(new HelloServiceImpl());

        EasyRpcServer easyRpcServer = new EasyRpcServer(context);
        EasyRpcProperties properties = new EasyRpcProperties();
        properties.setServerPort(8888);
        easyRpcServer.setProperties(properties);
        easyRpcServer.openSync();
    }

    @Test
    public void testServer2() {
        ServiceProviderRegistry context = new ServiceProviderRegistry();
        context.register(new HelloServiceImpl());


        EasyRpcServer easyRpcServer = new EasyRpcServer(context);
        EasyRpcProperties properties = new EasyRpcProperties();
        properties.setServerPort(8889);
        easyRpcServer.setProperties(properties);

        easyRpcServer.openSync();
    }

    @Test
    public void testClient() throws Exception {

        RegistryConfig registryConfig = RegistryConfig.RegistryConfigBuilder.builder().host("localhost").port(8888).build();
        for (int i = 0; i < 10; i++) {
            EasyRpcRequest easyRpcRequest = buildRequest("zh" + i);
            EasyRpcResponse response = easyRpcClientPool.sendRequest(registryConfig, easyRpcRequest);
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

    @Test
    public void testProxyInvoker() throws Exception {
        // 注册provider对象
        ServiceProviderRegistry context = new ServiceProviderRegistry();
        context.register(new HelloServiceImpl());

        // 启动服务
        EasyRpcServer easyRpcServer = new EasyRpcServer(context);
        easyRpcServer.openAsync();

        EasyRpcInvoker easyRpcInvoker = new EasyRpcInvoker();
        easyRpcInvoker.setPool(easyRpcClientPool);
        // 获取代理对象
        HelloService helloService = easyRpcInvoker.getProxy(HelloService.class);
        String result = helloService.sayHello("刘亦菲");
        System.out.println(result);
    }


    @Test
    public void testLb() throws Exception {
        // 注册provider对象
        ServiceProviderRegistry context = new ServiceProviderRegistry();
        context.register(new HelloServiceImpl());

        RegistryConfig registryConfig1 = RegistryConfig.RegistryConfigBuilder.builder().host("localhost").port(8888).build();
        RegistryConfig registryConfig2 = RegistryConfig.RegistryConfigBuilder.builder().host("localhost").port(8889).build();
        List<RegistryConfig> configs = Arrays.asList(registryConfig1, registryConfig2);

        EasyRpcInvoker invoker = new EasyRpcInvoker();
        invoker.setPool(easyRpcClientPool);
        invoker.setRegistries(configs);
        HelloServiceImpl helloService = invoker.getProxy(HelloServiceImpl.class);
        String result = helloService.sayHello("刘亦菲");
        System.out.println(result);
    }

}
