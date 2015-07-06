/*
    Copyright 2006 Ernest Micklei @ PhilemonWorks.com

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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Provides a set of static utility methods that are used in the setting up and
 * running diagnostic tasks.
 * 
 * @author Ernest Micklei
 */
public class DiagnoseUtil {

    /**
     * Test that the value is a non empty String. Throws an exception otherwise.
     * 
     * @param variableName
     *            field name for the Task
     * @param value
     *            of the variable
     * @param owner
     *            Task class
     * @throws DiagnoseException
     */
    public static void verifyNonEmptyString(String variableName, Object value, Class<?> owner) throws DiagnoseException {
        DiagnoseUtil.verifyNotNull(variableName, value, owner);
        if (!(value instanceof String))
            throw new DiagnoseException(owner.getName() + " parameter [" + variableName + "] must be a String.");
        String stringValue = (String) value;
        if (stringValue.length() == 0)
            throw new DiagnoseException(owner.getName() + " parameter [" + variableName + "] cannot be empty.");
    }

    /**
     * Test that the value is not null. Throws an exception otherwise.
     * 
     * @param variableName
     *            field name for the Task
     * @param value
     *            of the variable
     * @param owner
     *            Task class
     * @throws DiagnoseException
     */
    public static void verifyNotNull(String variableName, Object value, Class<?> owner) throws DiagnoseException {
        if (value == null)
            throw new DiagnoseException(owner.getName() + " parameter [" + variableName + "] cannot be null.");
    }

    /**
     * Return the unqualified name of a Java Class.
     * 
     * @param someClass
     *            Class
     * @return String name of the Class
     */
    public static String shortName(Class<?> someClass) {
        String fullName = someClass.getName();
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    public static Object perform(Object receiver, String methodName) throws DiagnoseException {
        return perform(receiver, methodName, new Object[0]);
    }

    /**
     * Can invoke both instance and static methods on the receiver
     * 
     * @param receiver
     * @param methodName
     * @param arguments
     * @return
     * @throws DiagnoseException
     */
    public static Object perform(Object receiver, String methodName, Object[] arguments) throws DiagnoseException {
        Class<?>[] types = argumentTypesFrom(arguments);
        Object result;
        try {
            result = perform(receiver, methodName, arguments, types);
        } catch (DiagnoseException e) {
            for (int t = 0; t < types.length; t++)
                types[t] = types[t].getSuperclass(); // wild guess...
            result = perform(receiver, methodName, arguments, types);
        }
        return result;
    }

    /**
     * Can invoke both instance and static methods on the receiver
     * 
     * @param receiver
     * @param methodName
     * @param arguments
     * @return
     * @throws DiagnoseException
     */
    public static Object perform(Object receiver, String methodName, Object[] arguments, Class[] types) throws DiagnoseException {
        if ("this".equals(methodName))
            return receiver;
        Object result = null;
        try {
            Class methodClass = receiver.getClass();
            if (methodClass == Class.class) // receiver is class?
                methodClass = (Class) receiver;
            Method method = methodClass.getMethod(methodName, types);
            result = method.invoke(receiver, arguments);
        } catch (IllegalArgumentException e) {
            throw new DiagnoseException("IllegalArgumentException invoking " + methodName, e);
        } catch (IllegalAccessException e) {
            throw new DiagnoseException("IllegalAccessException invoking " + methodName, e);
        } catch (InvocationTargetException e) {
            throw new DiagnoseException("InvocationTargetException invoking " + methodName, e);
        } catch (SecurityException e) {
            throw new DiagnoseException("SecurityException invoking " + methodName, e);
        } catch (NoSuchMethodException e) {
            throw new DiagnoseException("NoSuchMethodException invoking " + methodName, e);
        }
        return result;
    }

    /**
     * Performs the constructing of a new instance from a Class.
     * 
     * @param instanceClass
     * @param constructorArguments
     * @return
     * @throws DiagnoseException
     */
    public static Object newInstance(Class<?> instanceClass, Object[] constructorArguments) throws DiagnoseException {
        try {
            Constructor<?> constructor = instanceClass.getConstructor(argumentTypesFrom(constructorArguments));
            return constructor.newInstance(constructorArguments);
        } catch (SecurityException e) {
            throw new DiagnoseException("SecurityException constructing " + instanceClass, e);
        } catch (NoSuchMethodException e) {
            throw new DiagnoseException("NoSuchMethodException constructing " + instanceClass, e);
        } catch (IllegalArgumentException e) {
            throw new DiagnoseException("IllegalArgumentException constructing " + instanceClass, e);
        } catch (InstantiationException e) {
            throw new DiagnoseException("InstantiationException constructing " + instanceClass, e);
        } catch (IllegalAccessException e) {
            throw new DiagnoseException("IllegalAccessException constructing " + instanceClass, e);
        } catch (InvocationTargetException e) {
            throw new DiagnoseException("InvocationTargetException constructing " + instanceClass, e);
        }

    }

    public static Class<?>[] argumentTypesFrom(Object[] arguments) {
        Class<?>[] types = new Class[arguments.length];
        for (int t = 0; t < arguments.length; t++)
            types[t] = arguments[t].getClass();
        return types;
    }

    /**
     * Resolve the name to a Class using the ContextClassLoader of the current
     * thread.
     * 
     * @param className
     * @return Class
     * @throws DiagnoseException
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        Class<?> loaded = null;
        try {
            loaded = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
            // retry using standard classloader
            loaded = DiagnoseUtil.class.getClassLoader().loadClass(className);
        }
        return loaded;
    }

    /**
     * Try to determine the class of the object that registered a DiagnosticTask
     * by inspecting the stack frame of a locally thrown Exception. Kind of a
     * trick to do it this way.
     * 
     * @return Class
     */
    protected static Class detectRequestorClass() {
        String className = null;
        try {
            throw new RuntimeException();
        } catch (RuntimeException re) {
            // traverse the stack to find the method that invoked this detection
            // method
            // the requestor class will be 2 frames away from this.
            int frameIndex = 0;
            StackTraceElement trace[] = re.getStackTrace();
            while (!("detectRequestorClass".equals(trace[frameIndex].getMethodName())))
                frameIndex++;
            frameIndex = Math.min(frameIndex + 2, trace.length);
            className = trace[frameIndex].getClassName();
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException cnfex) {
            // If the sender class could not be determined then answer this
            // class
            return Check.class;
        }
    }

    /**
     * Get a DataSource connection using JNDI.
     * 
     * @param datasourceName
     *            String
     * @return Connection
     * @throws DiagnoseException
     */
    public static Connection getDataSourceConnection(String datasourceName) throws DiagnoseException {
        Connection con = null;
        String errorMsg = "Unable to find or connect to [" + datasourceName + "]";
        try {
            DataSource ds = (DataSource) (new InitialContext().lookup(datasourceName));
            con = ds.getConnection();
        } catch (ClassCastException e) {
            throw new DiagnoseException(errorMsg, e);
        } catch (NamingException e) {
            throw new DiagnoseException(errorMsg, e);
        } catch (SQLException e) {
            throw new DiagnoseException(errorMsg, e);
        }
        return con;
    }

    /**
     * Try finding a resource using a class loader. First use the context
     * classloader of the current thread then try to use the classloader of this
     * utility unless useContextClassLoaderOnly is true
     * 
     * @param resource
     *            String
     * @param useContextClassLoaderOnly
     *            boolean
     * @return URL || null
     */
    public static URL findResource(String resource, boolean useContextClassLoaderOnly) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (useContextClassLoaderOnly || (url != null))
            return url;
        return DiagnoseUtil.class.getClassLoader().getResource(resource);
    }

