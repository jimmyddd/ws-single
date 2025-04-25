package com.jm.subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WSSubscriptionServiceImpl<T> implements IWSSubscriptionService<T> {


    private final ConcurrentHashMap<String, List<T>> subscriptionMap = new ConcurrentHashMap<>();

    @Override
    public List<T> geSubscription(String sessionId) {
        return subscriptionMap.get(sessionId);
    }

    @Override
    public void addSubscription(String sessionId, T message) {
        subscriptionMap.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }

    @Override
    public void removeSubscription(String sessionId, T message) {
        List<T> overrides = subscriptionMap.get(sessionId);
        if (overrides != null) {
            overrides.remove(message);
            if (overrides.isEmpty()) {
                subscriptionMap.remove(sessionId);
            }
        }
    }

    @Override
    public void clearSubscriptions(String sessionId) {
        subscriptionMap.remove(sessionId);
    }

    @Override
    public List<T> getAllSubscriptions() {
        return subscriptionMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
