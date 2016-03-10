package com.philemonworks.selfdiagnose.output;

import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.SelfDiagnose;

import java.util.Date;

/**
 * JSONReporter creates a JSON document with all the results of running SelfDiagnose.
 */
public class JSONReporter implements DiagnoseRunReporter {

    static final JsonObject JSON_SELFDIAGNOSE = new JsonObject()
        .field("version", SelfDiagnose.VERSION);

    private String content = "";

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return "application/json";
    }

    public void report(DiagnoseRun run) {
        JsonObject jsonRoot =
            new JsonObject()
                .field("selfdiagnose", JSON_SELFDIAGNOSE)
                .field("run", run.endDateTime)
                .field("since", Startup.TIMESTAMP)
                .field("results", toResultsJson(run));

        this.content = jsonRoot.toJson();
    }

    private JsonArray toResultsJson(final DiagnoseRun run) {
        JsonArray jsonResults = new JsonArray();
        for (DiagnosticTaskResult result: run.results) {
            if (result.wantsToBeReported()) {
                jsonResults.element(toResultJson(result));
            }
        }
        return jsonResults;
    }

    public JsonObject toResultJson(DiagnosticTaskResult result) {
        JsonObject obj = new JsonObject();

        obj.field("task", result.getTask().getTaskName())
           .field("status", result.getStatus());

        if(result.hasComment()) {
            obj.field("comment", result.getComment());
        } else if(result.getTask().hasComment()) {
            obj.field("comment", result.getTask().getComment());
        }

        obj.field("message", result.getMessage())
           .field("duration", result.getExecutionTime())
           .field("requestor", result.getTask().getRequestor())
           .field("severity", result.getSeverity());

        return obj;
    }

    private static class JsonArray {
        private StringBuilder json = new StringBuilder();

        private boolean firstField = true;

        public JsonArray element(JsonObject obj) {
            rawElement(obj.toJson());
            return this;
        }

        public void rawElement(String value) {
            if(!firstField) {
                json.append(",");
            }

            json.append(value);

            firstField = false;
        }

        public String toJson() {
            return "[" + json + "]";
        }
    }


    private static class JsonObject {

        private StringBuilder json = new StringBuilder();

        private boolean firstField = true;

        public JsonObject field(String name, long value) {
            rawField(name, Long.toString(value));
            return this;
        }

        public JsonObject field(String name, Object value) {
            rawField(name, quote(value.toString()));
            return this;
        }

        public JsonObject field(String name, Date value) {
            field(name, DiagnoseUtil.format(value));
            return this;
        }

        public JsonObject field(String name, JsonObject obj) {
            rawField(name, obj.toJson());
            return this;
        }

        public JsonObject field(String name, JsonArray array) {
            rawField(name, array.toJson());
            return this;
        }

        public void rawField(String name, String value) {
            if(!firstField) {
                json.append(",");
            }

            json.append('"')
                .append(name)
                .append('"')
                .append(":")
                .append(value);

            firstField = false;
        }

        public String toJson() {
            return "{" + json + "}";
        }

        public static String quote(String string) {
            if (string == null || string.length() == 0) {
                return "\"\"";
            }

            int len = string.length();
            StringBuilder sb = new StringBuilder(len + 4);

            sb.append('"');
            for (int i = 0; i < len; i += 1) {
                char c = string.charAt(i);
                switch (c) {
                    case '\\':
                    case '"':
                        sb.append('\\');
                        sb.append(c);
                        break;
                    case '/':
                        sb.append('\\');
                        sb.append(c);
                        break;
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    default:
                        if (c < ' ') {
                            String t = "000" + Integer.toHexString(c);
                            sb.append("\\u")
                              .append(t.substring(t.length() - 4));
                        } else {
                            sb.append(c);
                        }
                }
            }
            sb.append('"');
            return sb.toString();
        }
    }
}
