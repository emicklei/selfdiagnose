package com.philemonworks.selfdiagnose.check;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

public class CheckSystemProperty extends CheckProperty {
    /**
     * 
     */
    private static final long serialVersionUID = -6407951114588243399L;

    public String getDescription() {
        return "Access a Java global System property";
    }
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        String value = System.getProperty(this.getProperty());
        this.checkValueAgainstPattern(result, "System", "property", property, value);
    }
}
