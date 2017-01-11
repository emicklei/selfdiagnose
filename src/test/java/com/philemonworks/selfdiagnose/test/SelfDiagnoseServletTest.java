package com.philemonworks.selfdiagnose.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;
import com.philemonworks.selfdiagnose.output.HTMLReporter;
import com.philemonworks.selfdiagnose.output.XMLReporter;
import com.philemonworks.selfdiagnose.test.xtra.MockHttpRequest;
import com.philemonworks.selfdiagnose.test.xtra.MockHttpResponse;
import com.philemonworks.selfdiagnose.test.xtra.RequestInspectingTask;

public class SelfDiagnoseServletTest extends TestCase {
    MockHttpRequest request;
    HttpServletResponse response;
    RequestInspectingTask requestInspectingTask;
    SelfDiagnoseServlet s;

    public void setUp() {
        request = new MockHttpRequest();
        response = new MockHttpResponse();
        requestInspectingTask = new RequestInspectingTask();
        SelfDiagnose.register(requestInspectingTask);
        s = new SelfDiagnoseServlet();
    }

    public void testGetReportXml() {
        assertEquals(XMLReporter.class, s.getReporter("xml").getClass());
    }

    public void testGetReportHtml() {
        assertEquals(HTMLReporter.class, s.getReporter("html").getClass());
    }

    public void testGetReportMissing() {
        try {
            s.getReporter("missing");
            fail("should raise exception");
        } catch (Exception ex) {
        }
    }

    public void testRun() {
        try {
            s.service(request, response);
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertEquals(requestInspectingTask.request, request);
    }
    
    public void testReload() {
        request.parameters.put("reload","true");
        try {
            s.service(request, response);
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
