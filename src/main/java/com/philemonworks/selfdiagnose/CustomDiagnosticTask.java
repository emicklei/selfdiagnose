/*
    Copyright 2006-2015 Ernest Micklei @ PhilemonWorks.com

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

/**
 * CustomDiagnosticTask is a wrapper on a custom defined DiagnosticTask.
 * If the custom task could be created and initialized at configuration time then all messages are delegated to the task.
 * If the custom task could not be created then this class provides the error message with the reason every it is asked to run.
 * 
 * @author Ernest Micklei
 */
public class CustomDiagnosticTask extends DiagnosticTask {
    private static final long serialVersionUID = 3770101714847787671L;

    String reference; // an identifier to a task in a DI context
    DiagnosticTask task;
    String errorMessage;

    public String getDescription() {
        if (task == null) // failed to create custom task
            return "No description available because task instance could not be created";
        else
            return task.getDescription();
    }

    public DiagnosticTaskResult createResult() {
        if (task == null) // failed to create custom task
            return super.createResult();
        else
            return task.createResult();
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        if (task == null) { // failed to create custom task
            result.setErrorMessage(errorMessage);
            return;
        }
        task.run(ctx, result);
    }

    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        if (task == null) // failed to create custom task
            throw new DiagnoseException(this.errorMessage);
        task.setUp(ctx);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DiagnosticTask getTask() {
        return task;
    }

    public void setTask(DiagnosticTask task) {
        this.task = task;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public void setVariableName(String varName) {
        super.setVariableName(varName);
        if (this.task != null) {
            this.task.setVariableName(varName);
        }
    }

    @Override
    public void setTimeoutInMilliSeconds(int timeoutInMS) {
        super.setTimeoutInMilliSeconds(timeoutInMS);
        if (this.task != null) {
            this.task.setTimeoutInMilliSeconds(timeoutInMS);
        }
    }

    @Override
    public void setComment(String comment) {
        super.setComment(comment);
        if (this.task != null) {
            this.task.setComment(comment);
        }
    }
}
