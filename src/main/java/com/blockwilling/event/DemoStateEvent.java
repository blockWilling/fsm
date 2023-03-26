package com.blockwilling.event;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public class DemoStateEvent implements StateFlowEvent {
    @Override
    public String getEventType() {
        return "订单取消";
    }

    @Override
    public String getId() {
        return "ORDER01";
    }

    @Override
    public String getExt() {
        return "管理员后台取消";
    }

    public Boolean needRetry() {
        return null;
    }
}
