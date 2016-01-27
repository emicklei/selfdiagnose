package com.philemonworks.selfdiagnose.check.vendor;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.philemonworks.selfdiagnose.output.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.philemonworks.selfdiagnose.SelfDiagnose;

/**
 * SelfDiagnoseResource provides a REST interface to the SelfDiagnose tool.
 * @author ernestmicklei
 */

@Path("/internal/selfdiagnose{extension}")
@Service("SelfDiagnoseResource")
public class SelfDiagnoseResource implements ApplicationContextAware{
    
    // http://localhost:9998/internal/selfdiagnose.html
    @GET
    @Produces("text/html,application/xml,application/json")
    @Consumes("text/html,application/xml")
    public Response runAndReportResults(@PathParam("extension") String format, @QueryParam("format") String formatOverride) {
        DiagnoseRunReporter reporter = new HTMLReporter();
        ResponseBuilder builder = Response.ok().header("Content-Type", "text/html");     
        if (".xml".equals(format) || "xml".equals(formatOverride)) {
            reporter = new XMLReporter();
            builder.header("Content-Type", "application/xml");    
        } else if (".json".equals(format) || "json".equals(formatOverride)) {
            reporter = new JSONReporter();
            builder.header("Content-Type", "application/json");
        }
        DiagnoseRun run = SelfDiagnose.runTasks(reporter);
        builder.header("X-SelfDiagnose-OK", run.isOK());
        return builder.entity(reporter.getContent()).build();
    }   
    
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response runSubmittedTasks(@PathParam("extension") String format, InputStream input) {
        SelfDiagnose.flush();
        try {
            SelfDiagnose.configure(input);
        } catch (Exception e) {
            return Response.serverError().entity(e.getCause().getMessage()).build();
        }
        return this.runAndReportResults(format, null);
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextInjector.inject(applicationContext);
    }    
}