    public static Enumeration<URL> findResources(String resource, boolean useContextClassLoaderOnly) throws IOException {
        Enumeration<URL> url = Thread.currentThread().getContextClassLoader().getResources(resource);
        if (useContextClassLoaderOnly || (url != null))
            return url;
        return DiagnoseUtil.class.getClassLoader().getResources(resource);
    }

    public static String format(String template, String param0) {
        return format(template, param0, "");
    }

    public static String format(String template, String param0, String param1) {
        return format(template, param0, param1, "");
    }

    public static String format(String template, String param0, String param1, String param2) {
        return MessageFormat.format(template, new Object[] { param0, param1, param2 });
    }

    public static String format(String template, String param0, String param1, String param2, String param3) {
        return MessageFormat.format(template, new Object[] { param0, param1, param2, param3 });
    }

    // one more, i know its ugly
    public static String format(String template, String param0, String param1, String param2, String param3, String param4) {
        return MessageFormat.format(template, new Object[] { param0, param1, param2, param3, param4 });
    }

    /**
     * Use XSD datetime format without zone indication
     */
    public static String format(Date dateTime) {
        String dot = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").format(new Date());
        return dot.replaceAll("[.]", "T");
    }

    public static URL retrieveURL(ExecutionContext ctx, String resourceName, String urlSpec) throws Exception {
        // 3 ways: by resource name, by url parameter (String), by url parameter
        // (variable)
        if (resourceName != null)
            return DiagnoseUtil.findResource(ctx.resolveString(resourceName), false);
        // url != null, variable or direct value
        Object storedURL = ctx.resolveValue(urlSpec);
        if (storedURL instanceof String)
            return new URL((String) storedURL);
        else
            return (URL) storedURL;
    }

    /**
     * Return the standard non-boolean getter for a property unless the property refers to the object "this".
     * 
     * @param property
     * @return getter or this
     */
    public static String getPropertyAccessMethodName(String property) {
        if ("this".equals(property))
            return "this";
        return "get" + property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1);
    }

    /**
     * Compose an error message from the exception and/or its causes.
     * 
     * @param ex
     * @return
     */
    public static String getErrorMessage(Exception ex) {
        if (ex.getCause() != null) {
            return "cause:" + ex.getCause().getMessage();
        } else {
            return ex.getMessage();
        }
    }
}
