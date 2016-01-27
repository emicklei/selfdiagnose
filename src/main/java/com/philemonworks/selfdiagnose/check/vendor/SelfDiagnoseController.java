package com.philemonworks.selfdiagnose.check.vendor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.philemonworks.selfdiagnose.output.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.philemonworks.selfdiagnose.SelfDiagnose;

@Controller
public class SelfDiagnoseController implements ApplicationContextAware {

    @RequestMapping(method = RequestMethod.GET, value = { "/internal/selfdiagnose{extension}" })
    public ModelAndView displaySelfDiagnostics(
            final HttpServletRequest request, 
            final HttpServletResponse response,
            @PathVariable(value = "extension") final String extension,
            @RequestParam(value = "format", required = false) final String format){
        final ModelAndView mav = new ModelAndView("/internal/selfdiagnose");

        String acceptOrNull = request.getHeader(HttpHeaders.ACCEPT);
        // use html on default
        DiagnoseRunReporter reporter = new HTMLReporter();
        // unless somehow xml is asked for
        if (".xml".equalsIgnoreCase(extension) || 
             "xml".equalsIgnoreCase(format) ||
            (acceptOrNull != null && "application/xml".equalsIgnoreCase(acceptOrNull))) {
            reporter = new XMLReporter();
        } else if (".json".equalsIgnoreCase(extension) ||
                "json".equalsIgnoreCase(format) ||
                (acceptOrNull != null && "application/json".equalsIgnoreCase(acceptOrNull))) {
            reporter = new JSONReporter();
        }
        DiagnoseRun run = SelfDiagnose.runTasks(reporter);
        response.setHeader("X-SelfDiagnose-OK", Boolean.toString(run.isOK()));
        
        request.setAttribute("contentType", reporter.getContentType());
        mav.addObject("content", reporter.getContent());

        return mav;
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextInjector.inject(applicationContext);
    }
}