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
        // Create cancellation scope for timer
        CancellationScope timerCancellationScope =
                Workflow.newCancellationScope(
                        () -> {
                            timerPromise = Workflow.newTimer(Duration.ofSeconds(input.getTimer()));
                        });
        timerCancellationScope.run();
        // Create cancellation scope for activities
        CancellationScope activityCancellationScope =
                Workflow.newCancellationScope(
                        () -> {
                            activitiesPromise = Async.procedure(this::runActivities);
                        });
        activityCancellationScope.run();

        // Wait for timer and activities promises, whichever completes first
        try {
            Promise.anyOf(timerPromise, activitiesPromise).get();
        } catch (ActivityFailure e) {
            // We need to handler ActivityFailure here as it will be delivered to workflow code in this .get() call
            // However we just log it as will handle later with activitiesPromise.getFailure
            // If we dont handle it here we would fail execution
            logger.warn("Activity failure: " + e.getMessage());
        }

        // if our timer promise completed but activities are still running
        if (timerPromise.isCompleted() && !activitiesPromise.isCompleted()) {
            activityCancellationScope.cancel("timer fired");

            // Wait for the pause signal
            Workflow.await(() -> !awaitingPause);

            Workflow.continueAsNew(input);
            return new SampleResult("Parent wf: result, continue-as-new");
        } else {
            // cancel timer so TimerFired does not get delivered to our worker
            // in case timer does fire before or at the time we want to complete execution
            timerCancellationScope.cancel("activities completed/failed before timer");
            if (activitiesPromise.getFailure() != null) {
                return new SampleResult("Parent wf: result, compensation initiated...");
            }
            return new SampleResult("Parent wf: result, no compensation initiated....");
        }
    }
    
    @Override
    public void resume() {
        awaitingPause = false;
    }

    // Query method implementation
    @Override
    public boolean isPaused() {
        return awaitingPause;
    }

    private void runActivities() {
        activities.one();
        activities.two();
        activities.three();
        activities.four();
    }

}
