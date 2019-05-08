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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.Locale;

/**
 * DiagnosticTask is the abstract class for all tasks that can be registered with SelfDiagnose. Typically tasks are
 * parameterized in order to run. Tasks may want to re-implement the setUp method in which parameter verification should
 * be done.
 *
 * @author emicklei
 */
public abstract class DiagnosticTask implements Serializable {
    private static final long serialVersionUID = -1320140869670492598L;
    private static final Logger LOGGER = Logger.getLogger(SelfDiagnose.class);

    /**
     * The attribute that names the variable where results are stored.
     */
    public static final String PARAMETER_VARIABLE = "var";
    /**
     * Attribute that specifies the task comment; this is rendered as label for the task results.
     */
    public static final String PARAMETER_COMMENT = "comment";
    /**
     * Attribute that specifies whether to include the task results in the report. Defaults to {@code true}, meaning the task results will be included if known.
     */
    public static final String PARAMETER_REPORT = "report";
    /**
     * Attribute that specifies whether to limit the execution time of the task to timeout milliseconds.
     */
    public static final String PARAMETER_TIMEOUT = "timeout";
    /**
     * Attribute that specifies the task severity.
     */
    public static final String PARAMETER_SEVERITY = "severity";

    /**
     * The identifier for the object that created and registered this task.
     */
    private String requestor = "{unknown}";
    /**
     * Name of the reference that refers to the value of the property.
     */
    private String variableName;
    /**
     * Used to explain the task in both the log and the configuration.
     */
    private String comment;
    /**
     * Indication for whether the receiver will report the result.
     */
    private boolean reportResults = true;
    /**
     * If timeout (milliseconds) is set (>0) then this run is aborted when it takes more time to complete.
     * The actual time to complete (again in milliseconds) is reported
     */
    private int timeoutInMilliseconds = 0;
    /**
     * Used to indicate whether a task has critical or warning severity when a check fails.
     * By default, severity is set to critical.
     */
    private Severity severity = Severity.CRITICAL;
    /**
     * Used to detect if the severity has been explicitly specified.
     */
    private boolean defaultSeverity = true;

    /**
     * Return an object to store the results of running the receiver.
     *
     * @return DiagnosticTaskResult
     */
    public DiagnosticTaskResult createResult() {
        return new DiagnosticTaskResult(this);
    }

    /**
     * Returns a description for the task.
     *
     * @return String the description
     */
    public abstract String getDescription();

    /**
     * On default, the task is the unqualified name for the class in lower case.
     * This name must be equal to that of the complexType as defined in the XSD.
     *
     * @return String the name for this task
     */
    public String getTaskName() {

        Class<?> theClass = this.getClass();
        String taskName = theClass.getName();
        return taskName.substring(taskName.lastIndexOf('.') + 1).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Return the identifier for the object that created and will register this task.
     *
     * @return String
     */
    public String getRequestor() {
        return requestor;
    }

    /**
     * Override this method to verify that task parameters are initialized/set correctly.
     *
     * @param ctx ExecutionContext
     * @throws DiagnoseException
     */
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
    }

    /**
     * Run the task and answer the result.
     *
     * @return DiagnosticTaskResult
     */
    public DiagnosticTaskResult run() {
        return this.run(new ExecutionContext());
    }

    /**
     * Run the task and answer the result.
     *
     * @param ctx : ExecutionContext
     * @return DiagnosticTaskResult
     */
    public DiagnosticTaskResult run(ExecutionContext ctx) {
        DiagnosticTaskResult result = this.createResult();
        long begin = System.currentTimeMillis();
        long end = begin;
        try {
            this.setUp(ctx);
            this.run(ctx, result);
            end = System.currentTimeMillis();
        } catch (DiagnoseException e) {
            result.setFailedMessage(e.getMessage());
        } catch (Exception ex) {
            String why = ex.getMessage();
            if (ex.getCause() != null) {
                why = ex.getCause().getMessage();
            }
            if (why == null) {
                why = ex.toString();
            }
            String errorMessage = "Unexpected error occurred while running "+this.getClass().getName()+" because: " + why;
            result.setErrorMessage(errorMessage);
            LOGGER.error(errorMessage, ex);
        }
        result.setExecutionTime(end - begin);
        return result;
    }

