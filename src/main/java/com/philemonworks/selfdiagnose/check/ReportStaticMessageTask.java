package com.philemonworks.selfdiagnose.check;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * Class to report a message with a given status.
 * <p>
 * This task doesn't actually check anything, it just adds a line to the report.
 */
public class ReportStaticMessageTask extends DiagnosticTask {

    private final boolean passed;
    private final String message;
    private final String description;

    /**
     * @param passed  true and this check is considered to be passed, otherwise failed.
     * @param message The message to display
     */
    public ReportStaticMessageTask(boolean passed, String message, String description) {
        super();

        this.passed = passed;
        this.message = message;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        if (passed) {
            result.setPassedMessage(message);
        } else {
            result.setFailedMessage(message);
        }
    }
}
