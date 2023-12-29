package com.netease.core.config;

import java.io.Serializable;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public class RegistryConfig implements Serializable {

    private static final long serialVersionUID = 7605572645801055932L;
    private String appName;
    private String host;
    private Integer port;

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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "RegistryConfig{" +
                ", appName='" + appName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }


    public static final class RegistryConfigBuilder {
        private String appName;
        private String host;
        private Integer port;

        private RegistryConfigBuilder() {
        }

        public static RegistryConfigBuilder builder() {
            return new RegistryConfigBuilder();
        }


        public RegistryConfigBuilder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public RegistryConfigBuilder host(String host) {
            this.host = host;
            return this;
        }

        public RegistryConfigBuilder port(Integer port) {
            this.port = port;
            return this;
        }

        public RegistryConfig build() {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setHost(host);
            registryConfig.setPort(port);
            registryConfig.appName = this.appName;
            return registryConfig;
        }
    }
}
