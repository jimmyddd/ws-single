package com.jm.handler;


import com.jm.factory.WSMessageHandleFactory;
import com.jm.enums.WSActionEnum;
import com.jm.facade.IWSFacade;
import com.jm.model.WSMessageDTO;
import com.jm.model.WSResponseVO;
import com.jm.model.WSTempResponseDTO;
import com.jm.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
public class RealTimeDataHandleServiceImpl implements IRealTimeDataHandleService {

    @Override
    public void clearSubscriptions(String sessionId) {
        WSMessageHandleFactory.clearSubscriptions(sessionId);
    }

    @Override
    public WSResponseVO subscription(WSMessageDTO message, String sessionId) {
        WSResponseVO wsResponse = new WSResponseVO();
        wsResponse.setAction(WSActionEnum.snapshotData.name());
        wsResponse.setSubName(message.getSubName());
        wsResponse.setSubId(message.getSubId());
        wsResponse.setTimeStamp(System.currentTimeMillis());
        String subName = message.getSubName();
        IWSFacade<?> service = WSMessageHandleFactory.getService(subName);
        if (Objects.isNull(service)) {
            log.error("订阅主题不存在,subName：{}", subName);
        } else {
            Object snapshotData = service.getSnapshotData(message, sessionId);
            if (Objects.isNull(snapshotData)) {
                log.error("获取快照数据异常。");
            }
            try {
                WSTempResponseDTO cast = ClassUtils.cast(snapshotData, WSTempResponseDTO.class);
                // 缓存sessionId
                service.putSessionId(sessionId, cast.getSaveParams());
                wsResponse.setData(cast.getData());
            } catch (Exception e) {
                log.error("快照数据处理异常：snapshotData：{}。", snapshotData, e);
            }
        }
        return wsResponse;
    }

    @Override
    public void unsubscribe(WSMessageDTO message, String sessionId) {
        String subName = message.getSubName();
        IWSFacade<?> service = WSMessageHandleFactory.getService(subName);
        if (Objects.isNull(service)) {
            log.error("订阅主题不存在,subName：{}", subName);
            return;
        }
        service.unsubscribe(message, sessionId);
    }
}
