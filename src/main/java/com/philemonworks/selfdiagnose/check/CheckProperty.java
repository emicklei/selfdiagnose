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

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.PatternMatchingTask;
/**
 * CheckProperty is an abstract class for tasks that verifies properties against a regular expression pattern.
 * 
 * @author Ernest Micklei
 */
public abstract class CheckProperty extends PatternMatchingTask {
	private static final long serialVersionUID = -1701980048760932884L;

	protected static final String PARAMETER_PROPERTY = "property";

	// parameters
	protected String property;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		// store variable if specified
		super.initializeFromAttributes(attributes);
		this.setProperty(attributes.getValue(PARAMETER_PROPERTY));
	}

	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		this.checkPropertyAccess();
	}

	protected void checkPropertyAccess() throws DiagnoseException {
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_PROPERTY, property,this.getClass());
	}

	/**
	 * @return
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param string
	 */
	public void setProperty(String string) {
		property = string;
	}
	/**
	 * Answer whether the object itself is checked rather than one of its properties (field,operation).
	 */
	public boolean isThisRequested(){
		return "this".equals(property);
	}
}