package com.blockwilling.processor;

import com.blockwilling.check.CheckResult;
import com.blockwilling.check.Checkable;
import com.blockwilling.check.CheckerExecutor;
import com.blockwilling.context.StateContext;

import javax.annotation.Resource;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public abstract class AbstractStateProcessor<T, C> implements StateProcessor<T, C>, StateActionStep<T, C> {
    @Resource
    private CheckerExecutor checkerExecutor;

    @Override
    public final T process(StateContext<C> context) throws Exception {
        CheckResult<T> result = null;
        Checkable<T,C> checkable = null;
        T ret;
        try {
            checkable = this.getCheckable(context);
            // 参数校验器
            result = checkerExecutor.serialCheck(checkable.getParamChecker(), context);
            if (!result.isSuccess()) {
                return result.getBody();
            }
            // 数据准备
            this.prepare(context);
            // 串行校验器
            result = checkerExecutor.serialCheck(checkable.getSyncChecker(), context);
            if (!result.isSuccess()) {
                return result.getBody();
            }
            // 并行校验器
            result = checkerExecutor.parallelCheck(checkable.getAsyncChecker(), context);
            if (!result.isSuccess()) {
                return result.getBody();
            }
            // getNextState不能在prepare前，因为有的nextState是根据prepare中的数据转换而来
            String nextState = this.getNextState(context);
            // 业务逻辑
            this.action(nextState, context);
            // 持久化
            ret = this.save(nextState, context);
            // after
            this.after(context);
        } finally {
            checkerExecutor.releaseCheck(checkable, context, result);
        }
        return ret;
    }
}
