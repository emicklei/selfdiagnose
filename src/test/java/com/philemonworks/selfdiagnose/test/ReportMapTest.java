package com.philemonworks.selfdiagnose.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.report.ReportMap;

public class ReportMapTest extends TestCase {
    ExecutionContext ctx;
    
    public void setUp(){
         ctx = new ExecutionContext();
    }
	public void testMapFromContext() throws Exception {
		Map map = new HashMap();
		map.put("key", "value");
		map.put("int", new Integer(10));
		ctx.setValue("report", map);
		
		ReportMap check = new ReportMap();
		check.setValue("${report}");
        DiagnosticTaskResult result = check.createResult();
        check.run(ctx,result);

        assertTrue(result.isUnknown());		
        System.out.println(result);
	}
}
