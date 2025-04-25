package com.jm.facade;

import cn.hutool.core.collection.CollectionUtil;
import com.jm.model.WSBaseEntity;
import com.jm.model.WSMessageDTO;
import com.jm.subscription.IWSSubscriptionService;


import java.util.List;
import java.util.stream.Collectors;


public interface IWSFacade<T extends WSBaseEntity> {

    /**
     * 获取主题
     *
     * @return 主题
     */
    String getTopic();

    /**
     * 获取subscriptionRepository
     *
     * @return subscriptionRepository
     */
    IWSSubscriptionService<T> getSubscriptionRepository();

    /**
     * 获取快照数据
     *
     * @param message message
     * @return 快照数据
     */
    Object getSnapshotData(WSMessageDTO message, String sessionId);

    /**
     * 推送数据到前端
     */
    void sendMessage();

    /**
     * 获取用户map
     *
     * @param sessionId sessionId
     */
    @SuppressWarnings("unchecked")
    default void putSessionId(String sessionId, Object message) {
        IWSSubscriptionService<T> repository = getSubscriptionRepository();
        repository.addSubscription(sessionId, (T) message);
    }

    /**
     * 取消订阅
     *
     * @param sessionId sessionId
     */
    default void unsubscribe(WSMessageDTO message, String sessionId) {
        IWSSubscriptionService<T> repository = getSubscriptionRepository();
        List<T> list = repository.geSubscription(sessionId);
        if (CollectionUtil.isNotEmpty(list)) {
            List<T> toRemove = list.stream()
                    .filter(val -> message.getSubId().equals(val.getSubId()))
                    .collect(Collectors.toList());
            toRemove.forEach(override -> repository.removeSubscription(sessionId, override));
        }
    }

    /**
     * 清空订阅
     *
     * @param sessionId sessionId
     */
    default void clearSubscriptions(String sessionId) {
        IWSSubscriptionService<T> repository = getSubscriptionRepository();
        repository.clearSubscriptions(sessionId);
    }
}
