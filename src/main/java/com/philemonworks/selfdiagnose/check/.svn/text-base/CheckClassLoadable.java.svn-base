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
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/** 
 * CheckClassLoadable is a DiagnosticTask that verifies that a given class is loadable.
 * This task using the context class loader provided by the current thread.
 * If that fails then it tries using the class loader of the task itself.
 * If that fails too then the task reports the failure.
 * <p/>
 * <pre>
 &lt;checkclassloadable name="org.xml.sax.Attributes" /&gt;
 * </pre> 
 * @author E.M.Micklei
 */
public class CheckClassLoadable extends DiagnosticTask {
    private static final long serialVersionUID = -3344160860801902183L;
    private static final String PARAMETER_NAME = "name";
	private String className;
	/**
	 * Return the description of this task.
	 */	
	public String getDescription() {
		return "Check that this class can be found and is loadable";
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes){
		this.setClassName(attributes.getValue(PARAMETER_NAME));
		super.initializeFromAttributes(attributes);
	}
	
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, className, CheckClassLoadable.class);
	}
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		try {
			DiagnoseUtil.classForName(className);
		} catch (ClassNotFoundException ex) {
			result.setFailedMessage(DiagnoseUtil.format("Class [{0}] is not found or cannot be loaded",className));
			return;
		}
		result.setPassedMessage(DiagnoseUtil.format("Class [{0}] is loadable", className));
	}
	/**
	 * Name of the class to find.
	 * @return String
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * Name of the class to find
	 * @param string String
	 */
	public void setClassName(String string) {
		className = string;
	}
}
