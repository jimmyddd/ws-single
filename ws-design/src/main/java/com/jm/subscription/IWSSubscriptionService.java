package com.jm.subscription;

import java.util.List;


public interface IWSSubscriptionService<T> {

    /**
     * 获取订阅信息
     *
     * @param sessionId 连接id
     * @return 连接信息
     */
    List<T> geSubscription(String sessionId);

    /**
     * 添加连接信息
     *
     * @param sessionId 连接id
     * @param message   连接信息
     */
    void addSubscription(String sessionId, T message);

    /**
     * 移除连接信息
     *
     * @param sessionId 连接id
     * @param message   连接信息
     */
    void removeSubscription(String sessionId, T message);

    /**
     * 清除连接信息
     *
     * @param sessionId 连接id
     */
    void clearSubscriptions(String sessionId);

    /**
     * 获取所有连接信息
     *
     * @return 连接信息
     */
    List<T> getAllSubscriptions();
}
