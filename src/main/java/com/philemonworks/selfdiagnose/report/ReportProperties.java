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

	public void run(ExecutionContext ctx, DiagnosticTaskResult result)
			throws DiagnoseException {
		CompositeDiagnosticTaskResult comResult = (CompositeDiagnosticTaskResult) result;
		Object value = ctx.resolveValue(this.getValue());
		if (value == null) {
			result.setFailedMessage(DiagnoseUtil.format("No properties set for variable {0}",this.getValue()));
			return;
		}
		Properties props = (Properties)value;
		String[] sortedKeys = new String[props.size()];
		System.arraycopy(props.keySet().toArray(), 0, sortedKeys, 0, sortedKeys.length);
		Arrays.sort(sortedKeys);
		for (int i=0;i<sortedKeys.length;i++) {
			String each = sortedKeys[i];
			DiagnosticTaskResult localResult = new DiagnosticTaskResult(this);
			localResult.setPassedMessage(DiagnoseUtil.format("{0} = {1}",each,props.getProperty(each)));
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
