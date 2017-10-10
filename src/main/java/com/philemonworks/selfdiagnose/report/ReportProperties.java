package com.philemonworks.selfdiagnose.report;

import com.philemonworks.selfdiagnose.*;
import com.philemonworks.selfdiagnose.check.CheckValueMatches;

import java.util.Arrays;
import java.util.Properties;

/**
 * ReportProperties can dump the key value pairs of a Properties object
 *
 * &lt;reportproperties value="${myProperties}" comment="my.properties"/&gt;
 *
 * @author ernestmicklei
 */
public class ReportProperties extends CheckValueMatches {
	private static final long serialVersionUID = 2022702301493132046L;

	public String getDescription() {
		return "Reports the key=value pairs of a Properties object";
	}

	private Properties injectedProperties= null; // if null then read from execution context using value parameter.

    // set the properties to override the execution context lookup using the value parameter.
    public void setProperties(Properties props) {
        this.injectedProperties = props;
    }

	public void run(ExecutionContext ctx, DiagnosticTaskResult result)
			throws DiagnoseException {
		CompositeDiagnosticTaskResult comResult = (CompositeDiagnosticTaskResult) result;
		Properties props = this.injectedProperties;
		if (props == null) { // get it from the execution context
			Object value = ctx.resolveValue(this.getValue());
			if (value == null) {
				result.setFailedMessage(DiagnoseUtil.format("No properties set for variable {0}", this.getValue()));
				return;
			}
			props = (Properties) value;
		}
		String[] sortedKeys = new String[props.size()];
		System.arraycopy(props.keySet().toArray(), 0, sortedKeys, 0, sortedKeys.length);
		Arrays.sort(sortedKeys);
		int maxWidth = 0;
        for (int i=0;i<sortedKeys.length;i++) {
            String each = sortedKeys[i];
            maxWidth = Math.max(maxWidth,each.length());
        }
		for (int i=0;i<sortedKeys.length;i++) {
			String each = sortedKeys[i];
			DiagnosticTaskResult localResult = new DiagnosticTaskResult(this);
			String padded = each;
			while (padded.length() != maxWidth) {
			    padded = " " + padded;
            }
			localResult.setPassedMessage(DiagnoseUtil.format("{0} = {1}",padded,props.getProperty(each)));
			localResult.setSeverity(Severity.NONE);

			comResult.addResult(localResult);
		}
	}
	/**
	 * This method is redefined such that the run method will get a composed result for its argument.
	 * @return CompositeDiagnosticTaskResult
	 */
	public DiagnosticTaskResult createResult(){
		return new CompositeDiagnosticTaskResult(this);
	}
}
