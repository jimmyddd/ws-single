package com.jm.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.jm.enums.WSActionEnum;
import com.jm.facade.IWSFacade;
import com.jm.model.WSDemoDTO;
import com.jm.model.WSMessageDTO;
import com.jm.model.WSResponseVO;
import com.jm.model.WSTempResponseDTO;
import com.jm.server.WebSocketServerBinary;
import com.jm.subscription.IWSSubscriptionService;
import com.jm.subscription.WSSubscriptionServiceImpl;
import com.jm.vo.DemoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class DemoFacadeImpl implements IWSFacade<WSDemoDTO> {



    private final IWSSubscriptionService<WSDemoDTO> subscriptionRepository
            = new WSSubscriptionServiceImpl<>();

    @Override
    public String getTopic() {
        return "test";
    }

    @Override
    public IWSSubscriptionService<WSDemoDTO> getSubscriptionRepository() {
        return subscriptionRepository;
    }

    @Override
    public Object getSnapshotData(WSMessageDTO message, String sessionId) {
        WSDemoDTO demoDTO = new WSDemoDTO();

        demoDTO.setSessionId(sessionId);

        BeanUtil.copyProperties(message, demoDTO);

        DemoVO demoVO = new DemoVO();

        return WSTempResponseDTO.builder().data(demoVO).saveParams(demoDTO).build();

    }

    @Override
    public void sendMessage() {
        List<WSDemoDTO> list = subscriptionRepository.getAllSubscriptions();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<DemoVO> demoVOS = new ArrayList<>();

        TimeInterval timer = DateUtil.timer();
        for (WSDemoDTO item : list) {
            WSResponseVO wsResponse = BeanUtil.copyProperties(item, WSResponseVO.class);
            wsResponse.setAction(WSActionEnum.realTimeData.name());
            wsResponse.setTimeStamp(System.currentTimeMillis());
            wsResponse.setData(demoVOS);
            WebSocketServerBinary.sendMessage(wsResponse, item.getSessionId());
        }
        log.info("推送测试数据，耗时：{}", timer.interval());

    }
}
