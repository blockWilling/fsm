package com.blockwilling.context;

import com.blockwilling.event.StateFlowEvent;
import lombok.Data;

import java.util.List;

/**
 * Created by blockWilling  on 2022/8/11.
 */
@Data
public class StateContext<T> {
    T t;
    StateFlowEvent stateFlowEvent;
    String currentState;
    String event;
    List<String> bizCode;
    List<String> scene;
    List<String> operatorCode;
    List<String> category;
}
