package com.philemonworks.selfdiagnose;

import java.util.Properties;

import com.philemonworks.selfdiagnose.report.ReportProperties;

public class Report {
	private Report(){} /* prevent creating an instance */
	
	/**
	 * @param className
	 * @return String className
	 */
	public static String properties(Properties props) {
		ReportProperties task = new ReportProperties();
		task.setValue("${props}");
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return task.getValue();
	}
}