    /**
     * Run the task. If an error is detected then raise a DiagnoseException.
     * Otherwise use the result object to the report any messages when a run is completed.
     *
     * @param ctx    ExecutionContext
     * @param result DiagnosticTaskResult
     * @throws DiagnoseException
     */
    public abstract void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException;

    /**
     * Set the identifier for the object that created and registered this task.
     *
     * @param identifier the new identifier
     */
    public void setRequestor(String identifier) {
        requestor = identifier;
    }

    /**
     * This method is sent from the SelfDiagnoseHandler when a configuration is being processed. Use the passed
     * attributes to initialize the receiver.
     * If a variable parameter is passed then store it.
     *
     * @param attributes org.xml.sax.Attributes
     */
    public void initializeFromAttributes(Attributes attributes) {
        this.setComment(attributes.getValue(PARAMETER_COMMENT));
        this.setVariableName(attributes.getValue(PARAMETER_VARIABLE));
        this.setReportResults(!"false".equals(attributes.getValue(PARAMETER_REPORT)));

        String severity = attributes.getValue(PARAMETER_SEVERITY);
        if (severity != null && !severity.isEmpty()) {
            try {
                this.setSeverity(Severity.valueOf(severity.toUpperCase()));
            } catch (Exception ex) {
                LOGGER.error("invalid severity attribute value: " + severity, ex);
            }
        }

        // read timeout (ms) if available
        String timeoutOrNull = attributes.getValue(PARAMETER_TIMEOUT);
        if (timeoutOrNull != null && !timeoutOrNull.isEmpty()) {
            try {
                this.setTimeoutInMilliSeconds(Integer.parseInt(timeoutOrNull));
            } catch (NumberFormatException ex) {
                LOGGER.error("invalid timeout attribute value: " + timeoutOrNull, ex);
            }
        }
    }

    /**
     * User-defined Diagnostic Task subclasses should redefine this method to provide reporttemplates for both a successful and failed run.
     * This is only needed if the task uses the method DiagnoseUtil.report(DiagnosticTaskResult result, boolean isSucces, ...).
     *
     * @param isSuccess result of running the task
     * @return String containing one or more template arguments such as {0}
     */
    public String getDefaultReportTemplate(boolean isSuccess) {
        return this.getClass().getName() + (isSuccess ? " passed " : " failed ") + "[missing getDefaultReportTemplate()]";
    }

    /**
     * Get the name of the variable where to store results.
     *
     * @return String the variable name
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Set the name of the variable where to store results.
     *
     * @param varName the new variable name
     */
    public void setVariableName(String varName) {
        variableName = varName;
    }

    public boolean hasComment() {
        return comment != null && comment.length() > 0;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isReportResults() {
        return reportResults;
    }

    public void setReportResults(boolean reportResults) {
        this.reportResults = reportResults;
    }

    public int getTimeoutInMilliSeconds() {
        return timeoutInMilliseconds;
    }

    public void setTimeoutInMilliSeconds(int timeoutInMS) {
        this.timeoutInMilliseconds = timeoutInMS;
    }

    public boolean needsLimitedRuntime() {
        return this.timeoutInMilliseconds > 0;
    }

    public Severity getSeverity() {
        return severity;
    }

    public boolean isDefaultSeverity() {
        return defaultSeverity;
    }

    /**
     * Make sure that severity is set correctly within the allowed values, see {@link Severity} enumeration.
     * @param severity
     */
    public void setSeverity(Severity severity) {
        if(severity == null) {
            throw new NullPointerException("severity == NULL");
        }
        this.severity = severity;
        this.defaultSeverity = false;
    }

}
