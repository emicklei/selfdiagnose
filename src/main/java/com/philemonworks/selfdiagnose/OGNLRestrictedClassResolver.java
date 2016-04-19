package com.philemonworks.selfdiagnose;

import ognl.ClassResolver;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * OgnlRestrictedClassResolver exists to prevent expressions
 * from using static methods on as java.lang.Runtime.exec() .
 * Created by emicklei on 18/04/16.
 */
public class OgnlRestrictedClassResolver implements ClassResolver {
    private final static Logger log = Logger.getLogger(OgnlRestrictedClassResolver.class);

    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        if ("java.lang.Runtime".equals(className)) {
            log.warn("attempt to access class [" + className + "], returning null");
            return null;
        }
        return Class.forName(className);
    }
}
