package com.blockwilling.check;

import com.blockwilling.context.StateContext;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by blockWilling  on 2022/8/11.
 */
@Component
@Slf4j
public class CheckerExecutor implements InitializingBean, DisposableBean {
    private ExecutorService executor = null;

    /**
     * 执行并行校验器，
     * 按照任务投递的顺序判断返回。
     */
    public <T,C> CheckResult<T> parallelCheck(List<Checker<T,C>> checkers, StateContext<C> context) {
        if (!CollectionUtils.isEmpty(checkers)) {
            if (checkers.size() == 1) {
                checkers.get(0).check(context);
            }
            List<Future<CheckResult<T>>> resultList = Collections.synchronizedList(new ArrayList<>(checkers.size()));
            checkers.sort(Comparator.comparingInt(Checker::order));
            for (Checker<T,C> c : checkers) {
                Future<CheckResult<T>> future = executor.submit(() -> {
                    return c.check(context);
                });
                resultList.add(future);
            }
            for (Future<CheckResult<T>> future : resultList) {
                try {
                    CheckResult<T> sr = future.get();
                    if (!sr.isSuccess()) {
                        return sr;
                    }
                } catch (Exception e) {
                    log.error("parallelCheck executor.submit error.", e);
                    throw new RuntimeException(e);
                }
            }
        }
        return new CheckResult<>(true);
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadFactory huToolFactory = new ThreadFactoryBuilder().setNamePrefix("CheckerExecutor-").build();
        executor = new ThreadPoolExecutor(Integer.max(availableProcessors / 2, 1), Integer.max(availableProcessors / 2, 1),
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000), huToolFactory);
    }

    public <T,C> CheckResult<T> serialCheck(List<Checker<T,C>> syncChecker, StateContext<C> context) {
        for (Checker<T,C> checker : syncChecker) {
            CheckResult<T> check = checker.check(context);
            if (!check.isSuccess()) {
                return check;
            }
        }
        return new CheckResult<>(true);
    }

    /**
     * 执行checker的释放操作
     */
    public <T, C> void releaseCheck(Checkable<T,C> checkable, StateContext<C> context, CheckResult<T> result) {
        List<Checker<T,C>> checkers = new ArrayList<>();
        checkers.addAll(checkable.getParamChecker());
        checkers.addAll(checkable.getSyncChecker());
        checkers.addAll(checkable.getAsyncChecker());
        checkers.removeIf(Checker::needRelease);
        if (!CollectionUtils.isEmpty(checkers)) {
            if (checkers.size() == 1) {
                checkers.get(0).release(context, result);
                return;
            }
            CountDownLatch latch = new CountDownLatch(checkers.size());
            for (Checker<T,C> c : checkers) {
                executor.execute(() -> {
                    try {
                        c.release(context, result);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
