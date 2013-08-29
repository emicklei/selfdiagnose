package com.philemonworks.selfdiagnose.test.xtra;

import javax.servlet.http.HttpServletRequest;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;

public class RequestInspectingTask extends DiagnosticTask {
    public HttpServletRequest request;

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        request = SelfDiagnoseServlet.getCurrentRequest();
        SelfDiagnoseServlet.getCurrentSession();
        SelfDiagnoseServlet.getWebApplicationName();
    }
}
