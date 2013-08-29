/*
 Copyright 2008 Ernest Micklei @ PhilemonWorks.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */
package com.philemonworks.selfdiagnose;

import java.util.Arrays;

/**
 * Instance is a simple wrapper class to do reflection invocations.
 * Its primary (sole) use can be found in the vendor-dependent tasks.
 * Using reflection, vendor-dependent classes and their API can be used
 * without having JAR dependencies in this project.
 * 
 * @author ernest.micklei
 */
public class Instance {
    private final Object self;
    
    public Instance(Object any){
        this.self = any;
    }    
    public static Instance create(String className) {  
        return create(className, new Object[0]); 
    }
    public static Instance create(String className, Object[] args) {  
        try {
            final Class clazz = (Class)DiagnoseUtil.classForName(className);
            final Object instance = DiagnoseUtil.newInstance(clazz, args);
            return new Instance(instance);
        } catch (DiagnoseException ex) {
            throw new RuntimeException("failed to create instance of:" + className + " with arguments:" + Arrays.toString(args));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("failed to create instance of:" + className + " with arguments:" + Arrays.toString(args));
        }
        
    }        
    public Instance invoke(String methodName) {
        try {
            Object result = DiagnoseUtil.perform(this.self, methodName);
            return new Instance(result);
        } catch (Exception ex) {
            throw new RuntimeException("failed to invoke:" + methodName + " on:" + this.self);
        }
    }
    public Instance invoke(String methodName, Object arg) {
        Object val = arg instanceof Instance ? ((Instance)arg).value() : arg;
        try {
            Object result = DiagnoseUtil.perform(this.self, methodName, new Object[]{val});
            return new Instance(result);
        } catch (Exception ex) {
            throw new RuntimeException("failed to invoke:" + methodName + " on:" + this.self + " with argument:" + val, ex);
        }
    } 
    public Instance invoke(String methodName, int arg) {
        try {
            Object result = DiagnoseUtil.perform(this.self, methodName, new Object[]{new Integer(arg)}, new Class[]{int.class});
            return new Instance(result);
        } catch (Exception ex) {
            throw new RuntimeException("failed to invoke:" + methodName + " on:" + this.self + " with argument:" + arg, ex);
        }
    }   
    public String stringValue(){
        return (String)self;
    }
    public int intValue(){
        return ((Integer)self).intValue();
    }
    public Object value(){
        return self;
    }
    public String toString(){
        return "Instance{" + self.toString() + "}";
    }
    public long longValue() {
        return ((Long)self).longValue();
    }
    public double doubleValue() {
        return ((Double)self).doubleValue();
    }

}
