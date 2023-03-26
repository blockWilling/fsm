package com.blockwilling.processor;

import com.blockwilling.context.StateContext;
import com.blockwilling.entity.AbstractEngineEntity;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public interface StateProcessor<T,C> {
    T process(StateContext<C> stateContext) throws Exception;

    <C extends AbstractEngineEntity> boolean filter(StateContext<C> context);
}
