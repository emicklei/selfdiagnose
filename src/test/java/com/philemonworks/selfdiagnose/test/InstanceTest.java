package com.philemonworks.selfdiagnose.test;

import java.math.BigDecimal;

import com.philemonworks.selfdiagnose.Instance;


public class InstanceTest extends junit.framework.TestCase {

    public void testCreateOneElementList(){
        Instance list = Instance.create("java.util.ArrayList");
        list.invoke("add", "Hello World");
        assertEquals("Hello World", list.invoke("get", 0).stringValue());
    }
    
    public void testCreateMissingClass(){
        try {
            Instance object = Instance.create("bogus.Class");
            fail("should raise exception");
        } catch (Exception ex) {
            // ok
        }
    }
    
    public void testCreateMissingMethod(){
        try {
            Instance object = Instance.create("java.util.ArrayList");
            object.invoke("missing");
            fail("should raise exception");
        } catch (Exception ex) {
            // ok
        }
    }    
    
    public void testInstanceArg(){
        Instance i42 = new Instance(new BigDecimal(42));
        Instance i8 = new Instance(new BigDecimal(8));
        Instance i50 = i42.invoke("add",i8);
        BigDecimal big = (BigDecimal)i50.value();
        assertEquals(50, big.intValue());
    }      
    
    public void testCreateMissingMethodArg(){
        try {
            Instance object = Instance.create("java.util.ArrayList");
            object.invoke("missing","undefined");
            fail("should raise exception");
        } catch (Exception ex) {
            // ok
        }
    }         
    
    public void testInvokeToString(){
        try {
            Instance object = Instance.create("java.util.ArrayList");
            object.invoke("toString");
        } catch (Exception ex) {
            fail("raises exception");
        }
    }   
    public void testInvokeWithIntFails(){
        try {
            Instance object = Instance.create("com.philemonworks.selfdiagnose.test.xtra.Any");
            object.invoke("int", 42);
            fail("should raise exception");
        } catch (Exception ex) {
            // ok
        }
    }   
    public void testInvokeWithInt(){
        try {
            Instance object = Instance.create("com.philemonworks.selfdiagnose.test.xtra.Any");
            object.invoke("integer", 42);
        } catch (Exception ex) {
            fail("raises exception");
            ex.printStackTrace();
        }
    }      
    
    public void testInt(){
       assertEquals(42,new Instance(new Integer(42)).intValue());
    }
    public void testLong(){
        assertEquals(42L,new Instance(new Long(42L)).longValue());
     }   
    public void testDouble(){
        assertEquals((double)42.12345,0.1,new Instance(new Double(42.12345)).doubleValue());
     }    
    public void testSelf(){
        assertEquals(new Integer(42),new Instance(new Integer(42)).value());
     }
    public void testToString(){
        assertNotNull(new Instance(new Integer(42)).toString());
     }      
}
