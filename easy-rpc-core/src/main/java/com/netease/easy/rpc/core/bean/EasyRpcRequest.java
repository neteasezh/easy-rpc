package com.netease.core.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author zhuhai
 * @date 2023/12/19
 */
public class EasyRpcRequest implements Serializable {
    private static final long serialVersionUID = -7662246011349323078L;
    private String requestId;
    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }


    @Override
    public String toString() {
        return "EasyRpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

    public static final class EasyRpcRequestBuilder {
        private String requestId;
        private String className;
        private String methodName;
        private Class<?>[] parameterTypes;
        private Object[] parameters;

        private EasyRpcRequestBuilder() {
        }

        public static EasyRpcRequestBuilder builder() {
            return new EasyRpcRequestBuilder();
        }

        public EasyRpcRequestBuilder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public EasyRpcRequestBuilder className(String className) {
            this.className = className;
            return this;
        }

        public EasyRpcRequestBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public EasyRpcRequestBuilder parameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public EasyRpcRequestBuilder parameters(Object[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public EasyRpcRequest build() {
            EasyRpcRequest easyRpcRequest = new EasyRpcRequest();
            easyRpcRequest.setRequestId(requestId);
            easyRpcRequest.setClassName(className);
            easyRpcRequest.setMethodName(methodName);
            easyRpcRequest.setParameterTypes(parameterTypes);
            easyRpcRequest.setParameters(parameters);
            return easyRpcRequest;
        }
    }
}
