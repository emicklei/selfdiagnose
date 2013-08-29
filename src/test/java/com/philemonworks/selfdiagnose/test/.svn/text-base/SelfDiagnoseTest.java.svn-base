package com.philemonworks.selfdiagnose.test;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.SelfDiagnose;

public class SelfDiagnoseTest extends TestCase {
    public void testConfigure(){
        SelfDiagnose.configure((URL)null);
    }
    public void testReload(){
        SelfDiagnose.reloadConfiguration();
    }
    public void testRun(){
        SelfDiagnose.runTasks();
    }
    public void testConfigureReal(){
        SelfDiagnose.configure("selfdiagnose-test.xml");
        assertEquals("tasks from test.xml",21,SelfDiagnose.getTasks().size());
    }
    public void testConfigureURLReal() throws MalformedURLException{
        SelfDiagnose.configure(new URL("file:///src/test/resources/selfdiagnose-test.xml"));
        assertEquals("tasks from test.xml",0,SelfDiagnose.getTasks().size());
    }    
}
