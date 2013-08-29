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
package com.philemonworks.selfdiagnose.check.vendor;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.Instance;
/**
 * CheckEndecaService is task specific to Endeca Search Engine.
 * This task can query the services and report the count and time values.
 * If a variable name is given then store the ENEQueryResults in the execution context.
 * 
 * @author ernestmicklei
 */
public class CheckEndecaService extends DiagnosticTask {
    private static final long serialVersionUID = 4081027704734026285L;
    
    private String host;
    private String port;
    private String query;

    public String getDescription() {
        return "Verifies the state of the Endeca service";
    }   
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        String eneHost = ctx.resolveString(host);
        String enePort = ctx.resolveString(port);
        try {
            /*
            HttpENEConnection connection = new HttpENEConnection();
            connection.setHostname(host);
            connection.setPort(port);
            UrlENEQuery eneQuery = new UrlENEQuery(query, "UTF-8");
            ENEQueryResults eneResults = connection.query(eneQuery);
            Navigation navigation = eneResults.getNavigation();
            long numERecs = navigation.getTotalNumERecs();
            double time = eneResults.getTotalNetworkAndComputeTime();
            */        
            Instance connection = Instance.create("com.endeca.navigation.HttpENEConnection");
            connection.invoke("setHostname", eneHost);
            connection.invoke("setPort", enePort);
            Instance eneQuery = Instance.create("com.endeca.navigation.UrlENEQuery",new Object[]{query,"UTF-8"});
            Instance eneResults= connection.invoke("query",eneQuery);
            Instance navigation = eneResults.invoke("getNavigation");
            long numERecs = navigation.invoke("getTotalNumERecs").longValue();
            double time = eneResults.invoke("getTotalNetworkAndComputeTime").doubleValue();
            
            result.setPassedMessage(
                "Total number of products in Endeca: " + numERecs + ". " +
                "Total network and computation time by Endeca: " + time);
            ctx.setValue(this.getVariableName(), eneResults.value());
          } catch (Exception e) {
              throw new RuntimeException("Unable to query Endeca", e);
          }              
    }
    public void initializeFromAttributes(Attributes attributes) {
        host = attributes.getValue("host");
        port = attributes.getValue("port");
        query = attributes.getValue("query");
        super.initializeFromAttributes(attributes);
    }
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        DiagnoseUtil.verifyNotNull("host", host, CheckEndecaService.class);
        DiagnoseUtil.verifyNotNull("port", port, CheckEndecaService.class);
        DiagnoseUtil.verifyNotNull("query", query, CheckEndecaService.class);
    }
}
