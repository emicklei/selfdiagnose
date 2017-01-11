package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.*;
import com.philemonworks.selfdiagnose.output.DiagnoseRunReporter;
import com.philemonworks.selfdiagnose.output.HTMLReporter;
import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.net.URL;

public class SelfDiagnoseTest extends TestCase {
    public void testConfigure() {
        SelfDiagnose.configure((URL) null);
    }

    public void testReload() {
        SelfDiagnose.reloadConfiguration();
    }

    public void testRun() {
        SelfDiagnose.runTasks();
    }

    public void testConfigureReal() {
        SelfDiagnose.configure("selfdiagnose-test.xml");
        assertEquals("tasks from test.xml", 21, SelfDiagnose.getTasks().size());
    }

    public void testConfigureURLReal() throws MalformedURLException {
        SelfDiagnose.configure(new URL("file:///src/test/resources/selfdiagnose-test.xml"));
        assertEquals("tasks from test.xml", 0, SelfDiagnose.getTasks().size());
    }

    public void testWithTimeout_NoTimeout() {
        SelfDiagnose.configure("selfdiagnose-ok.xml");
        DiagnoseRunReporter reporter = new HTMLReporter();
        ExecutionContext ctx = new ExecutionContext();
        SelfDiagnose.runTasks(reporter, ctx, 100);
        assertTrue(reporter.getContent().contains("Failures: 0"));
    }

    public void testWithTimeout_Timeout() {
        SelfDiagnose.configure("selfdiagnose-ok.xml");
        SelfDiagnose.getTasks().add(new DiagnosticTask() {
            @Override
            public String getDescription() {
                return "takes a long time";
            }

            @Override
            public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    fail("Should not be interrupted");
                }
            }
        });
        DiagnoseRunReporter reporter = new HTMLReporter();
        ExecutionContext ctx = new ExecutionContext();
        SelfDiagnose.runTasks(reporter, ctx, 1);
        assertTrue(reporter.getContent().contains("Failures: 1"));
        assertTrue(reporter.getContent().contains("Could not execute all SelfDiagnose tasks within specified limit of 1 ms."));
    }

}
