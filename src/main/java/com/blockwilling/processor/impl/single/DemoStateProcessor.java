package com.blockwilling.processor.impl.single;

import com.blockwilling.context.StateContext;
import com.blockwilling.entity.AbstractEngineEntity;
import com.blockwilling.processor.StateProcessor;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public class DemoStateProcessor implements StateProcessor<Integer, Integer> {
    @Override
    public Integer process(StateContext stateContext) {
        return 0;
    }

    @Override
    public <C extends AbstractEngineEntity> boolean filter(StateContext<C> context) {
        return false;
    }
}
