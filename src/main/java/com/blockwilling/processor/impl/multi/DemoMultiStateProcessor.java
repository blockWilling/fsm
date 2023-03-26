package com.blockwilling.processor.impl.multi;

import com.blockwilling.context.StateContext;
import com.blockwilling.entity.AbstractEngineEntity;
import com.blockwilling.processor.StateProcessor;
import com.blockwilling.processor.impl.multi.anno.DemoMultiProcessor;

import java.util.Arrays;
import java.util.List;

/**
 * Created by blockWilling  on 2022/8/11.
 */
@DemoMultiProcessor(currentState = {"NEW"}, event = "createOrder", bizCode = {"免费使用", "积分抵用"}
        , scene = {"周年回馈会员用户"}, operatorCode = {"ADMIN"}, category = {"门户线下订单同步"})
public class DemoMultiStateProcessor implements StateProcessor<Integer, Integer> {
    @Override
    public Integer process(StateContext stateContext) {
        return 1;
    }

    @Override
    public <C extends AbstractEngineEntity> boolean filter(StateContext<C> context) {
        DemoMultiProcessor annotation = this.getClass().getAnnotation(DemoMultiProcessor.class);
        String[] category = annotation.category();
        List<String> category1 = context.getCategory();
        List<String> string = Arrays.asList(category);
        for (String s : category1) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
