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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/**
 * 
 * CheckJNDIBinding is a task to check whether the naming server has a JNDI binding for a given name.
 * <p/>
 * <pre>
 &lt;checkjndibinding name="jdbc/mydb"/&gt;
 &lt;checkjndibinding name="jdbc/mydb" class="mypackage.DataSource"/&gt;
 &lt;checkjndibinding name="jdbc/mydb" class="mypackage.DataSource" url="localhost:900" factory="someFactory"/&gt;
 * </pre>
 * Stores the value found into the (optional) specified variable. 
 * @author E.M.Micklei
 */
public class CheckJNDIBinding extends DiagnosticTask {	
    private static final long serialVersionUID = 4773822648065380753L;
    private static final String PARAMETER_FACTORY = "factory";
	private static final String PARAMETER_URL = "url";
	private static final String PARAMETER_CLASS = "class";
	private static final String PARAMETER_NAME = "name";
	private String initialContextFactory;
	private String namingServerURL;
	private String jndiName;
	private String valueClassName;
	private Class valueClass;
	/**
	 * Return the description of this task.
	 */
	public String getDescription() {
		return "Check that the value of this binding is present and of the correct type.";
	}		
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		super.initializeFromAttributes(attributes);
		this.setJndiName(attributes.getValue(PARAMETER_NAME));
		// optional
		this.setValueClassName(attributes.getValue(PARAMETER_CLASS));
		// optional as pair
		this.setNamingServerURL(attributes.getValue(PARAMETER_URL));
	    this.setInitalContextFactory(attributes.getValue(PARAMETER_FACTORY));
	}		
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, jndiName, CheckJNDIBinding.class);
		// if specified then both must be present
		if (this.hasContextFactorySpecified()) {
		    DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, namingServerURL, CheckJNDIBinding.class);
		    DiagnoseUtil.verifyNonEmptyString(PARAMETER_FACTORY, initialContextFactory, CheckJNDIBinding.class);
		}    		
		if (this.hasValueClassSpecified()){
			// resolve to valueClass
			try {
				valueClass = DiagnoseUtil.classForName(valueClassName);
			} catch (ClassNotFoundException e) {
				throw new DiagnoseException("No such class ["+valueClassName+"]",e);
			}
		}
	}
	public boolean hasContextFactorySpecified(){
	    return namingServerURL != null || initialContextFactory != null; 
	}
	public boolean hasValueClassSpecified(){
	    return valueClassName != null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run()
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
	    Context initialContext = null;
	    Object value = null;
		try {
		    if (this.hasContextFactorySpecified()){
		        Hashtable env = new Hashtable();
		        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
		        env.put(Context.PROVIDER_URL, namingServerURL);
		        initialContext = new InitialContext(env);
		    } else {
		        initialContext = new InitialContext();
		    }
			value = initialContext.lookup(jndiName);
		} catch (NamingException e1) {
			throw new DiagnoseException("No such binding ["+jndiName+"]",e1);
		}
		if (value == null) {
			result.setFailedMessage("Value for binding [" + jndiName + "] is null, expected was [" + valueClassName + "]");
			return;
		}
		ctx.setValue(this.getVariableName(), value);
		if (this.hasValueClassSpecified() && !(valueClass.isAssignableFrom(value.getClass()))) {
		        throw new DiagnoseException("Value for binding [" + jndiName + "] is of type [" + value.getClass().getName() + "], expected was (or implementor of)["
					+ valueClassName + "] ");
		}
		if (this.hasValueClassSpecified()) {
			String msg = "JNDI binding [" + jndiName + "] of type [" + valueClassName + "] is available in naming server";
			if (namingServerURL != null)
				msg += "[" + namingServerURL + "]";
			result.setPassedMessage(msg);
		} else
		    result.setPassedMessage("JNDI binding [" + jndiName + "] is available");
	}
	/**
	 * @return String initalContextFactory
	 */
	public String getInitalContextFactory() {
		return initialContextFactory;
	}
	/**
	 * @return String namingServerURL
	 */
	public String getNamingServerURL() {
		return namingServerURL;
	}
	/**
	 * @return String
	 */
	public String getValueClassName() {
		return valueClassName;
	}
	/**
	 * @param string
	 */
	public void setInitalContextFactory(String string) {
		initialContextFactory = string;
	}
	/**
	 * @param string
	 */
	public void setNamingServerURL(String string) {
		namingServerURL = string;
	}
	/**
	 * @param class1
	 */
	public void setValueClassName(String class1) {
		valueClassName = class1;
	}
	/**
	 * @return String jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}
	/**
	 * @param string
	 */
	public void setJndiName(String string) {
		jndiName = string;
	}
}
