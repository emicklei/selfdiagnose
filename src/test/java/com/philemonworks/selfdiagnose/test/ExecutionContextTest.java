package com.philemonworks.selfdiagnose.test;

import java.util.Date;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.ExecutionContext;

public class ExecutionContextTest extends TestCase {
    ExecutionContext ctx;
    
    public void setUp(){
         ctx = new ExecutionContext();
    }
    
    public void testOgnl() throws DiagnoseException{
        ctx.setValue("root", new Date());
        assertNotNull(ctx.getValue("root.toString()"));
    }
    
    public void testArrayNavigation() throws DiagnoseException {
        String[] array = new String[]{"help","me"};
        ctx.setValue("array", array);
        Object me = ctx.getValue("array[1]");
        assertEquals("me",me);
    }
    
    public void testTwoVars() throws DiagnoseException {
        ctx.setValue("one", new Integer(1));
        ctx.setValue("two", new Integer(2));
        Object three = ctx.getValue("one+two");
        assertEquals(new Integer(3),three);
    }    
}
