package com.blockwilling.check;

import lombok.Data;

/**
 * Created by blockWilling  on 2022/8/11.
 */
@Data
public class CheckResult<T> {
    public CheckResult(Boolean success) {
        this.success = success;
    }

    public CheckResult() {
    }
    T body;

    boolean success;
}
