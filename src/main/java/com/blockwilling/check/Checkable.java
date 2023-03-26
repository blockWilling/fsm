package com.blockwilling.check;

import java.util.Collections;
import java.util.List;

/**
 * Created by blockWilling  on 2022/8/11.
 */
public interface Checkable<T,C> {
    /**
     * 参数校验
     */
    default List<Checker<T,C>> getParamChecker() {
        return Collections.EMPTY_LIST;
    }

    /**
     * 需同步执行的状态检查器
     */
    default List<Checker<T,C>> getSyncChecker() {
        return Collections.EMPTY_LIST;
    }

    /**
     * 可异步执行的校验器
     */
    default List<Checker<T,C>> getAsyncChecker() {
        return Collections.EMPTY_LIST;
    }
}
