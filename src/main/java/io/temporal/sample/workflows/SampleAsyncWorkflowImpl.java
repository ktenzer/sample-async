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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@WorkflowImpl(taskQueues = "samplequeue")
public class SampleAsyncWorkflowImpl implements SampleAsyncWorkflow {
    private final Logger logger = Workflow.getLogger(SampleAsyncWorkflowImpl.class);
    // note per-activity options are set in TemporalOptionsConfig
    private final SampleActivities activities = Workflow.newActivityStub(SampleActivities.class);
    private Promise<Void> activitiesPromise;
    Promise<Void> timerPromise;
    private boolean awaitingPause = true;

    @Override
    public SampleResult run(SampleInput input) {

        // Generate random boolean to fail activity
        Random rd = new Random(); 
        Boolean isFailed = rd.nextBoolean();

        try {
            activities.one();
            activities.two();
            activities.three(isFailed);
            activities.four();
        } catch (ActivityFailure e) {
            // Wait for the pause signal
            Workflow.await(() -> !awaitingPause);

            Workflow.continueAsNew(input);
            return new SampleResult("Workflow resumed, continue-as-new");
        }
        return new SampleResult("Workflow complete");
    }
    
    @Override
    public void resume() {
        awaitingPause = false;
    }

    @Override
    public boolean isPaused() {
        return awaitingPause;
    }
}
