package io.temporal.sample.workflows;

import io.temporal.failure.ActivityFailure;
import io.temporal.sample.activities.SampleActivities;
import io.temporal.sample.model.SampleInput;
import io.temporal.sample.model.SampleResult;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.HashMap;

@WorkflowImpl(taskQueues = "samplequeue")
public class SampleAsyncWorkflowImpl implements SampleAsyncWorkflow {
    private final Logger log = Workflow.getLogger(SampleAsyncWorkflowImpl.class);
    // note per-activity options are set in TemporalOptionsConfig
    private final SampleActivities activities = Workflow.newActivityStub(SampleActivities.class);
    private boolean awaitingPause = true;

    @Override
    public SampleResult run(SampleInput input, HashMap<String, SampleResult> partialResults) {

        if (partialResults == null) {
            partialResults = new HashMap<>();
        }

        while (true) {
            try {
                SampleResult result1 = partialResults.get("resultOne");
                if (result1 == null) {
                    result1 = activities.one();
                    partialResults.put("resultOne", result1);
                }

                SampleResult result2 = partialResults.get("resultTwo");
                if (result2 == null) {
                    result2 = activities.two();
                    partialResults.put("resultTwo", result2);
                }

                SampleResult result3 = partialResults.get("resultThree");
                if (result3 == null) {
                    result3 = activities.three();
                    partialResults.put("resultThree", result3);
                }

                SampleResult result4 = partialResults.get("resultFour");
                if (result4 == null) {
                    result4 = activities.four();
                    partialResults.put("resultFour", result4);
                }

                return new SampleResult("Workflow complete");
                // all activities have completed successfully
            } catch (ActivityFailure e) {
                log.info("Activity failed: {}", e.getMessage());

                // Wait for the pause signal and the start the workflow from the beginning
                log.info("Pausing workflow, waiting for 'resume' signal...");
                Workflow.await(() -> !awaitingPause);

                awaitingPause = true;

                log.info("Resuming workflow...");
            }
        }
    }

    @Override
    public void resume() {
        log.info("Signal 'resume' received");
        awaitingPause = false;
    }

    @Override
    public boolean isPaused() {
        return awaitingPause;
    }
}
