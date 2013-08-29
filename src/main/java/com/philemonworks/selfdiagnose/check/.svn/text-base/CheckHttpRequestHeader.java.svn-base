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
package com.philemonworks.selfdiagnose.check;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;
/**
 * CheckHttpRequestHeader reads a header field from the current Http Servlet Request.
 * If the protocol is specified then the check is only performed when it matches.
 * 
 * &lt;checkhttprequestheader header="" protocol="http" pattern="" /&gt;
 * 
 * @author ernestmicklei
 */
public class CheckHttpRequestHeader extends CheckProperty {
    private static final long serialVersionUID = 3506374991286668244L;
    
    private String protocol;

    public String getDescription() {
        return "Access a header field from the current Http Request (the one that runs the diagnose)";
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
     */
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        this.setProperty(attributes.getValue("header"));
        this.setProtocol(attributes.getValue("protocol"));
    }
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        DiagnoseUtil.verifyNotNull("header", this.getHeader(), CheckHttpRequestHeader.class);
    }
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        HttpServletRequest request = SelfDiagnoseServlet.getCurrentRequest();
        if (protocol != null) {
            String scheme = request.getScheme();
            if (!scheme.equals(protocol)) {
                result.setPassedMessage("Ignore check for http header ["+this.getHeader()+"] because of different protocol; expecting ["+protocol+"]");
                return;
            }
        }
        String value = request.getHeader(this.getHeader());
        this.checkValueAgainstPattern(result, "HTTP Request", "header", this.getHeader(), value);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHeader() {
        return this.getProperty();
    }

    public void setHeader(String header) {
        this.setProperty(header);
    }
}
