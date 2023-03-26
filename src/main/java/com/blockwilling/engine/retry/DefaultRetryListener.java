package com.blockwilling.engine.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

/**
 * Created by blockWilling  on 2022/8/12.
 */
@Component("DefaultRetryListener")
@Slf4j
public class DefaultRetryListener implements RetryListener {
    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return false;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if(throwable!=null){
            log.error("【DefaultRetryListener】close",throwable);
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if(throwable!=null){
            log.error("【DefaultRetryListener】onError",throwable);
        }
    }
}
