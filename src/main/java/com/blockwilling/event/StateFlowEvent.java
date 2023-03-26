package com.blockwilling.event;


/**
 * Created by blockWilling  on 2022/8/11.
 */
public interface StateFlowEvent {
    String getEventType();
    String getId();
    String getExt();
    Boolean needRetry();
}
