package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.Severity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.check.vendor.SpringApplicationContextInjector;

public class CustomSpringTaskTest {
    @Test
    public void testThatACustomTaskCanbeSpringBean() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/spring.xml");
        SelfDiagnose.configure("with-spring-task.xml");
        SpringApplicationContextInjector.inject(ctx); // this is what a Controller/Resource will do
        SelfDiagnose.run();
        Assert.assertTrue(SpringEnabledTask.RUNNED);
        Assert.assertEquals(SelfDiagnose.getTasks().get(0).getComment(), "comment");
        Assert.assertEquals(SelfDiagnose.getTasks().get(0).getSeverity(), Severity.WARNING);
    }
}
