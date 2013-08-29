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

import java.util.Locale;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/**
 * CheckBeanProperty is a DiagnosticTask that checks a property and matches its (String) value to a regular expression pattern.
 * This task can only be used together with another task that provides the bean by inserting a variable into the execution context. 
 * <p/>
 * <pre>
&lt;checkbeanproperty bean="${name of var}" property="attribute name" pattern="regular expression" /&gt;
&lt;checkbeanproperty bean="${name of var}" method="toString"  pattern="regular expression" /&gt;
&lt;checkbeanproperty bean="${name of var}" pattern="regular expression" /&gt;
 * </pre>
 * Stores the property value into the (optional) specified variable.  
 * @author Ernest Micklei
 */
public class CheckBeanProperty extends CheckProperty {
    private static final long serialVersionUID = -7976546053356912993L;
    protected static final String PARAMETER_METHOD = "method";
	protected static final String PARAMETER_BEAN = "bean";	
	// optional parameter
	private String method;
	// context variable name
	private String bean;
	/**
	 * Return the description of this task.
	 */
	public String getDescription(){
		return "Check whether the value (or its String representation )of a bean property matches a pattern";
	}
	
	public void initializeFromAttributes(Attributes attributes) {
		super.initializeFromAttributes(attributes);
		this.setMethod(attributes.getValue(PARAMETER_METHOD));
		this.setBean(attributes.getValue(PARAMETER_BEAN));
	}

	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNotNull(PARAMETER_BEAN, bean, CheckBeanProperty.class);
	}
	// Property can be null because bean can be a String
	protected void checkPropertyAccess() throws DiagnoseException {}	
	
	public void run(ExecutionContext ctx, DiagnosticTaskResult result)
			throws DiagnoseException {
		Object beanValue = ctx.resolveValue(this.getBean());
		DiagnoseUtil.verifyNotNull("bean", beanValue, CheckBeanProperty.class);		

		Object value = beanValue;
		if (isMessagePerformRequired())
			value = DiagnoseUtil.perform(beanValue, this.constructGetter(), new Object[0]);
		ctx.setValue(this.getVariableName(), value);
		checkValueAgainstPattern(result,"Bean", "property", this.constructGetter(), value);
	}

    private boolean isMessagePerformRequired() {
		return property != null || method != null;
	}

	public String constructGetter(){
		if (method != null) 
			return method;
		if (property == null)
			return "this";
 		String getter = "get"
			+ (String.valueOf(this.getProperty().charAt(0)).toUpperCase(Locale.getDefault()))
			+ this.getProperty().substring(1);
		return getter;
	}
	
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setBean(String aString){
		bean = aString;
	}
	public String getBean(){
		return bean;
	}
}
