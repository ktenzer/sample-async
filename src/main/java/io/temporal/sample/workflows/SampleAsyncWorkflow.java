package io.temporal.sample.workflows;

import io.temporal.sample.model.SampleInput;
import io.temporal.sample.model.SampleResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.HashMap;

@WorkflowInterface
public interface SampleAsyncWorkflow {
    @WorkflowMethod
    SampleResult run(SampleInput input, HashMap<String, SampleResult> partialResults);

    @QueryMethod
    boolean isPaused();

    @SignalMethod
    void resume();
}
