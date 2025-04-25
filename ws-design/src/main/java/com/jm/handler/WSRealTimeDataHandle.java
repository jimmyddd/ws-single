package com.jm.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.jm.enums.SessionStatus;
import com.jm.enums.WSActionEnum;
import com.jm.model.WSMessageDTO;
import com.jm.model.WSResponseVO;
import com.jm.server.WebSocketServerBinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;


@Slf4j
@Component
public class WSRealTimeDataHandle implements IWebSocketMessageHandle {


    @Resource
    private IRealTimeDataHandleService realTimeDataService;

    @Override
    public void sessionStatusTransform(SessionStatus sessionStatus, Session session) {
        String sessionId = session.getId();
        switch (sessionStatus) {
            case OPEN:
                break;
            case CLOSE:
            case ERROR:
                realTimeDataService.clearSubscriptions(sessionId);
                break;
            default:
                log.warn("未知的命令状态！");
        }
    }

    @Override
    public void messageHandle(WSMessageDTO message, WSActionEnum wsActionEnum, String sessionId) {
        TimeInterval timer = DateUtil.timer();
        timer.start();
        switch (wsActionEnum) {
            case subscription:
                log.info("订阅信息处理中...");
                WSResponseVO subscription = realTimeDataService.subscription(message, sessionId);
                WebSocketServerBinary.sendMessage(subscription, sessionId);
                break;
            case unsubscribe:
                log.info("取消订阅信息处理中...");
                realTimeDataService.unsubscribe(message, sessionId);
                break;
            default:
                log.info("未知消息处理");
        }
        log.info("消息类型：{}，处理完成，耗时：{}", wsActionEnum, timer.interval());

    }
}
