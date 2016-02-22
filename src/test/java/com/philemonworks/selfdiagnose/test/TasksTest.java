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
package com.philemonworks.selfdiagnose.test;

import java.util.HashMap;
import java.util.Map;

import com.philemonworks.selfdiagnose.*;
import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.check.CheckValidURL;
import com.philemonworks.selfdiagnose.output.DiagnoseRun;
import com.philemonworks.selfdiagnose.output.XMLReporter;

public class TasksTest extends TestCase {
    public void testValidURL() {
        CheckValidURL task = new CheckValidURL();
        task.setUrl("http://www.philemonworks.com");
        DiagnosticTaskResult result = task.run();
        if (result.isFailed())
            fail("correct url failed");
    }

    public void testInvalidURL() {
        CheckValidURL task = new CheckValidURL();
        task.setUrl("http://www philemonworks.com");
        DiagnosticTaskResult result = task.run();
        if (!result.isFailed())
            fail("[expected exception]");
    }

    public void testClassImplementsInterface() {
        assertTrue("jndi", Map.class.isAssignableFrom(HashMap.class));
    }

    public void testTimeoutTask() {
        SleepTask sleep = new SleepTask();
        sleep.setTimeoutInMilliSeconds(1000);

        DiagnoseRun run = runSingleTask(sleep);

        assertTrue(!run.isOK());
        assertTrue(run.timeMs < 1500);
    }

    public void testTimeoutCustomTask() {
        SleepTask sleep = new SleepTask();
        CustomDiagnosticTask ct = new CustomDiagnosticTask();
        // set task before attributes
        ct.setTask(sleep);
        ct.setTimeoutInMilliSeconds(1000);

        DiagnoseRun run = runSingleTask(sleep);

        assertTrue(!run.isOK());
        assertTrue(run.timeMs < 1500);
    }

    public void testDefaultTaskSeverity() {
        testCustomTaskSeverity(null, Severity.CRITICAL);
    }

    public void testCustomTaskSeverity() {
        testCustomTaskSeverity(Severity.UNKNOWN);
        testCustomTaskSeverity(Severity.OK);
        testCustomTaskSeverity(Severity.WARNING);
        testCustomTaskSeverity(Severity.CRITICAL);
    }

    private void testCustomTaskSeverity(Severity severity) {
        testCustomTaskSeverity(severity, severity);
    }

    private void testCustomTaskSeverity(Severity givenSeverity, Severity expectedSeverity) {
        CustomSeverityTask task = new CustomSeverityTask(givenSeverity);

        DiagnoseRun runResult = runSingleTask(task);
        DiagnosticTaskResult taskResult = runResult.results.get(0);

        assertEquals(taskResult.getSeverity(), expectedSeverity.name());
    }

    private DiagnoseRun runSingleTask(final DiagnosticTask task) {
        SelfDiagnose.flush();
        SelfDiagnose.register(task);
        return SelfDiagnose.runTasks(new XMLReporter());
    }

    static class SleepTask extends DiagnosticTask {
        private static final long serialVersionUID = -5193920249800557424L;

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
            try {
                System.out.println("[SleepTask] going to sleep for 2 seconds");
                Thread.sleep(1000 * 2);
                System.out.println("[SleepTask] awake after 2 seconds");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CustomSeverityTask extends DiagnosticTask {
        private static final long serialVersionUID = 1L;

        private final Severity customSeverity;

        public CustomSeverityTask(final Severity customSeverity) {
            this.customSeverity = customSeverity;
        }

        @Override
        public String getDescription() {
            return "Custom severity: " + customSeverity;
        }

        @Override
        public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
            if(customSeverity != null) {
                result.setSeverity(customSeverity);
            }
            result.setPassedMessage("GOOD");
        }
    }
}
