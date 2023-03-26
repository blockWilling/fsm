package com.blockwilling.engine;

import com.blockwilling.event.StateFlowEvent;

/**
 * Created by blockWilling  on 2022/8/12.
 */
public interface FsmEngine<T> {
    T sendEvent(StateFlowEvent orderStateEvent) throws Exception;

}
