package com.philemonworks.selfdiagnose.test;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckBeanProperty;

public class CheckBeanPropertyTest extends TestCase {

    public static final String stopt() {
        return "HELLO";
    }

    public void testArrayListSize() throws DiagnoseException {
        CheckBeanProperty task = new CheckBeanProperty();
        task.setProperty("property");
        task.setBean("${bean}");
        task.setVariableName("var");

        ExecutionContext ctx = new ExecutionContext();
        ctx.setValue("bean", task);
        assertFalse(task.run(ctx).isError());
        assertEquals(ctx.getValue("var"), "property");

    }

    public void testStringProperty() throws DiagnoseException {
        CheckBeanProperty task = new CheckBeanProperty();
        task.setMethod("toUpperCase");
        task.setBean("Hello");
        task.setVariableName("var");

        ExecutionContext ctx = new ExecutionContext();
        assertFalse(task.run(ctx).isError());
        assertEquals(ctx.getValue("var"), "HELLO");

    }

    public void testCallStaticMethodProperty() throws DiagnoseException {
        CheckBeanProperty task = new CheckBeanProperty();
        task.setBean("${@com.philemonworks.selfdiagnose.test.CheckBeanPropertyTest@stopt()}");
        task.setVariableName("var");

        ExecutionContext ctx = new ExecutionContext();
        assertFalse(task.run(ctx).isError());
        assertEquals(ctx.getValue("var"), "HELLO");

    }
}
