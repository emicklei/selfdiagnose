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

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.Severity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * HTMLReporter is to produce an HTML report for a DiagnoseRun.
 *
 * @author ernestmicklei
 */
public class HTMLReporter implements DiagnoseRunReporter {
    protected StringBuffer html = new StringBuffer();
    private boolean odd = true;

    public void report(DiagnoseRun run) {
        beginHTML();
        appendReportBody(run);
        endHTML();
    }

    public void appendReportBody(DiagnoseRun run) {
        html.append("<TABLE class='report'>");
        appendTableHeader();
        for (Iterator<DiagnosticTaskResult> it = run.results.iterator(); it.hasNext();) {
            this.write(it.next());
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
        html.append(" | <a href='?format=xml'>XML</a> <a href='?format=json'>JSON</a>");
        html.append(" | since:" + this.formatDate(Startup.TIMESTAMP));
        html.append(" report:" + this.formatDate(new Date()));
    }

    private String formatDate(Date aDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(aDate);
    }

    protected void write(DiagnosticTaskResult result) {
        if (!result.wantsToBeReported()) {
            return;
        }

        String rowClass = rowClass(odd, result);

        html.append("<tr class='" + rowClass + "'>");
        append(result);
        html.append("</tr>\n");

        odd = !odd;
    }

    protected void appendTableHeader() {
        html.append("<tr class='header'>");
        header("Comment");
        header("Result");
        header("Message");
        header("Severity");
        header("Time [ms]");
        html.append("</tr>\n");
    }

    protected void append(DiagnosticTaskResult result) {
        // comment
        if (result.hasComment()) {
            data(result.getComment());
        } else {
            data(result.getTask().getComment());
        }

        // result + message
        if (result.isPassed()) {
            data("OK");
        } else if (result.isFailed()) {
            data("FAILED");
        } else if (result.isError()) {
            data("ERROR");
        }

        data(result.getMessage());

        // severity
        data(toSeverityLabel(result));

        // time
        if (result.getExecutionTime() == 0) {
            data(null);
        } else {
            data(Long.toString(result.getExecutionTime()));
        }
    }

    protected void data(String content) {
        html.append("<td>");
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
        html.append("<HEAD>\n");
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
        html.append("</HEAD>\n");
        html.append("<BODY>\n");
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

    private String rowClass(boolean odd, DiagnosticTaskResult result) {
        StringBuilder css = new StringBuilder();

        css.append(odd ? "odd" : "even")
            .append(" ");

        if (result.isPassed()) {
            css.append("passed");
        } else if (result.isFailed()) {
            css.append("failed");
        } else if (result.isError()) {
            css.append("error");
        }

        css.append(" ")
            .append(result.getSeverity().name().toLowerCase());

        return css.toString();
    }

    private String toSeverityLabel(final DiagnosticTaskResult result) {
        if(result.getSeverity() == Severity.NONE) {
            return "";
        } else {
            return result.getSeverity().name().toLowerCase();
        }
    }

}
