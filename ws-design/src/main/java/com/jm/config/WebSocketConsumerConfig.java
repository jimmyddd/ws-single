package com.jm.config;


import com.jm.enums.WSTypeEnum;
import com.jm.handler.IWebSocketMessageHandle;
import com.jm.server.WebSocketServerBinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.List;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "component.scan", name = "ws-server", havingValue = "true")
public class WebSocketConsumerConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注入一个ServerEndpointExporter,该Bean会自动注册使用@ServerEndpoint注解申明的websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setHandler(List<IWebSocketMessageHandle> messageHandleList) {
        for (IWebSocketMessageHandle item : messageHandleList) {
            WSTypeEnum wsType = item.getWsType();
            switch (wsType) {
                case NORMAL_WS:
                    WebSocketServerBinary.HANDLE_LIST.add(item);
                    break;
                default:
                    log.error("wsTypeEnum error");
            }
        }
    }
}
