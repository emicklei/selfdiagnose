package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

public class SpringEnabledTask extends DiagnosticTask {
    private static final long serialVersionUID = 8619242990958760676L;

    public static boolean RUNNED = false;

    @Override
    public String getDescription() {
        return "SpringEnabledTask";
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        RUNNED = true;
    }
}
