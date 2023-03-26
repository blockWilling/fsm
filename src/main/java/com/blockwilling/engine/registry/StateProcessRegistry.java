package com.blockwilling.engine.registry;

import com.blockwilling.context.StateContext;
import com.blockwilling.processor.AbstractStateProcessor;

import java.util.List;

/**
 * Created by blockWilling  on 2022/8/12.
 */
public interface StateProcessRegistry<T,C> {
    List<AbstractStateProcessor<T,C>> acquireStateProcess(StateContext<C> context);
}
