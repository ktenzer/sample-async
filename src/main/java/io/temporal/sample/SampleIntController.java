package io.temporal.sample;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.sample.model.SampleInput;
import io.temporal.sample.model.SampleResult;
import io.temporal.sample.workflows.SampleAsyncWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SampleIntController {

    @Autowired
    WorkflowClient client;

    @GetMapping("/")
    public String update() {
        return "index";
    }

    @PostMapping(
            value = "/run",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity sampleInt(@RequestBody SampleInput input) {
        SampleAsyncWorkflow workflow =
                client.newWorkflowStub(SampleAsyncWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setTaskQueue("samplequeue")
                                .setWorkflowId("sample-workflow")
                                .build());
        try {
            return new ResponseEntity<>(workflow.run(input, null), HttpStatus.OK);
        } catch (WorkflowFailedException e) {
            return new ResponseEntity<>(new SampleResult("workflow failed: " + e.getMessage()), HttpStatus.OK);
        }
    }

}

