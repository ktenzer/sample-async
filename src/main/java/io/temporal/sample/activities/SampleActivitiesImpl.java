package io.temporal.sample.activities;

import io.temporal.failure.ApplicationFailure;
import io.temporal.sample.model.SampleResult;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@ActivityImpl(taskQueues = "samplequeue")
public class SampleActivitiesImpl implements SampleActivities {

    private final static Logger log = LoggerFactory.getLogger(SampleActivitiesImpl.class);

    @Override
    public SampleResult one() {
        log.info("Activity one started...");
        randomSleep();

        thisMayOrMayNotThrowAnError("one");

        return new SampleResult("Activity one done...");
    }

    @Override
    public SampleResult two() {
        log.info("Activity two started...");
        randomSleep();

        thisMayOrMayNotThrowAnError("two");

        return new SampleResult("Activity two done...");
    }

    @Override
    public SampleResult three() {
        log.info("Activity three started...");
        randomSleep();

        thisMayOrMayNotThrowAnError("three");

        return new SampleResult("Activity three done...");
    }

    @Override
    public SampleResult four() {
        log.info("Activity four started...");
        randomSleep();

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

    private void randomSleep() {
        try {
            // Generate a random number between 2 and 11
            int randomNumber = ThreadLocalRandom.current().nextInt(2, 11);
            System.out.println("Sleeping for " + randomNumber + " seconds...");

            // Sleep for the random number of seconds (startToClose is 10 seconds)
            Thread.sleep(randomNumber * 1000); // 10% failure
            System.out.println("Awake after sleeping for " + randomNumber + " seconds.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }    

    private void thisMayOrMayNotThrowAnError(String activityName) {
        Random random = new Random();
        double randomValue = random.nextDouble();
        log.info("**** Random value: {}", randomValue);
        if (randomValue < 0.10) { // 10% chance of failure
            log.info("Activity {} failed...", activityName);
            throw ApplicationFailure.newNonRetryableFailure("simulated failure from " + activityName,
                    "some error", null);
        }
    }
}
