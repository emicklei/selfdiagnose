package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * Sample task that simulates a successful outcome.
 *
 * @author Yaroslav Skopets
 */
public class SuccessfulTask extends DiagnosticTask {

    private static final long serialVersionUID = 1L;

    private final String passedMessage;

    public SuccessfulTask(String passedMessage) {
        this.passedMessage = passedMessage;
    }

    @Override
    public String getDescription() {
        return "successful task";
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        result.setPassedMessage(passedMessage);
    }
}
