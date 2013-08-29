package com.philemonworks.selfdiagnose.report;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.philemonworks.selfdiagnose.CompositeDiagnosticTaskResult;
import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckValueMatches;

/**
 * ReportProperties can dump the key value pairs of a Map object
 * 
 * &lt;reportmap value="${myMap}" comment="my.map"/&gt;
 * 
 * @author ernestmicklei
 */
public class ReportMap extends CheckValueMatches {
    private static final long serialVersionUID = 2022702301493132046L;

    public String getDescription() {
        return "Reports the key=value pairs of a Map object";
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        CompositeDiagnosticTaskResult comResult = (CompositeDiagnosticTaskResult) result;
        Object value = ctx.resolveValue(this.getValue());
        if (value == null) {
            result.setFailedMessage(DiagnoseUtil.format("No Map set for variable {0}", this.getValue()));
            return;
        }
        Map map = (Map) value;
        String[] sortedKeys = new String[map.size()];
        Set keySet = new HashSet();
        Iterator keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()) {
            Object key = keyIterator.next();
            if (key instanceof String) {
                keySet.add(key);
            } else {
                keySet.add(key.toString());
            }
        }
        System.arraycopy(keySet.toArray(), 0, sortedKeys, 0, sortedKeys.length);
        Arrays.sort(sortedKeys);
        for (int i = 0; i < sortedKeys.length; i++) {
            String each = sortedKeys[i];
            DiagnosticTaskResult localResult = new DiagnosticTaskResult(this);
            Object eachValue = map.get(each);
            String valueString;
            if (eachValue instanceof String) {
                valueString = (String) eachValue;
            } else {
                if (eachValue == null) {
                    valueString = "null";
                } else {
                    valueString = eachValue.toString();
                }
            }
            localResult.setPassedMessage(DiagnoseUtil.format("{0} = {1}", each, valueString));
            comResult.addResult(localResult);
        }
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
