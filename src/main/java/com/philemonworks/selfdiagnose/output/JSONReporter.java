package com.philemonworks.selfdiagnose.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSONReporter creates a JSON document with all the results of running SelfDiagnose.
 *
 * @author amhamid
 */
public class JSONReporter implements DiagnoseRunReporter {

    private final StringBuffer jsonResult = new StringBuffer();

    public String getContent() {
        return jsonResult.toString();
    }

    public String getContentType() {
        return "application/json";
    }

    public void report(DiagnoseRun run) {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();

        jsonResult.append(gson.toJson(run.results));
    }
}
