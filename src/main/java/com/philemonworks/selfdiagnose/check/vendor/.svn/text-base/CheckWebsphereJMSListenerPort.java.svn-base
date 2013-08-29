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
package com.philemonworks.selfdiagnose.check.vendor;

import javax.management.MBeanServer;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckMBeanProperty;

/**
 * CheckWebsphereJMSListenerPort is a DiagnosticTask that verifies the operational status of a ListenerPort in WAS.
 *
 * &lt;checkwebspherejmslistenerport name="sli_caseStatusService-ontvangenBLIBerichtLp" /&gt;
 *
 *	CheckWebsphereJMSListenerPort requires the following runtime dependencies: 
 *	<ul>
 *	<li>MAVEN_REPO/ibm-websphere/jars/jmxc-5.1.jar</li>
 *	<li>MAVEN_REPO/ibm-websphere/jars/admin-5.1.jar</li>
 * </ul>
 * CheckWebsphereJMSListenerPort uses the following fixed parameters:
 * <ul>
 * 	<li>mbeanFactory=com.ibm.websphere.management.AdminServiceFactory</li>
 * <li>query=WebSphere:type=ListenerPort,name={name},*</li>
 * <li>method=isStarted</li>
 * <ul>
 * If these parameters must be different from your WAS installation then try using the CheckMBeanProperty task.
 * 
 * @author E.M.Micklei
 */
public class CheckWebsphereJMSListenerPort extends CheckMBeanProperty {
	private static final long serialVersionUID = -1325435114569866371L;
	private final static String PARAMETER_NAME = "name";	
    private String name;
    
    public CheckWebsphereJMSListenerPort(){
        super();
        this.setMbeanServerFactoryClassName("com.ibm.websphere.management.AdminServiceFactory");
        this.setOperation("isStarted");
        this.setPattern("true");
    }    
    
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        this.name = attributes.getValue(PARAMETER_NAME);
        this.setQuery("WebSphere:type=ListenerPort,name=" + this.getName() + ",*");
    }
    
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        if (this.getOperation() == null)  // then property must be set
            super.setUp(ctx);
        DiagnoseUtil.verifyNotNull(PARAMETER_NAME, name, CheckWebsphereJMSListenerPort.class);
        DiagnoseUtil.verifyNotNull(PARAMETER_FACTORY, mbeanServerFactoryClassName, CheckMBeanProperty.class);                
    }    
    
    /**
     * Create a new MBeanServer proxy using the specified MBeanFactory class
     * @return MBeanServer
     * @throws DiagnoseException
     */
    protected MBeanServer getMBeanServer() throws DiagnoseException {
        Class factoryClass = null;
        try {
            factoryClass = DiagnoseUtil.classForName(this.getMbeanServerFactoryClassName());
        } catch (ClassNotFoundException e) {
            throw new DiagnoseException("No such class:" + this.getMbeanServerFactoryClassName(), e);
        }
        Object wsFactory = DiagnoseUtil.perform(factoryClass,"getMBeanFactory",new Class[0]);
        return (MBeanServer)DiagnoseUtil.perform(wsFactory, "getMBeanServer", new Object[0]);
    }
    
    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#getDescription()
     */
    public String getDescription() {
        return "Verifies the operational status of a ListenerPort that reads JMS messages from a queue";
    }

    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#run(com.philemonworks.selfdiagnose.ExecutionContext, com.philemonworks.selfdiagnose.DiagnosticTaskResult)
     */
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        Object operationResult = this.invokePropertyOnMBean(result);
        Boolean isStarted = (Boolean) operationResult;
        if (isStarted.booleanValue()) {
            result.setPassedMessage("Listenerport ["+name+"] is available and started");
        } else {
            result.setFailedMessage("Listenerport ["+name+"] is available but not started");
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}