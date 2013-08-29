package com.philemonworks.selfdiagnose.test;

import java.util.Properties;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.report.ReportProperties;

public class ReportPropertiesTest extends TestCase {
    ExecutionContext ctx;
    
    public void setUp(){
         ctx = new ExecutionContext();
    }
	public void testPropertiesFromContext() throws Exception {
		Properties props = new Properties();
		props.setProperty("key", "value");
		ctx.setValue("report", props);
		
		ReportProperties check = new ReportProperties();
		check.setValue("${report}");
        DiagnosticTaskResult result = check.createResult();
        check.run(ctx,result);

        assertTrue(result.isUnknown());		
        System.out.println(result);
	}
}
