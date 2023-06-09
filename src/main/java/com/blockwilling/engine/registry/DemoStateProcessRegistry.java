package com.blockwilling.engine.registry;

import com.blockwilling.context.StateContext;
import com.blockwilling.entity.DemoEngineEntity;
import com.blockwilling.processor.AbstractStateProcessor;
import com.blockwilling.processor.StateProcessor;
import com.blockwilling.processor.impl.multi.anno.DemoMultiProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by blockWilling  on 2022/8/12.
 */
@Component
public class DemoStateProcessRegistry implements BeanPostProcessor,StateProcessRegistry<DemoEngineEntity,DemoEngineEntity> {
    /**
     * 第一层key是订单状态。
     * 第二层key是订单状态对应的事件，一个状态可以有多个事件。
     * 第三层key是具体场景code，场景下对应的多个处理器，需要后续进行过滤选择出一个具体的执行。
     */
    private  Map<String, Map<String, Map<String, List<AbstractStateProcessor<DemoEngineEntity,DemoEngineEntity>>>>> stateProcessMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractStateProcessor && bean.getClass().isAnnotationPresent(DemoMultiProcessor.class)) {
            DemoMultiProcessor annotation = bean.getClass().getAnnotation(DemoMultiProcessor.class);
            String[] states = annotation.currentState();
            String event = annotation.event();
            String[] bizCodes = annotation.bizCode().length == 0 ? new String[]{"#"} : annotation.bizCode();
            String[] sceneIds = annotation.scene().length == 0 ? new String[]{"#"} : annotation.scene();
            initProcessMap(states, event, bizCodes, sceneIds, stateProcessMap, (AbstractStateProcessor<DemoEngineEntity,DemoEngineEntity>) bean);
        }
        return bean;
    }

    private <E extends StateProcessor> void initProcessMap(String[] states, String event, String[] bizCodes, String[] sceneIds,
                                                           Map<String, Map<String, Map<String, List<E>>>> map, E processor) {
        for (String bizCode : bizCodes) {
            for (String sceneId : sceneIds) {
                Arrays.asList(states).parallelStream().forEach(orderStateEnum -> {
                    registerStateHandlers(orderStateEnum, event, bizCode, sceneId, map, processor);
                });
            }
        }
    }

    /**
     * 初始化状态机处理器
     */
    public <E extends StateProcessor> void registerStateHandlers(String orderStateEnum, String event, String bizCode, String sceneId,
                                                                 Map<String, Map<String, Map<String, List<E>>>> map, E processor) {
        // state维度
        if (!map.containsKey(orderStateEnum)) {
            map.put(orderStateEnum, new ConcurrentHashMap<>());
        }
        Map<String, Map<String, List<E>>> stateTransformEventEnumMap = map.get(orderStateEnum);
        // event维度
        if (!stateTransformEventEnumMap.containsKey(event)) {
            stateTransformEventEnumMap.put(event, new ConcurrentHashMap<>());
        }
        // bizCode and sceneId
        Map<String, List<E>> processorMap = stateTransformEventEnumMap.get(event);
        String bizCodeAndSceneId = bizCode + "@" + sceneId;
        if (!processorMap.containsKey(bizCodeAndSceneId)) {
            processorMap.put(bizCodeAndSceneId, new CopyOnWriteArrayList<>());
        }
        processorMap.get(bizCodeAndSceneId).add(processor);
    }
    @Override
    public  List<AbstractStateProcessor<DemoEngineEntity,DemoEngineEntity>> acquireStateProcess(StateContext<DemoEngineEntity> context) {
        return stateProcessMap.get(context.getCurrentState()).get(context.getEvent()).get(context.getBizCode() + "@" + context.getScene());
    }
}

