package com.jm.enums;


public enum WSActionEnum {
    fetchData,
    heartbeat,
    subscription,
    unsubscribe,
    snapshotData,
    realTimeData,
    error;

    private WSActionEnum() {
    }

}
