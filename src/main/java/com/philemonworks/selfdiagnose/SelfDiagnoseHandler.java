/*
    Copyright 2006 Ernest Micklei @ PhilemonWorks.com

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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;

/**
 * SelfDiagnoseHandler is a SAX handler that can read the configuration xml file
 * called <strong>selfdiagnose.xml</strong> that conforms to the XSD <strong>selfdiagnose-2.0.xsd</strong>
 *
 * @author E.M.Micklei
 */
public class SelfDiagnoseHandler extends DefaultHandler {
    private Stack<DiagnosticTask> iteratorStack = new Stack<DiagnosticTask>();
    private static Properties taskMapping;

    /**
     * In order to read the configuration, all task element definitions must be known.
     */
    static {
        taskMapping = new Properties();
        try {
            taskMapping.load(SelfDiagnoseHandler.class.getResourceAsStream("/task-mapping.properties"));
        } catch (IOException e) {
            Logger.getLogger(SelfDiagnoseHandler.class).error("Unable to initialize SelfDiagnoseHandler", e);
        }
    }

    /**
     * Register a DiagnosticTask with an XML tag (which is constructed from the short class name in lowercase).
     * If the configuration has an element named by this tag then a new DiagnosticTask
     * is created and initialized by the handler.
     *
     * @param diagnosticTaskClass Class must be concrete subclass of DiagnosticTask
     */
    public static void addBindingFor(Class<? extends DiagnosticTask> diagnosticTaskClass) {
        if (!DiagnosticTask.class.isAssignableFrom(diagnosticTaskClass))
            throw new RuntimeException("Only DiagnosticTask classes can be handled");
        String tag = DiagnoseUtil.shortName(diagnosticTaskClass).toLowerCase(Locale.getDefault());
        taskMapping.setProperty(tag, diagnosticTaskClass.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
     *      org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("selfdiagnose".equals(qName)) {
            this.handleHeader(attributes);
            return; // document root
        }
        if ("tasks".equals(qName))
            return; // sequence
        if ("task".equals(qName)) {
            this.handleCustomTask(attributes);
            return;
        }
        if ("iterator".equals(qName)) {
            this.handleIterator(attributes);
            return;
        }
        try {
            // element names for tasks are case-insensitive
            String className = taskMapping.getProperty(qName.toLowerCase(Locale.getDefault()));
            @SuppressWarnings("unchecked")
            Class<? extends DiagnosticTask> taskClass = (Class<? extends DiagnosticTask>) Thread.currentThread().getContextClassLoader().loadClass(className);
            if (taskClass == null)
                throw new RuntimeException("No task class registered (case-insensitive) for:" + qName);
            DiagnosticTask task = (DiagnosticTask) taskClass.newInstance();
            task.initializeFromAttributes(attributes);
            this.addTaskToRegistration(task);
        } catch (Exception ex) {
            throw new SAXException("Failed to create/initialize the task for:" + qName, ex);
        }
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
        if ("iterator".equals(name)) {
            CollectionIteratorTask cit = (CollectionIteratorTask) iteratorStack.pop();
            this.addTaskToRegistration(cit);
        }
    }

    private void handleHeader(Attributes attributes) {
        String parallelExecution = attributes.getValue("parallel");
        if (parallelExecution != null && !parallelExecution.isEmpty() && parallelExecution.equals("true")) {
            SelfDiagnose.setParallelExecution(true);
        }
    }

    private void handleIterator(Attributes attributes) {
        CollectionIteratorTask cit = new CollectionIteratorTask();
        cit.initializeFromAttributes(attributes);
        iteratorStack.push(cit);
    }

    private void handleCustomTask(Attributes attributes) throws SAXException {
        CustomDiagnosticTask customTask = new CustomDiagnosticTask();
        String className = attributes.getValue("class");
        if (className != null) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends DiagnosticTask> taskClass = (Class<? extends DiagnosticTask>) Thread.currentThread().getContextClassLoader().loadClass(className);
                DiagnosticTask task = (DiagnosticTask) taskClass.newInstance();
                task.initializeFromAttributes(attributes);
                customTask.setTask(task);
            } catch (Exception e) {
                customTask.setErrorMessage(e.getMessage());
                this.addTaskToRegistration(customTask);
                throw new SAXException(e);
            }
        } else {
            // task will be resolved by its reference in a DI context
            // attributes will be copied to the actual task after resolving.
            customTask.initializeFromAttributes(attributes);
            customTask.setReference(attributes.getValue("ref"));
        }
        this.addTaskToRegistration(customTask);
    }

    private void addTaskToRegistration(DiagnosticTask task) {
        // if the task is contained by an iterator then add it to the container
        if (!iteratorStack.isEmpty()) {
            CollectionIteratorTask cit = (CollectionIteratorTask) iteratorStack.lastElement();
            cit.register(task);
            return;
        }
        SelfDiagnose.register(task, SelfDiagnose.getConfigFilename());
    }
}
