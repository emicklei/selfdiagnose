/*
 Copyright 2006-2015 Ernest Micklei @ PhilemonWorks.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
 */
package com.philemonworks.selfdiagnose;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import com.philemonworks.selfdiagnose.output.DiagnoseRun;
import com.philemonworks.selfdiagnose.output.DiagnoseRunReporter;
import com.philemonworks.selfdiagnose.output.XMLReporter;

/**
 * SelfDiagnose is the component that keeps a registration of DiagnosticTasks
 * and can run them providing a simple report that is logged using Log4j.
 * Registration is done:
 * <ul>
 * <li>programmatically by invoking the register() method passing a
 * DiagnosticTask subclass instance</li>
 * <li>declarative by providing a configuration file named
 * <strong>selfdiagnose.xml</strong> that conforms to the
 * <strong>selfdiagnose.xsd</strong></li>
 * </ul>
 * 
 * @author Ernest M. Micklei
 */
public abstract class SelfDiagnose {
    public static final String MDC_SELFDIAGNOSE_TASK_RESULT = "selfdiagnose-task-result";

    /**
     * The name of the resource that holds the specification of tasks.
     */
    public final static String VERSION = "2.7.3";
    public final static String COPYRIGHT = "(c) ernestmicklei.com";
    public final static String CONFIG = "selfdiagnose.xml";
    private static URL CONFIG_URL = null; // will be initialized by configure(...)
    private final static Logger LOG = Logger.getLogger(SelfDiagnose.class);

    private static List<DiagnosticTask> tasks = Collections.synchronizedList(new ArrayList<DiagnosticTask>());
    static {
        SelfDiagnose.configure(CONFIG);
    }

    /**
     * Return the filename that is used to configure SelfDiagnose
     * @return
     */
    public static String getConfigFilename() {
        if (SelfDiagnose.CONFIG_URL == null)
            return "unknown";
        String resourceName = SelfDiagnose.CONFIG_URL.getFile();
        return resourceName.substring(resourceName.lastIndexOf('/') + 1);
    }

    /**
     * Try to configure SelfDiagnose using the XML configuration file with the
     * given resource name.
     * 
     * @param resourceName : String
     */
    public static void configure(String resourceName) {
        // If a configuration file can be founds then process it first.
        Enumeration<URL> configURLs = null;
        try {
            configURLs = DiagnoseUtil.findResources(resourceName, false);
        } catch (IOException e1) {
            LOG.error("Unable to collect configuration file(s)", e1);
        }
        if (configURLs == null || !configURLs.hasMoreElements()) {
            LOG.warn("No configuration found. SelfDiagnose will only run tasks that are registered by the application. " + "If this was not intended then make sure that the configuration ["
                    + resourceName + "] can be found on the classpath.");
            return;
        }
        tasks = Collections.synchronizedList(new ArrayList<DiagnosticTask>());
        while (configURLs.hasMoreElements()) {
            URL each = configURLs.nextElement();
            LOG.info("Initializing from configuration [" + each.getFile() + "]");
            try {
                configure(each.openStream());
            } catch (Exception e) {
                LOG.error("Aborted configuration of SelfDiagnose using [" + each.getFile() + "] because: " + e.toString());
            }
        }

    }

    /**
     * Read the configuration from the resource by URL.
     * @param configURL : URL
     */
    public static void configure(URL configURL) {
        if (configURL == null) {
            LOG.info("Unable to configure because URL is not given");
            return;
        }
        LOG.info("Initializing from configuration [" + configURL.getFile() + "]");
        tasks = Collections.synchronizedList(new ArrayList());
        CONFIG_URL = configURL;
        try {
            configure(configURL.openStream());
        } catch (Exception e) {
            LOG.error("Aborted configuration of SelfDiagnose using [" + configURL.getFile() + "] because: " + e.toString());
        }
    }

    /**
     * Read the configuration from an InputStream
     * @param is
     * @throws Exception
     */
    public static void configure(InputStream is) throws Exception {

        SAXParser p = SAXParserFactory.newInstance().newSAXParser();
        SelfDiagnoseHandler s = new SelfDiagnoseHandler();
        p.parse(is, s);
        is.close();
    }

    /**
     * Flush all registered tasks from the configuration and re-load the
     * configuration.
     */
    public static void reloadConfiguration() {
        LOG.info("Flushing registered diagnostic tasks by configuration");
        SelfDiagnose.configure(CONFIG_URL);
    }

    /**
     * Add the argument to the global list of Diagnostic tasks.
     * 
     * @param task
     *            DiagnosticTask
     * @return DiagnosticTask
     */
    public static DiagnosticTask register(DiagnosticTask task) {
        return SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
    }

    /**
     * Add the argument to the global list of Diagnostic tasks.
     * 
     * @param task
     *            DiagnosticTask
     * @param identifier
     *            String
     * @return DiagnosticTask
     */
    public static DiagnosticTask register(DiagnosticTask task, String identifier) {
        task.setRequestor(identifier);
        tasks.add(task);
        return task;
    }

    public static DiagnoseRun runTasks() {
        return runTasks(new XMLReporter());
    }

    /**
     * Basic method to run all registered tasks.
     * 
     */
    public static DiagnoseRun runTasks(DiagnoseRunReporter reporter) {
        return SelfDiagnose.runTasks(tasks, reporter, new ExecutionContext());
    }

    /**
     * Basic method to run all registered tasks.
     * 
     */
    public static DiagnoseRun runTasks(DiagnoseRunReporter reporter, ExecutionContext ctx) {
        return SelfDiagnose.runTasks(tasks, reporter, ctx);
    }

    /**
     * Basic method to the tasks provided 
     * 
     */
    public static DiagnoseRun runTasks(List<DiagnosticTask> taskList, DiagnoseRunReporter reporter, ExecutionContext ctx) {
        DiagnoseRun run = new DiagnoseRun();
        List<DiagnosticTaskResult> results = new ArrayList<DiagnosticTaskResult>(taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            DiagnosticTask each = (DiagnosticTask) taskList.get(i);
            DiagnosticTaskResult result = null;
            // see if task wants to run with a timeout
            if (each.needsLimitedRuntime()) {
                result = new TaskBackgroundRunner().runWithin(each, ctx, each.getTimeoutInMilliSeconds());
            } else {
                result = each.run(ctx);
            }
            result.addToResults(results);
        }
        run.finished();
        run.results = results;
        reporter.report(run);
        return run;
    }

    /**
     * Run all registered DiagnosticTasks and report to the LOG. After running
     * all tasks, some simple statistics are logged.
     */
    public static void run() {
        SelfDiagnose.runTasks(new XMLReporter());
    }

    /**
     * Flush all registered tasks
     */
    public static void flush() {
        tasks = Collections.synchronizedList(new ArrayList<DiagnosticTask>());
    }

    /**
     * Return the modifiable collection of registered Diagnostic tasks. Use at
     * your own risk.
     */
    public static List<DiagnosticTask> getTasks() {
        return tasks;
    }

    /**
     * Remove the previously registered task. Ignore if was not present.
     * @param custom
     */
    public static void unregister(DiagnosticTask task) {
        tasks.remove(task);
    }
}
