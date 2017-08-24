package com.philemonworks.selfdiagnose.check;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * Created by booms on 21-6-17.
 */
public class CheckEnvironmentProperty extends CheckProperty {
    @Override
    public String getDescription() {
        return "Access a environment System property";
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        String value = System.getenv(this.getProperty());
        this.checkValueAgainstPattern(result, "Environment", "value", property, value);
    }
}
