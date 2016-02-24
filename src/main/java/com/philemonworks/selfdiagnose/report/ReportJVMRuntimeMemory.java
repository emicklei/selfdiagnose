package com.philemonworks.selfdiagnose.report;

import com.philemonworks.selfdiagnose.*;

/**
 * ReportJVMRuntimeMemory is a task that reports the current JVM memory consumption
 * Usage:
 * <pre>
 * &lt;reportjvmruntimememory comment="Java VM Memory" /&gt;
 * </pre>
 * @author ernestmicklei
 */
public class ReportJVMRuntimeMemory extends DiagnosticTask {

    private static final long serialVersionUID = 1502360353931029303L;

    public ReportJVMRuntimeMemory() {
        setSeverity(Severity.NONE);
    }

    public String getDescription() {
        return "Reports on the current JVM memory consumption";
    }
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        StringBuilder sb = new StringBuilder();
        sb.append("free memory: " + freeMemory / 1024);
        sb.append(" Kb, allocated memory: " + allocatedMemory / 1024);
        sb.append(" Kb, max memory: " + maxMemory /1024);
        sb.append(" Kb, total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
        sb.append(" Kb");
        result.setPassedMessage(sb.toString());
    }
}
