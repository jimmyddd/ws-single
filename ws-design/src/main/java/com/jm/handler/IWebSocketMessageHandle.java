package com.jm.handler;


import com.jm.enums.SessionStatus;
import com.jm.enums.WSActionEnum;
import com.jm.enums.WSTypeEnum;
import com.jm.model.WSMessageDTO;

import javax.websocket.Session;


public interface IWebSocketMessageHandle {

    /**
     * 获取ws的类型
     */
    default WSTypeEnum getWsType() {
        return WSTypeEnum.NORMAL_WS;
    }


    /**
     * 连接状态改变
     *
     * @param sessionStatus SessionStatus
     * @param session       session
     */
    void sessionStatusTransform(SessionStatus sessionStatus, Session session);

    /**
     * socket消息处理
     *
     * @param message      message
     * @param wsActionEnum wsActionEnum
     * @param sessionId    sessionId
     */
    void messageHandle(WSMessageDTO message, WSActionEnum wsActionEnum, String sessionId);


}
