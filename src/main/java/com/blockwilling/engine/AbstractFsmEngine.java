package com.blockwilling.engine;

import com.blockwilling.context.StateContext;
import com.blockwilling.entity.AbstractEngineEntity;
import com.blockwilling.event.StateFlowEvent;
import com.blockwilling.exception.FsmException;
import com.blockwilling.processor.AbstractStateProcessor;
import com.blockwilling.processor.StateProcessor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by blockWilling  on 2022/8/12.
 */
public abstract class AbstractFsmEngine<T extends AbstractEngineEntity, C extends AbstractEngineEntity> implements FsmEngine {
    @Override
    public T sendEvent(StateFlowEvent orderStateEvent) throws Exception {
        C c = getEventEntity(orderStateEvent);
        if (orderStateEvent.needRetry()) {
            return sendEventRetryable(orderStateEvent, c);
        } else {
            return sendEvent(orderStateEvent, c);
        }
    }

    protected abstract C getEventEntity(StateFlowEvent orderStateEvent);

    public T sendEvent(StateFlowEvent orderStateEvent, C o) throws Exception {
        // 构造当前事件上下文
        StateContext<C> context = this.getStateContext(orderStateEvent, o);
        // 获取当前事件处理器
        StateProcessor<T, C> stateProcessor = this.getStateProcessor(context);
        // 执行处理逻辑
        return stateProcessor.process(context);
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 1.5), listeners = {"DefaultRetryListener"})
    public T sendEventRetryable(StateFlowEvent orderStateEvent, C c) throws Exception {
        return sendEvent(orderStateEvent, c);
    }

    private StateProcessor<T, C> getStateProcessor(StateContext<C> context) {
        List<AbstractStateProcessor<T, C>> processorResult = new LinkedList<>();
        List<AbstractStateProcessor<T, C>> processorList = acquireStateProcess(context);
        for (AbstractStateProcessor<T, C> processor : processorList) {
            if (processor.filter(context)) {
                processorResult.add(processor);
            }
        }
        if (processorResult.size() > 1) {
            throw new FsmException("解析出多个processor");
        }
        return processorResult.get(0);
    }

    protected abstract List<AbstractStateProcessor<T, C>> acquireStateProcess(StateContext<C> context);

    protected abstract StateContext<C> getStateContext(StateFlowEvent orderStateEvent, C o);
}
