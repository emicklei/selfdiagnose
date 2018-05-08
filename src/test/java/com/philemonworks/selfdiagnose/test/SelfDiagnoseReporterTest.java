/*
 * Copyright 2006 Ernest Micklei @ PhilemonWorks.com Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed
 * to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the
 * License.
 */
package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.output.JSONReporter;
import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.Check;
import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;
import com.philemonworks.selfdiagnose.check.CheckPropertyValuesAreJavaTypes;
import com.philemonworks.selfdiagnose.output.HTMLReporter;
import com.philemonworks.selfdiagnose.output.LoggingReporter;
import com.philemonworks.selfdiagnose.output.XMLReporter;
import com.philemonworks.selfdiagnose.test.xtra.MockHttpRequest;


public class SelfDiagnoseReporterTest extends TestCase {
    public void setUp() {
        Check.classLoadable("com.philemonworks.selfdiagnose.test.SelfDiagnoseReporterTest");
        Check.classLoadable("com.philemonworks.selfdiagnose.test.MissingClass");
        CheckPropertyValuesAreJavaTypes task = new CheckPropertyValuesAreJavaTypes();
        task.setResourceName("CheckPropertyValuesAreJavaTypes.properties");
        task.setComment("comment from SelfDiagnoseReporterTest");
        SelfDiagnose.register(task);
    }


    public void tearDown() {
        SelfDiagnose.flush();
    }      

    public void testHTML() {
        HTMLReporter reporter = new HTMLReporter();
        assertNotNull(reporter.getContentType());
        SelfDiagnose.runTasks(reporter);
        System.out.println("---------REPORT FROM HTML TEST ----------");
        System.out.println(reporter.getContent());
    }
    public void testLOG() {
        LoggingReporter reporter = new LoggingReporter();
        SelfDiagnose.runTasks(reporter);
    }
    public void testXML() {
        XMLReporter reporter = new XMLReporter();
        assertNotNull(reporter.getContentType());
        SelfDiagnose.runTasks(reporter);
        System.out.println("---------REPORT FROM XML TEST ----------");
        System.out.println(reporter.getContent());
    }
    public void testJSON() {
        JSONReporter reporter = new JSONReporter();
        assertNotNull(reporter.getContentType());
        SelfDiagnose.runTasks(reporter);
        System.out.println("---------REPORT FROM JSON TEST ----------");
        System.out.println(reporter.getContent());
    }
    public void testXMLWithXSL() {
        XMLReporter reporter = new XMLReporter();
        assertNotNull(reporter.getContentType());
        MockHttpRequest request = new MockHttpRequest();
        request.parameters.put("xsl", "myxsl");
        SelfDiagnoseServlet.setCurrentRequest(request);
        
        SelfDiagnose.runTasks(reporter);
        System.out.println("---------REPORT FROM XML TEST ----------");
        System.out.println(reporter.getContent());
    }

    public void testHTMLParallel() {
        HTMLReporter reporter = new HTMLReporter();
        assertNotNull(reporter.getContentType());
        ExecutionContext ctx = new ExecutionContext();
        ctx.setValue("parallel", true);
        SelfDiagnose.runTasks(reporter, ctx);
        System.out.println("---------REPORT FROM HTML PARALLEL TEST ----------");
        System.out.println(reporter.getContent());
    }

    public void testHTMLParallel2() {
        HTMLReporter reporter = new HTMLReporter();
        assertNotNull(reporter.getContentType());
        ExecutionContext ctx = new ExecutionContext();
        SelfDiagnose.setParallelExecution(true);
        SelfDiagnose.runTasks(reporter, ctx);
        System.out.println("---------REPORT FROM HTML PARALLEL TEST ----------");
        System.out.println(reporter.getContent());
    }
}
