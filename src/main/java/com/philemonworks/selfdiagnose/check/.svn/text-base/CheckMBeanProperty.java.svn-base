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
package com.philemonworks.selfdiagnose.check;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/**
 * CheckMBeanProperty is a DiagnosticTask that checks a property and matches its (String) value to a regular expression pattern. 
 * <p/>
 * <pre>
&lt;checkmbeanproperty query="MyApp:name=myBean" operation="getVersion" /&gt;
&lt;checkmbeanproperty mbeanfactory="com.ibm.websphere.management.AdminServiceFactory" query="WebSphere:type=ListenerPort,name=sli_caseStatusService-ontvangenBLIBerichtLp,*" operation="isStarted" pattern="true" /&gt;
 * </pre>
 * Stores the property value into the (optional) specified variable.  
 * @author Ernest Micklei
 */
public class CheckMBeanProperty extends CheckProperty {
	private static final long serialVersionUID = 6589792469814079102L;
	protected static final String PARAMETER_OPERATION = "operation";
    private static final String PARAMETER_QUERY = "query";
	protected static final String PARAMETER_FACTORY = "mbeanserverfactory";    
    // optional
    protected String operation = null;
    // required
    protected String query;
    // required
    protected String mbeanServerFactoryClassName;
    
    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#getDescription()
     */
    public String getDescription() {
        return "Check whether the String value of a managed bean property matches a pattern";
    }

    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#run(com.philemonworks.selfdiagnose.ExecutionContext, com.philemonworks.selfdiagnose.DiagnosticTaskResult)
     */
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        Object value = this.invokePropertyOnMBean(result);
        ctx.setValue(this.getVariableName(), value);        
		String stringValue = "";
		if (!(value instanceof String)) {
			stringValue = String.valueOf(value);
		} else {
			stringValue = (String) value;
		}
		if (pattern != null) {
			if (stringValue.matches(this.getPattern()))
				result.setPassedMessage(DiagnoseUtil.format(
					"MBean property [ {3} , {1} = {0} ] matches [{2}]", 
					stringValue, this.operationOrAttribute(), this.getPattern(), this.getQuery()));
			else
				result.setFailedMessage(DiagnoseUtil.format(
					"MBean property [ {3} , {1} = {0} ] does not match [{2}]", 
					stringValue, this.operationOrAttribute(), this.getPattern(), this.getQuery()));
		} else {
			result.setPassedMessage(DiagnoseUtil.format(
					"MBean property [ {1} , {0} = {2} ]",
					this.operationOrAttribute(), this.getQuery(), stringValue ));
		}
    }
    private String operationOrAttribute(){
        if (this.getOperation() != null) return this.getOperation(); // overrides attribute
        return this.getProperty();
    }
    /**
     * Create a new MBeanServer.
     * @return MBeanServer
     * @throws DiagnoseException
     */
    protected MBeanServer getMBeanServer() throws DiagnoseException {
        List mbeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mbeanServers != null && mbeanServers.size() > 0) {
            return (MBeanServer) mbeanServers.get(0);
        } else {
            // create one
            return ManagementFactory.getPlatformMBeanServer();
        }
    }
	/**
	 * Override this method to verify that task parameters are initialized/set correctly.
	 * @param ctx : ExecutionContext
	 * @throws DiagnoseException
	 */
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        if (this.getOperation() == null)  // then property must be set
            super.setUp(ctx);
        DiagnoseUtil.verifyNotNull(PARAMETER_QUERY, query, CheckMBeanProperty.class);
    }
	/**
	 * This method is sent from the SelfDiagnoseHandler when a configuration is being processed. Use the passed
	 * attributes to initialize the receiver.
	 * 
	 * @param attributes : org.xml.sax.Attributes
	 */
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        this.setOperation(attributes.getValue(PARAMETER_OPERATION));
        this.setQuery(attributes.getValue(PARAMETER_QUERY));
        this.setMbeanServerFactoryClassName(attributes.getValue(PARAMETER_FACTORY));
    }
    /**
     * Find the MBean and invoke the property as a method
     * @param result : DiagnosticTaskResult
     * @return Object
     * @throws DiagnoseException
     */
    protected Object invokePropertyOnMBean(DiagnosticTaskResult result) throws DiagnoseException {
        // MBeanServer server = this.getMBeanServer();
        MBeanServer server = this.getMBeanServer();
        Set objectNames = null;
        try {
            objectNames = server.queryNames(new ObjectName(this.getQuery()), null);
        } catch (MalformedObjectNameException e) {
            throw new DiagnoseException("Invalid query:" + this.getQuery(), e);
        }
        if (objectNames.size() == 0) {
        	throw new DiagnoseException("No MBean found by query [" + this.getQuery() + "]");
        }
        ObjectName nodeAgent = (ObjectName) objectNames.iterator().next();
        if (this.isThisRequested()) {
        	return nodeAgent;
        }        
        Object operationResult = null;
        // operation precedes property
        if (this.getOperation() != null){
            operationResult = this.invokeOperationOnMBean(nodeAgent,server);
        } else {
            operationResult = this.accessAttributeFromMBean(nodeAgent,server);
        }
        return operationResult;
    }
    /**
     * Invoke the method on the MBean using the MBeanServer
     * @param ObjectName nodeAgent
     * @param MBeanServer server
     * @return Object
     * @throws DiagnoseException
     */
    protected Object invokeOperationOnMBean(ObjectName nodeAgent, MBeanServer server) throws DiagnoseException {
        Object result = null;
        try {
            result = server.invoke(nodeAgent, this.getOperation(), null, null);
        } catch (Exception e1) {
            throw new DiagnoseException("Unable to invoke operation [" + this.getOperation() + "] on ["+nodeAgent+"]", e1);
        }
        return result;
    }   
    /**
     * Access the attribute of the MBean using the MBeanServer
     * @param ObjectName nodeAgent
     * @param MBeanServer server
     * @return Object
     * @throws DiagnoseException
     */
    protected Object accessAttributeFromMBean(ObjectName nodeAgent, MBeanServer server) throws DiagnoseException {
        Object result = null;
        try {
            result = server.getAttribute(nodeAgent, this.getAttribute());
        } catch (Exception e1) {
            throw new DiagnoseException("Unable to access attribute [" + this.getAttribute() + "] from ["+nodeAgent+"]", e1);
        }
        return result;
    }     
    public String getAttribute(){
        // alias
        return this.getProperty();
    }
    public String getMbeanServerFactoryClassName() {
        return mbeanServerFactoryClassName;
    }
    public void setMbeanServerFactoryClassName(String mbeanFactoryClassName) {
        this.mbeanServerFactoryClassName = mbeanFactoryClassName;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}    
}