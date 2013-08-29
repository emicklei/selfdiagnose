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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckResourceAccessible is a DiagnosticTask that verifies the availability of a resource by name on the classpath.
 * <p/>
 * <pre>
&lt;checkresourceaccessible name="application.properties" /&gt;
 * </pre>
 * Stores the URL (java.net.URL) of the resource into the (optional) specified variable. 
 * @author emicklei
 */
public class CheckResourceAccessible extends DiagnosticTask {
	private static final String PARAMETER_NAME = "name";
	private String name = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes){
		super.initializeFromAttributes(attributes);
		this.setName(attributes.getValue(PARAMETER_NAME));
	}
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, CheckResourceAccessible.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run()
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		URL url = DiagnoseUtil.findResource(name,false);
		if (url == null)
			throw new DiagnoseException("Unable to find:" + name);	
		ctx.setValue(this.getVariableName(), url);
		try {
			InputStream is = url.openStream();
			if (is.available() == 0)
				result.setFailedMessage("No data available from [" + name + "] located at [" + url + "]");			
		} catch (IOException ex) {
			throw new DiagnoseException(ex);
		}
		result.setPassedMessage("Resource [" + name + "] is located at [" + url + "]");
	}
	/**
	 * @return String resourceName
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}
	/**
	 * Return the description of this task.
	 */	
	public String getDescription() {
		return "Check that this resource can be found and is accessible";
	}
}
