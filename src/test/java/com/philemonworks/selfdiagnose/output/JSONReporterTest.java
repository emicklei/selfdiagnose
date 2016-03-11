package com.philemonworks.selfdiagnose.output;

import com.philemonworks.selfdiagnose.*;
import junit.framework.TestCase;

public class JSONReporterTest extends TestCase {

    private JSONReporter reporter = new JSONReporter();

    public void testShouldWriteJsonForMultipleTasks() {
        TestTask testTask1 = new TestTask();
        testTask1.setRequestor("me");
        testTask1.setComment("Test1");

        DiagnosticTaskResult result1 = new DiagnosticTaskResult(testTask1);
        result1.setFailedMessage("Failed1");
        result1.setExecutionTime(10);
        result1.setSeverity(Severity.WARNING);

        TestTask testTask2 = new TestTask();
        testTask2.setRequestor("you");
        testTask2.setComment("Test2");

        DiagnosticTaskResult result2 = new DiagnosticTaskResult(testTask2);
        result2.setPassedMessage("OK2");
        result2.setExecutionTime(20);
        result2.setSeverity(Severity.NONE);

        TestTask testTask3 = new TestTask();
        testTask3.setComment("Test3");

        DiagnosticTaskResult result3 = new DiagnosticTaskResult(testTask3);
        result3.setErrorMessage("Error3");
        result3.setExecutionTime(30);
        result3.setSeverity(Severity.CRITICAL);

        DiagnoseRun run = toDiagnoseRun(result1, result2, result3);

        String expected = toJson(run,
            "{\"task\":\"jsonreportertest$testtask\",\"status\":\"failed\",\"comment\":\"Test1\",\"message\":\"Failed1\",\"duration\":10,\"requestor\":\"me\",\"severity\":\"WARNING\"}," +
            "{\"task\":\"jsonreportertest$testtask\",\"status\":\"passed\",\"comment\":\"Test2\",\"message\":\"OK2\",\"duration\":20,\"requestor\":\"you\",\"severity\":\"NONE\"}," +
            "{\"task\":\"jsonreportertest$testtask\",\"status\":\"error\",\"comment\":\"Test3\",\"message\":\"Error3\",\"duration\":30,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");

        assertEquals(expected, report(run));
    }

    public void testShouldWriteJsonForPassedTask() {
        DiagnosticTaskResult result = new DiagnosticTaskResult(new TestTask());
        result.setPassedMessage("OK");

        DiagnoseRun run = toDiagnoseRun(result);

        String expected = toJson(run, "{\"task\":\"jsonreportertest$testtask\",\"status\":\"passed\",\"message\":\"OK\",\"duration\":0,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");
        assertEquals(expected, report(run));
    }

    public void testShouldWriteCommentFromTaskIfNonFromResult() {
        TestTask task = new TestTask();
        task.setComment("task comment");

        DiagnosticTaskResult result = new DiagnosticTaskResult(task);
        result.setPassedMessage("ok");

        DiagnoseRun run = toDiagnoseRun(result);

        String expected = toJson(run, "{\"task\":\"jsonreportertest$testtask\",\"status\":\"passed\",\"comment\":\"task comment\",\"message\":\"ok\",\"duration\":0,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");
        assertEquals(expected, report(run));
    }

    public void testShouldWriteCommentFromResultInsteadOfFromTask() {
        TestTask task = new TestTask();
        task.setComment("task comment");

        DiagnosticTaskResult result = new DiagnosticTaskResult(task);
        result.setComment("result comment");
        result.setPassedMessage("ok");

        DiagnoseRun run = toDiagnoseRun(result);

        String expected = toJson(run, "{\"task\":\"jsonreportertest$testtask\",\"status\":\"passed\",\"comment\":\"result comment\",\"message\":\"ok\",\"duration\":0,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");
        assertEquals(expected, report(run));
    }

    public void testShouldEscapeJsonCharacters() {
        DiagnosticTaskResult result = new DiagnosticTaskResult(new TestTask());
        result.setPassedMessage("\n\t\"\\/\b\f\r");

        DiagnoseRun run = toDiagnoseRun(result);

        String expected = toJson(run, "{\"task\":\"jsonreportertest$testtask\",\"status\":\"passed\",\"message\":\"\\n\\t\\\"\\\\\\/\\b\\f\\r\",\"duration\":0,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");
        assertEquals(expected, report(run));
    }

    public void testShouldWriteJsonForErrorTask() {
        DiagnosticTaskResult result = new DiagnosticTaskResult(new TestTask());
        result.setErrorMessage("ERROR :(");

        DiagnoseRun run = toDiagnoseRun(result);

        String expected = toJson(run, "{\"task\":\"jsonreportertest$testtask\",\"status\":\"error\",\"message\":\"ERROR :(\",\"duration\":0,\"requestor\":\"{unknown}\",\"severity\":\"CRITICAL\"}");
        assertEquals(expected, report(run));
    }

    private DiagnoseRun toDiagnoseRun(final DiagnosticTaskResult ... results) {
        DiagnoseRun run = new DiagnoseRun();

        for(int i = 0; i < results.length; i++) {
            run.results.add(results[i]);
        }

        return run;
    }

    private String toJson(DiagnoseRun run, String results) {
        return "{\"selfdiagnose\":{\"version\":\""+ SelfDiagnose.VERSION +"\"},\"run\":\"" + DiagnoseUtil.format(run.endDateTime) + "\",\"since\":\"" + DiagnoseUtil.format(Startup.TIMESTAMP) + "\",\"results\":[" + results + "]}";
    }

    private String report(DiagnoseRun run) {
        reporter.report(run);
        return reporter.getContent();
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