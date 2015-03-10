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

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.CustomDiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.SelfDiagnose;
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
        SelfDiagnose.flush();
        SelfDiagnose.register(sleep);
        XMLReporter reporter = new XMLReporter();
        DiagnoseRun run = SelfDiagnose.runTasks(reporter);
        assertTrue(!run.isOK());
        assertTrue(run.timeMs < 2000); // safe check?
    }

    public void testTimeoutCustomTask() {
        SleepTask sleep = new SleepTask();
        CustomDiagnosticTask ct = new CustomDiagnosticTask();
        // set task before attributes
        ct.setTask(sleep);
        ct.setTimeoutInMilliSeconds(1000);
        SelfDiagnose.flush();
        SelfDiagnose.register(sleep);
        XMLReporter reporter = new XMLReporter();
        DiagnoseRun run = SelfDiagnose.runTasks(reporter);
        assertTrue(!run.isOK());
        if (run.timeMs >= 2000) {
            System.out.println("got " + run.timeMs + " want < 2000");
            fail();
        }
    }

    static class SleepTask extends DiagnosticTask {
        private static final long serialVersionUID = -5193920249800557424L;

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
            try {
                System.out.println("[SleepTask] going to sleep for 2 seconds");
                Thread.sleep(1000 * 2);
                System.out.println("[SleepTask] awake after 2 seconds");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
