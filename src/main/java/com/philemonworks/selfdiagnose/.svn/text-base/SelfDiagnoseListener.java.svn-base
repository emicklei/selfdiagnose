package com.philemonworks.selfdiagnose;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.philemonworks.selfdiagnose.output.LoggingReporter;

public class SelfDiagnoseListener implements ServletContextListener {
	private final static Logger LOG = Logger.getLogger(SelfDiagnoseListener.class);
	
	private boolean exitOnError = true;
	
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	public void contextInitialized(ServletContextEvent arg0) {
		LoggingReporter reporter = new LoggingReporter();
		SelfDiagnose.runTasks(reporter);
		if (!reporter.passed && exitOnError) {
			LOG.info("About to exit because of failed checks");
			System.exit(1);
		}
	}

	public boolean isExitOnError() {
		return exitOnError;
	}

	public void setExitOnError(boolean exitOnError) {
		this.exitOnError = exitOnError;
	}
}
