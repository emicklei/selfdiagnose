package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.ExecutionContext;
import org.junit.Test;
import org.junit.experimental.categories.Categories;

/**
 * Created by emicklei on 18/04/16.
 */
public class SecurityTest {
    @Test
    public void testThatRuntimeClassIsNotAccessible() {
        ExecutionContext ctx = new ExecutionContext();
        try {
            System.out.println(ctx.getValue("@java.lang.Runtime@getRuntime().exec('/bin/sh ls')"));
            throw new RuntimeException("DiagnoseException expected");
        } catch (Exception ex) {
            // OK
        }

    }
}
