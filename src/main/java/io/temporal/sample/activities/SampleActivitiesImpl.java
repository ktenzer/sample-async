package io.temporal.sample.activities;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionException;
import io.temporal.failure.ApplicationFailure;
import io.temporal.sample.model.SampleResult;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ActivityImpl(taskQueues = "samplequeue")
public class SampleActivitiesImpl implements SampleActivities {
    @Override
    public SampleResult one() {
        sleep(1);
        return new SampleResult("Activity one done...");
    }

    @Override
    public SampleResult two() {
        sleep(1);
        return new SampleResult("Activity two done...");
    }

    @Override
    public SampleResult three(Boolean isFailed) {
        sleep(1);

        if (isFailed) {
            throw ApplicationFailure.newNonRetryableFailure("simulated activity failure from three",
            "some error", null);
        }

        return new SampleResult("Activity three done...");
    }

    @Override
    public SampleResult four() {
        sleep(1);
        return new SampleResult("Activity four done...");
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException ee) {
            // Empty
        }
    }
}
