package com.philemonworks.selfdiagnose.output;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;

public class LoggingReporter implements DiagnoseRunReporter {
	private final static Logger LOG = Logger.getLogger(LoggingReporter.class);
	
	public boolean passed = true;
	
	public void report(DiagnoseRun run) {
		LOG.info("Start running diagnostic tasks");
		for (Iterator<?> it = run.results.iterator(); it.hasNext();) {
			this.report((DiagnosticTaskResult) it.next());
		}
		LOG.info("End running diagnostic tasks");
	}

	private void report(DiagnosticTaskResult next) {
		if (next.isError()) {
			this.passed = false;
			LOG.error(next.getMessage() + commentFrom(next));
		}
		if (next.isFailed()) {
			this.passed = false;
			LOG.error(next.getMessage() + commentFrom(next));
		}
		if (next.isPassed()) {
			LOG.info(next.getMessage() + commentFrom(next));
		}		
	}

	private String commentFrom(DiagnosticTaskResult next) {
		if (next.getComment() == null) return "";
		return new StringBuilder()
			.append(" {")
			.append(next.getComment())
			.append("}")
			.toString();
	}
	
	public String getContent() {
		return null;
	}

	public String getContentType() {
		return null;
	}
}
