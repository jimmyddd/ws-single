package com.jm.factory;


import com.jm.facade.IWSFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Configuration
public class WSMessageHandleFactory {


    private volatile static HashMap<String, IWSFacade<?>> SUB_NAME_MAP;

    @Autowired
    public void setMessageHandleList(List<IWSFacade<?>> messageHandleList) {
        SUB_NAME_MAP = new HashMap<>(messageHandleList.size());
        for (IWSFacade<?> iwsFacade : messageHandleList) {
            SUB_NAME_MAP.put(iwsFacade.getTopic(), iwsFacade);
        }
    }

    /**
     * 根据命令获取对应命令的执行方法
     *
     * @param subName 订阅名称
     * @return 具体实现类
     */
    public static IWSFacade<?> getService(String subName) {
        if (Objects.isNull(subName)) {
            return null;
        }
        return SUB_NAME_MAP.get(subName);
    }


    public static void clearSubscriptions(String sessionId) {
        SUB_NAME_MAP.values()
                .forEach(service -> service.clearSubscriptions(sessionId));
    }


}
