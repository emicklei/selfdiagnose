package com.philemonworks.selfdiagnose.test;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.SelfDiagnose;

public class SelfDiagnoseTest extends TestCase {

    public void setUp() {
        SelfDiagnose.flush();
    }

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
        assertEquals("parallel execution enabled", false, SelfDiagnose.isParallelExecution());
    }

    public void testConfigureURLReal() throws MalformedURLException {
        SelfDiagnose.configure(new URL("file:///src/test/resources/selfdiagnose-test.xml"));
        assertEquals("tasks from test.xml", 0, SelfDiagnose.getTasks().size());
    }

    public void testConfigureParallelReal() {
        SelfDiagnose.configure("selfdiagnose-test-parallel.xml");
        assertEquals("tasks from test.xml", 16, SelfDiagnose.getTasks().size());
        assertEquals("parallel execution enabled", true, SelfDiagnose.isParallelExecution());
    }

    public void testConfigureParallelWrongReal() {
        SelfDiagnose.configure("selfdiagnose-test-parallel-wrong.xml");
        assertEquals("tasks from test.xml", 18, SelfDiagnose.getTasks().size());
        assertEquals("parallel execution enabled", false, SelfDiagnose.isParallelExecution());
    }
}
