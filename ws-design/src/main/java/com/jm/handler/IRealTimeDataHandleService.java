package com.jm.handler;


import com.jm.model.WSMessageDTO;
import com.jm.model.WSResponseVO;


public interface IRealTimeDataHandleService {

    /**
     * close
     */
    void clearSubscriptions(String sessionId);

    /**
     * 订阅
     *
     * @param message   message
     * @param sessionId sessionId
     * @return Object
     */
    WSResponseVO subscription(WSMessageDTO message, String sessionId);

    /**
     * 取消订阅
     *
     * @param message   message
     * @param sessionId sessionId
     */
    void unsubscribe(WSMessageDTO message, String sessionId);
}
