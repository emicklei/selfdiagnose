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
package com.philemonworks.selfdiagnose.output;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;
import com.philemonworks.selfdiagnose.XMLUtils;

/**
 * XMLReporter creates an XML document (String) with all the results of running SelfDiagnose.
 * The document conforms to the selfdiagnose.xsd.
 *
 * @author ernestmicklei
 *
 */
public class XMLReporter implements DiagnoseRunReporter {
    private final StringBuffer xml = new StringBuffer();

    public String getContent() {
        return xml.toString();
    }

    public String getContentType() {
        return "text/xml";
    }

    public void report(DiagnoseRun run) {
        beginXML();
        for (Iterator<DiagnosticTaskResult> it = run.results.iterator(); it.hasNext();) {
            this.report((DiagnosticTaskResult) it.next());
        }
        endXML();
    }

    public void report(DiagnosticTaskResult result) {
        if (!result.wantsToBeReported())
            return;
        xml.append("\t\t<result\n\t\t\ttask=\"");
        xml.append(result.getTask().getTaskName());
        xml.append("\"\n\t\t\tstatus=\"");
        xml.append(result.getStatus());
        xml.append("\"\n\t\t\tmessage=\"");
        xml.append(XMLUtils.encode(result.getMessage()));
        if (result.getTask().hasComment()) {
            xml.append("\"\n\t\t\tcomment=\"");
            xml.append(XMLUtils.encode(result.getTask().getComment()));
        }
        xml.append("\"\n\t\t\trequestor=\"");
        xml.append(result.getTask().getRequestor());
        xml.append("\"\n\t\t\tduration=\"");
        xml.append((String.valueOf(result.getExecutionTime())));
        xml.append("\"\n\t\t\tseverity=\"");
        xml.append((String.valueOf(result.getSeverity())));
        xml.append("\" />\n");
    }

    public void beginXML() {
        xml.append("<?xml version=\"1.0\" ?>\n");
        this.checkForStylesheet();
        xml.append("<selfdiagnose ");
        xml.append("run=\"");
        xml.append(DiagnoseUtil.format(new Date()));
        xml.append("\" ");
        if (SelfDiagnoseServlet.getCurrentRequest() != null) {
            xml.append("context=\"");
            xml.append(SelfDiagnoseServlet.getCurrentRequest().getContextPath());
            xml.append("\" ");
        }
        xml.append("version=\"");
        xml.append(SelfDiagnose.VERSION);
        xml.append("\" ");
        xml.append(" >\n");
        xml.append("\t<results>\n");
    }

    public void endXML() {
        xml.append("\t</results>\n");
        xml.append("</selfdiagnose>");
    }

    /**
     * If the request has an paramter "xsl" then insert the stylesheet reference in the
     * response.
     */
    private void checkForStylesheet() {
        HttpServletRequest request = SelfDiagnoseServlet.getCurrentRequest();
        if (request != null && request.getParameter("xsl") != null) {
            xml.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            xml.append(request.getParameter("xsl"));
            xml.append("\"?>\n");
        }
    }
}
