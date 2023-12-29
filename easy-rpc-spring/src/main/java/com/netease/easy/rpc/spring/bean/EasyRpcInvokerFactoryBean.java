package com.netease.spring.bean;

import com.netease.core.proxy.EasyRpcInvoker;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author zhuhai
 * @date 2023/12/26
 */
public class EasyRpcInvokerFactoryBean implements FactoryBean {
    private EasyRpcInvoker easyRpcInvoker;
    private Class<?> targetClass;

    @Override
    public Object getObject() throws Exception {
        return easyRpcInvoker.getProxy(targetClass);
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public EasyRpcInvoker getEasyRpcInvoker() {
        return easyRpcInvoker;
    }

    public void setEasyRpcInvoker(EasyRpcInvoker easyRpcInvoker) {
        this.easyRpcInvoker = easyRpcInvoker;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
}
