/*
 Copyright 2006 Ernest Micklei @ PhilemonWorks.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */
package com.philemonworks.selfdiagnose;

import java.io.StringWriter;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.log4j.Logger;

/**
 * DiagnosticTaskResult hold information about the execution of a DiagnosticTask
 * and known how to create a report for logging.
 * <ul>
 * <li>Passed means: task completed succesfully</li>
 * <li>Failed means: execution task succes but tests failed</li>
 * <li>Error means: execution task failed (because of Exception)</li>
 * <li>Unknown means: result is not set and therefore not reported</li>
 * </ul>
 *
 * @author emicklei
 */
public class DiagnosticTaskResult {
    public static final String STATUS_PASSED = "passed";

    public static final String STATUS_FAILED = "failed";

    public static final String STATUS_ERROR = "error";

    public static final String STATUS_UNKNOWN = "unknown";

    private final DiagnosticTask task;

    @Expose
    @SerializedName("task")
    private final String taskName;

    @Expose
    private final String requestor;

    @Expose
    private String severity;

    @Expose
    private String status = STATUS_UNKNOWN;

    @Expose
    private String message = "";

    @Expose
    private String comment = null; // overrides task comment

    /**
     * Number of milliseconds to run this task.
     */
    @Expose
    @SerializedName("duration")
    private long executionTime = 0;

    /**
     * Constructor requires a DiagnosticTask to store the result of its run.
     *
     * @param task
     *            DiagnosticTask
     */
    public DiagnosticTaskResult(DiagnosticTask task) {
        super();

        if(task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }

        this.task = task;
        this.taskName =  task.getTaskName();
        this.requestor = task.getRequestor();
        this.severity = task.getSeverity();
    }

    /**
     * Indicates the outcome of the task
     *
     * @return STATUS_PASSED || STATUS_FAILED || STATUS_ERROR
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return boolean true if the run has failed
     */
    public boolean isError() {
        return status.equals(STATUS_ERROR);
    }

    /**
     * @return boolean true if the run has failed
     */
    public boolean isFailed() {
        return status.equals(STATUS_FAILED);
    }

    /**
     * @return boolean true if the run has passed
     */
    public boolean isPassed() {
        return status.equals(STATUS_PASSED);
    }

    /**
     * @return boolean true if the run has an unkown result
     */
    public boolean isUnknown() {
        return status.equals(STATUS_UNKNOWN);
    }

    public boolean wantsToBeReported() {
        return task.isReportResults() && (!this.isUnknown());
    }

    /**
     * Set the message explaining why the run has failed.
     *
     * @param aMessage
     */
    public void setErrorMessage(String aMessage) {
        status = STATUS_ERROR;
        message = aMessage;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Write a log entry using the logger associated with SelfDiagnose. If the
     * result was an error then write a verbose report.
     */
    public void logReport() {
        Logger log = Logger.getLogger(SelfDiagnose.class);
        StringWriter w = new StringWriter();
        this.writeMessagesOn(w);
        if (!isPassed()) {
            w.write(" - ");
            w.write(DiagnoseUtil.shortName(task.getClass()));
            w.write(" - ");
            w.write(task.getDescription());
        }
        if (task.hasComment()) {
            w.write(" // ");
            w.write(task.getComment());
        }
        if (isPassed()) {
            log.info(w.toString());
        } else {
            log.error(w.toString());
        }
    }

    protected void writeMessagesOn(StringWriter writer) {
        writer.write(message);
    }

    /**
     * Return the DiagnosticTask for which the result hold the results.
     *
     * @return DiagnosticTask
     */
    public DiagnosticTask getTask() {
        return task;
    }

    /**
     * Add a message to the list of reporting messages
     *
     * @param passedMessage
     *            String
     */
    public void setPassedMessage(String passedMessage) {
        status = STATUS_PASSED;
        message = passedMessage;
    }

    public void setFailedMessage(String failedMessage) {
        status = STATUS_FAILED;
        message = failedMessage;
    }

    /**
     * @return number of milliseconds
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Time in milliseconds
     * @param executionTime
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Dispatch method exists because of CompositeDiagnosticTaskResult.
     * @param results
     */
    public void addToResults(List<DiagnosticTaskResult> results) {
        results.add(this);
    }

    public String toString() {
        return super.toString() + "{status=" + status + ",message=" + message + "}";
    }

    public boolean hasComment() {
        return comment != null && !comment.equals("");
    }

    public String getComment() {
        return hasComment() ? comment : task.getComment();
    }

    public void setComment(String newComment) {
        comment = newComment;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity.name();
    }
}
