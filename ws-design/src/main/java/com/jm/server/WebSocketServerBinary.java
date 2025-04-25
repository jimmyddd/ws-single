package com.jm.server;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jm.enums.SessionStatus;
import com.jm.enums.WSActionEnum;
import com.jm.handler.IWebSocketMessageHandle;
import com.jm.model.WSMessageDTO;
import com.jm.model.WSResponseVO;
import com.jm.utils.CompressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint(value = "/websocket")
@ConditionalOnProperty(prefix = "component.scan", name = "ws-server", havingValue = "true")
public class WebSocketServerBinary {

    /**
     * 心跳超时时间，暂时设置15秒，可以改写成配置的方式
     */
    public static final int MAX_IDLE_TIMEOUT = 1000 * 60 * 10;

    /**
     * 存放所有在线的客户端
     */
    public static ConcurrentHashMap<String, Session> CLIENTS = new ConcurrentHashMap<>();

    /**
     * 消息处理handler
     */
    public static List<IWebSocketMessageHandle> HANDLE_LIST = new ArrayList<>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        sessionStatusTransform(SessionStatus.OPEN, session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        sessionStatusTransform(SessionStatus.CLOSE, session);
    }


    @OnError
    public void onError(Session session, Throwable error) {
        sessionStatusTransform(SessionStatus.ERROR, session);
        log.error("链接异常：{}", error.getMessage(), error);
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param buffer 客户端发送过来的消息，先解压再数据转换
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer buffer) {
        String message = getMessage(buffer);
        // 数据格式校验
        if (!JSONUtil.isTypeJSON(message)) {
            log.warn("数据格式异常");
        }
        // 解析消息体
        WSMessageDTO wsMessage = JSONUtil.toBean(message, WSMessageDTO.class);
        if (Objects.isNull(wsMessage) || StrUtil.isBlank(wsMessage.getAction())) {
            log.warn("消息反序列失败，或者消息类型为空");
            return;
        }
        WSActionEnum wsActionEnum = EnumUtil.likeValueOf(WSActionEnum.class, wsMessage.getAction());
        if (Objects.isNull(wsActionEnum)) {
            log.warn("命令错误");
            return;
        }
        if (wsActionEnum.equals(WSActionEnum.heartbeat)) {
            return;
        }
        HANDLE_LIST.forEach(webSocketMessageHandle ->
                webSocketMessageHandle.messageHandle(wsMessage, wsActionEnum, session.getId()));
    }

    /**
     * 获取消息体
     *
     * @param buffer buffer
     * @return message
     */
    @Nullable
    private static String getMessage(ByteBuffer buffer) {
        String message;
        try {
            byte[] bytes = CompressionUtils.decompressWithInflater(buffer.array());
            if (ArrayUtil.isEmpty(bytes)) {
                log.warn("消息为空");
                return null;
            }
            message = StrUtil.str(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("消息解压失败", e);
            return null;
        }
        log.info("收到客户端消息：{}", message);
        return message;
    }

    /**
     * 发送消息给组
     *
     * @param sessionIdGroup sessionIdGroup
     * @param record         record
     */
    public static void sendGroupMessage(List<String> sessionIdGroup, Object record) {
        for (String sessionId : sessionIdGroup) {
            if (!CLIENTS.containsKey(sessionId)) {
                log.warn("发送消息失败，当前sessionId对应客户端不存在");
                continue;
            }
            Session session = CLIENTS.get(sessionId);
            if (session.isOpen()) {
                try {
                    byte[] bytes = CompressionUtils.compressWithDeflater(
                            JSONUtil.toJsonStr(record).getBytes(StandardCharsets.UTF_8));
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
                } catch (Exception e) {
                    log.error("服务端发送消息给客户端失败 message:[{}]", e.getMessage(), e);
                }
            } else {
                log.warn("客户端已经离线，sessionId:[{}]", sessionId);
            }
        }
    }

    /**
     * 服务端发送消息给客户端
     */
    public static void sendMessage(WSResponseVO message, String sessionId) {
        message = wrapErrorResponse(message);
        Session session = CLIENTS.get(sessionId);
        if (session == null) {
            log.error("No websocket session found for user: {}, message: {}.", sessionId, message);
            return;
        }
        try {
            String jsonStr = JSONUtil.toJsonStr(message);
            byte[] bytes = CompressionUtils.compressWithDeflater(jsonStr.getBytes(StandardCharsets.UTF_8));
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败 message:[{}]", e.getMessage(), e);
        }
    }

    /**
     * 封装错误消息
     *
     * @param wsResponse wsResponse
     * @return WSResponseVO
     */
    private static WSResponseVO wrapErrorResponse(WSResponseVO wsResponse) {
        if (Objects.nonNull(wsResponse.getData())) {
            return wsResponse;
        }
        WSResponseVO result = new WSResponseVO();
        result.setAction(WSActionEnum.error.name());
        result.setSubName(wsResponse.getSubName());
        result.setSubId(wsResponse.getSubId());
        result.setTimeStamp(System.currentTimeMillis());
        JSONObject obj = JSONUtil.createObj();
        obj.putOnce("code", 500);
        obj.putOnce("message", "封装信息失败");
        result.setData(obj);
        return result;
    }


    /**
     * 消息处理
     *
     * @param sessionStatus SessionStatus
     * @param session       session
     */
    private void sessionStatusTransform(SessionStatus sessionStatus, Session session) {
        String sessionId = session.getId();
        switch (sessionStatus) {
            case OPEN:
                session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);
                CLIENTS.put(sessionId, session);
                break;
            case CLOSE:
            case ERROR:
                CLIENTS.remove(sessionId);
                break;
            default:
                log.warn("未知的命令状态！");
        }
        HANDLE_LIST.forEach(
                webSocketMessageHandle -> webSocketMessageHandle.sessionStatusTransform(sessionStatus, session));
        log.info("session状态改变：{}，sessionId：{}，当前在线人数为：{}", sessionStatus, sessionId, CLIENTS.size());
    }


}
