package com.blockwilling.processor;

import com.blockwilling.check.Checkable;
import com.blockwilling.context.StateContext;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public interface StateActionStep<T,C> {
    /**
     * 准备数据
     */
    default void prepare(StateContext<C> context) {
    }
    /**
     * 校验
     */
    T check(StateContext<C> context);
    /**
     * 获取当前状态处理器处理完毕后，所处于的下一个状态
     */
    String getNextState(StateContext<C> context);
    /**
     * 状态动作方法，主要状态迁移逻辑
     */
    T action(String nextState, StateContext<C> context) throws Exception;
    /**
     * 状态数据持久化
     */
    T save(String nextState, StateContext<C> context) throws Exception;
    /**
     * 状态迁移成功，持久化后执行的后续处理，一般这里after之后的操作是发送mq消息，
     * 需要保证消息发送与{@link StateActionStep#save(java.lang.String, StateContext)}的数据持久化的一致性
     */
    void after(StateContext<C> context);


    Checkable<T,C> getCheckable(StateContext<C> context);
}
