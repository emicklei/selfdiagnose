package com.philemonworks.selfdiagnose.test;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.CollectionIteratorTask;
import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckValueMatches;

public class CollectionIteratorTaskTest extends TestCase {
    ExecutionContext ctx;
    
    public void setUp(){
         ctx = new ExecutionContext();
    }
    
    public void testIterator() throws DiagnoseException{
        CollectionIteratorTask iterator = new CollectionIteratorTask();
        iterator.setExpression("${collection}");
        iterator.setVariableName("each");
        
        CheckValueMatches matcher = new CheckValueMatches();
        matcher.setValue("${each}");
        matcher.setPattern(".*");
        iterator.register(matcher);
        
        ctx.setValue("collection", new String[]{"one","two","three"});
        DiagnosticTaskResult result = iterator.run(ctx);
        assertTrue(result.isPassed());        
    }
}
