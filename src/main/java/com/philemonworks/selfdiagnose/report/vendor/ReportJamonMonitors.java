package com.philemonworks.selfdiagnose.report.vendor;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorComposite;
import com.jamonapi.MonitorFactory;
import com.philemonworks.selfdiagnose.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ReportJamonMonitors extends DiagnosticTask {
    private static final long serialVersionUID = 4764469767469398343L;

    public ReportJamonMonitors() {
        setSeverity(Severity.NONE);
    }

    @Override
    public String getDescription() {
        return "JamonAPI Report";
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        MonitorComposite monco = MonitorFactory.getRootMonitor();
        Monitor[] monitors = monco.getMonitors();
        if (monitors == null) {
            result.setPassedMessage("No monitor activity recorded");
            return;
        }
        // sort monitors by label lexicographically
        Arrays.sort(monitors,new Comparator<Monitor>() {
            public int compare(Monitor o1, Monitor o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });

        CompositeDiagnosticTaskResult comResult = (CompositeDiagnosticTaskResult) result;
        for (Monitor each : monitors) {
            DiagnosticTaskResult localResult = new DiagnosticTaskResult(this);
            localResult.setComment(each.getLabel());
            localResult.setPassedMessage(this.composeRow(each));
            comResult.addResult(localResult);
        }
    }

    private String composeRow(Monitor each) {
        StringBuilder sb = new StringBuilder();
        append("hits", this.padded(Math.round(each.getHits())), sb);
        append(" | avg", this.padded(each.getAvg()), sb);
        append(" | stddev", this.padded(each.getStdDev()), sb);
        append(" | min", this.padded(each.getMin()), sb);
        append(" | max", this.padded(each.getMax()), sb);
        append(" | first", this.formatDate(each.getFirstAccess()), sb);
        append(" | last", this.formatDate(each.getLastAccess()), sb);
        return sb.toString();
    }

    private String padded(Double d) {
        String dString = Double.toString(d);
        if (d <= 99999999d && dString.length() > 8) // if d IS larger then do not chop
            return dString.substring(0,8);
        StringBuilder sb = new StringBuilder(dString);
        for (int p = sb.length() ; p < 8 ; p++) sb.append(' ');
        return sb.toString();
    }

    private String padded(long l) {
        StringBuilder sb = new StringBuilder(Long.toString(l));
        for (int p = sb.length() ; p < 8 ; p++) sb.append(' ');
        return sb.toString();
    }

    private String formatDate(Date aDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return df.format(aDate);
    }

    private void append(String label, Object what, StringBuilder sb) {
        Object value = what;
        if (what instanceof Double) {
            value = Math.round(100f * (Double)what) / 100;
        }
        sb.append(label).append(": ").append(value);
    }
    /**
     * This method is redefined such that the run method will get a composed
     * result for its argument.
     *
     * @return CompositeDiagnosticTaskResult
     */
    public DiagnosticTaskResult createResult() {
        return new CompositeDiagnosticTaskResult(this);
    }
}
