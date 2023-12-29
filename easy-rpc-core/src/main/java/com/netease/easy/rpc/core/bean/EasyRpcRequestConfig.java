package com.netease.easy.rpc.core.bean;

import com.netease.easy.rpc.core.enums.ProtocolEnum;

/**
 * @author zhuhai
 * @date 2023/12/28
 */
public class EasyRpcRequestConfig {
    private String appName;
    private String host;
    private Integer port;
    private Integer timeout;
    private String provider;
    private ProtocolEnum protocol;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public ProtocolEnum getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }


    public static final class EasyRpcRequestConfigBuilder {
        private String appName;
        private String host;
        private Integer port;
        private Integer timeout;
        private String provider;
        private ProtocolEnum protocol;

        private EasyRpcRequestConfigBuilder() {
        }

        public static EasyRpcRequestConfigBuilder builder() {
            return new EasyRpcRequestConfigBuilder();
        }

        public EasyRpcRequestConfigBuilder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public EasyRpcRequestConfigBuilder host(String host) {
            this.host = host;
            return this;
        }

        public EasyRpcRequestConfigBuilder port(Integer port) {
            this.port = port;
            return this;
        }

        public EasyRpcRequestConfigBuilder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public EasyRpcRequestConfigBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public EasyRpcRequestConfigBuilder protocol(ProtocolEnum protocol) {
            this.protocol = protocol;
            return this;
        }

        public EasyRpcRequestConfig build() {
            EasyRpcRequestConfig easyRpcRequestConfig = new EasyRpcRequestConfig();
            easyRpcRequestConfig.setAppName(appName);
            easyRpcRequestConfig.setHost(host);
            easyRpcRequestConfig.setPort(port);
            easyRpcRequestConfig.setTimeout(timeout);
            easyRpcRequestConfig.setProvider(provider);
            easyRpcRequestConfig.setProtocol(protocol);
            return easyRpcRequestConfig;
        }
    }
}
