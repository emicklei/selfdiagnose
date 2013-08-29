package com.philemonworks.selfdiagnose.check.vendor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;

public class SpringAwareSelfDiagnoseServlet extends SelfDiagnoseServlet{
    private static final long serialVersionUID = 5111111622619509179L;

    @Override
    public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
        ApplicationContext ctx = (ApplicationContext)this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        SpringApplicationContextInjector.inject(ctx);
        super.service(arg0, arg1);
    }
}
