package com.philemonworks.selfdiagnose.report;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckProperty;

/**
 * ReportEnvironmentProperty is a task that reports an environment property
 * Usage:
 * <pre>
 * &lt;reportenvironmentproperty comment="Hostname" property="HOSTNAME" /&gt;
 * </pre>
 */
public class ReportEnvironmentProperty extends CheckProperty {

    //private static final long serialVersionUID = 1502360353931029303L;

    public String getDescription() {
        return "Reports an environment property";
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {

        String propertyValue = System.getenv(property);
        if (propertyValue == null) {
            result.setFailedMessage(DiagnoseUtil.format("Environment variable {0} not set", property));
        } else {
            result.setPassedMessage(propertyValue);
        }
    }
}
