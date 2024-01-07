package io.github.easy.rpc.core.enums;

/**
 * @author zhuhai
 * @date 2023/12/21
 */
public enum ProtocolEnum {
    TCP,
    HTTP;

    public static ProtocolEnum getProtocol(String protocol) {
        for (ProtocolEnum protocolEnum : ProtocolEnum.values()) {
            if (protocolEnum.name().equalsIgnoreCase(protocol)) {
                return protocolEnum;
            }
        }
        return null;
    }
}
