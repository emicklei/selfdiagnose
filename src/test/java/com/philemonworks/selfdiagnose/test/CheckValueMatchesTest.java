package com.philemonworks.selfdiagnose.test;

import java.util.Date;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckValueMatches;

public class CheckValueMatchesTest extends TestCase {
    ExecutionContext ctx;
    
    public void setUp(){
         ctx = new ExecutionContext();
    }
    
    public void testOgnl() throws DiagnoseException{
        ctx.setValue("root", new Date());
        CheckValueMatches check = new CheckValueMatches();
        check.setVariableName("var");
        check.setValue("${root}");
        check.setPattern(".*");
        DiagnosticTaskResult result = new DiagnosticTaskResult(check);
        check.run(ctx,result);
        assertTrue(result.isPassed());
        assertSame(ctx.getValue("root"),ctx.getValue("var"));
    }
    
    public void testKillYourSelf() {
        CheckValueMatches check = new CheckValueMatches();
        /**
        check.setValue("${@java.lang.System@exit(0)}");
        assertTrue(check.run().isError());
        **/
    }
    
    public void testThrowException() {
        CheckValueMatches check = new CheckValueMatches();
        check.setValue("${1/0}");
        assertTrue(check.run().isError());        
    }    
}