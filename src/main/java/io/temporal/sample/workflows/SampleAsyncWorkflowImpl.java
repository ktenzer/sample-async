package io.temporal.sample.workflows;

import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.sample.activities.SampleActivities;
import io.temporal.sample.model.SampleInput;
import io.temporal.sample.model.SampleResult;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.*;
import org.slf4j.Logger;

import java.time.Duration;

@WorkflowImpl(taskQueues = "samplequeue")
public class SampleAsyncWorkflowImpl implements SampleAsyncWorkflow {
    private final Logger logger = Workflow.getLogger(SampleAsyncWorkflowImpl.class);
    // note per-activity options are set in TemporalOptionsConfig
    private final SampleActivities activities = Workflow.newActivityStub(SampleActivities.class);
    private Promise<Void> activitiesPromise;
    Promise<Void> timerPromise;

    @Override
    public SampleResult run(SampleInput input) {
        // Wait for timer and activities promises, whichever completes first
        activitiesPromise = Async.procedure(this::runActivities);
        try {
            Promise.allOf(activitiesPromise).get();
        } catch (ActivityFailure e) {
            // We need to handler ActivityFailure here as it will be delivered to workflow code in this .get() call
            // However we just log it as will handle later with activitiesPromise.getFailure
            // If we dont handle it here we would fail execution
            logger.warn("Activity failure: " + e.getMessage());
        }


        return new SampleResult("Parent wf: result, no compensation initiated....");

    }

    private void runActivities() {
        activities.one();
        activities.two();
        activities.three();
        activities.four();
    }

}
