/*
    Copyright 2008-2011 Ernest Micklei @ PhilemonWorks.com

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.SelfDiagnose;

/**
 * HTMLReporter is to produce an HTML report for a DiagnoseRun.
 * 
 * @author ernestmicklei
 */
public class HTMLReporter implements DiagnoseRunReporter {
    protected StringBuffer html = new StringBuffer();
    private boolean odd = true;
    public static final String BASIC_DATA = "";
    public static final Date STARTUP_TIMESTAMP = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());

    public void report(DiagnoseRun run) {
        beginHTML();
        appendReportBody(run);
        endHTML();
    }

    public void appendReportBody(DiagnoseRun run) {
        html.append("<TABLE>");
        appendTableHeader();
        for (Iterator it = run.results.iterator(); it.hasNext();) {
            this.write((DiagnosticTaskResult) it.next());
        }
        html.append("</TABLE>");
        appendLinkBar(run);
    }

    protected void appendLinkBar(DiagnoseRun run) {
        html.append("<h4>");
        appendLinkBarLinks(run);
        html.append("</h4>");
    }

    protected void appendLinkBarLinks(DiagnoseRun run) {
        html.append(run.getTotalsLine());
        html.append(" | <a href='http://selfdiagnose.sourceforge.net' target='_blank'>SelfDiagnose</a>");
        html.append(" | " + SelfDiagnose.VERSION);
        html.append(" | <a href='?format=xml'>XML</a>");
        html.append(" | since:" + this.formatDate(STARTUP_TIMESTAMP));
        html.append(" report:" + this.formatDate(new Date()));
    }
    
    private String formatDate(Date aDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(aDate);
    }    

    protected void write(DiagnosticTaskResult result) {
        if (!result.wantsToBeReported())
            return;
        String rowClass;
        if (odd)
            rowClass = "odd";
        else
            rowClass = "even";
        html.append("<tr class=\"" + rowClass + "\">");
        append(result);
        html.append("</tr>\n");
        odd = !odd;
    }

    protected void appendTableHeader() {
        html.append("<tr class='odd'>");
        header("Comment");
        header("Message");
        html.append("</tr>\n");
    }

    protected void append(DiagnosticTaskResult result) {
        if (result.hasComment()) {
            data(result.getComment(), null);
        } else {
            data(result.getTask().getComment(), null);
        }
        if (result.isPassed()) {
            data(result.getMessage(), "passed");
        } else if (result.isFailed()) {
            data("[FAILED] " + result.getMessage(), "failed");
        } else if (result.isError()) {
            data("[ERROR] " + result.getMessage(), "error");
        }
    }

    protected void data(String content, String cssClass) {
        html.append(cssClass == null ? "<td>" : "<td class='" + cssClass + "'>");
        html.append(content == null ? "" : content);
        html.append("</td>");
    }

    protected void header(String content) {
        html.append("<th>");
        html.append(content == null ? "" : content);
        html.append("</th>");
    }

    protected void beginHTML() {
        html.append("<HTML>\n");
        html.append("<BODY>\n");
        html.append("<STYLE>");
        try {
            InputStream is = this.getClass().getResourceAsStream("/report.css");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (reader.ready()) {
                html.append(reader.readLine()).append("\n");
            }
        } catch (IOException ex) {
        } // too bad
        html.append("</STYLE>");
    }

    protected void endHTML() {
        html.append("</BODY></HTML>");
    }

    public String getContent() {
        return html.toString();
    }

    public String getContentType() {
        return "text/html";
    }
}
