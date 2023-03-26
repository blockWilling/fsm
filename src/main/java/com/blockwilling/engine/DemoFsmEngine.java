package com.blockwilling.engine;

import com.blockwilling.context.StateContext;
import com.blockwilling.engine.registry.StateProcessRegistry;
import com.blockwilling.entity.DemoEngineEntity;
import com.blockwilling.event.StateFlowEvent;
import com.blockwilling.processor.AbstractStateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by blockWilling  on 2022/8/12.
 */
@Component
public class DemoFsmEngine extends AbstractFsmEngine<DemoEngineEntity,DemoEngineEntity> {
    @Autowired
    private
    StateProcessRegistry<DemoEngineEntity,DemoEngineEntity> demoStateProcessRegistry;
    @Override
    protected DemoEngineEntity getEventEntity(StateFlowEvent orderStateEvent) {
        return new DemoEngineEntity();
    }

    @Override
    protected List<AbstractStateProcessor<DemoEngineEntity,DemoEngineEntity>> acquireStateProcess(StateContext<DemoEngineEntity> context) {
        return demoStateProcessRegistry.acquireStateProcess(context);
    }

    @Override
    protected StateContext<DemoEngineEntity> getStateContext(StateFlowEvent orderStateEvent, DemoEngineEntity o) {
        StateContext<DemoEngineEntity> stateContext = new StateContext<>();
        stateContext.setT(o);
        stateContext.setStateFlowEvent(orderStateEvent);
        return stateContext;
    }
}
