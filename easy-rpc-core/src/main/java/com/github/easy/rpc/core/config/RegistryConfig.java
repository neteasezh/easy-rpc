package com.github.easy.rpc.core.config;

import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistryConfig that = (RegistryConfig) o;
        return Objects.equals(appName, that.appName) && Objects.equals(host, that.host) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, host, port);
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
