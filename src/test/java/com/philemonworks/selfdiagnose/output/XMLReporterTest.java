package com.philemonworks.selfdiagnose.output;

import com.philemonworks.selfdiagnose.*;
import junit.framework.TestCase;

import java.util.Date;

public class XMLReporterTest extends TestCase {

    private XMLReporter reporter = new XMLReporter();

    public void testShouldGenerateExpectedXML() {
        TestTask task = new TestTask();
        task.setRequestor("me");
        task.setComment("Test1");

        DiagnosticTaskResult result = new DiagnosticTaskResult(task);
        result.setFailedMessage("Warning1");
        result.setExecutionTime(10);
        result.setSeverity(Severity.WARNING);

        DiagnosticTaskResult result2 = new DiagnosticTaskResult(task);
        result2.setFailedMessage("Critical1");
        result2.setExecutionTime(10);
        result2.setSeverity(Severity.CRITICAL);

        DiagnoseRun run = new DiagnoseRun();
        run.results.add(result);
        run.results.add(result2);
        run.endDateTime = new Date();
        reporter.report(run);

        String actual = reporter.getContent();

        String expected = "<?xml version=\"1.0\" ?>\n" +
                "<selfdiagnose run=\"" + DiagnoseUtil.format(run.endDateTime) + "\" since=\"" + DiagnoseUtil.format(Startup.TIMESTAMP) +
                "\" checks=\"2\" failures=\"1\" version=\"" + SelfDiagnose.VERSION + "\"  >\n" +
                "\t<results>\n" +
                "\t\t<result\n" +
                "\t\t\ttask=\"xmlreportertest$testtask\"\n" +
                "\t\t\tstatus=\"failed\"\n" +
                "\t\t\tmessage=\"Warning1\"\n" +
                "\t\t\tcomment=\"Test1\"\n" +
                "\t\t\trequestor=\"me\"\n" +
                "\t\t\tduration=\"10\"\n" +
                "\t\t\tseverity=\"WARNING\" />\n" +
                "\t\t<result\n" +
                "\t\t\ttask=\"xmlreportertest$testtask\"\n" +
                "\t\t\tstatus=\"failed\"\n" +
                "\t\t\tmessage=\"Critical1\"\n" +
                "\t\t\tcomment=\"Test1\"\n" +
                "\t\t\trequestor=\"me\"\n" +
                "\t\t\tduration=\"10\"\n" +
                "\t\t\tseverity=\"CRITICAL\" />\n" +
                "\t</results>\n" +
                "</selfdiagnose>";

        assertEquals(expected, actual);
    }

    static class TestTask extends DiagnosticTask {
        private static final long serialVersionUID = -1l;

        @Override
        public String getDescription() {
            return "TEST";
        }

        @Override
        public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        }
    }
}