package io.temporal.sample.activities;

import io.temporal.failure.ApplicationFailure;
import io.temporal.sample.model.SampleResult;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@ActivityImpl(taskQueues = "samplequeue")
public class SampleActivitiesImpl implements SampleActivities {

    private final static Logger log = LoggerFactory.getLogger(SampleActivitiesImpl.class);

    @Override
    public SampleResult one() {
        log.info("Activity one started...");
        sleep(1);

        thisMayOrMayNotThrowAnError("one");

        return new SampleResult("Activity one done...");
    }

    @Override
    public SampleResult two() {
        log.info("Activity two started...");
        sleep(1);

        thisMayOrMayNotThrowAnError("two");

        return new SampleResult("Activity two done...");
    }

    @Override
    public SampleResult three() {
        log.info("Activity three started...");
        sleep(1);

        thisMayOrMayNotThrowAnError("three");

        return new SampleResult("Activity three done...");
    }

    @Override
    public SampleResult four() {
        log.info("Activity four started...");
        sleep(1);

        thisMayOrMayNotThrowAnError("four");

        return new SampleResult("Activity four done...");
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException ee) {
            // Empty
        }
    }

    private void thisMayOrMayNotThrowAnError(String activityName) {
        Random random = new Random();
        double randomValue = random.nextDouble();
        log.info("**** Random value: {}", randomValue);
        if (randomValue < 0.33) { // 33% chance of failure
            log.info("Activity {} failed...", activityName);
            throw ApplicationFailure.newNonRetryableFailure("simulated failure from " + activityName,
                    "some error", null);
        }
    }
}
