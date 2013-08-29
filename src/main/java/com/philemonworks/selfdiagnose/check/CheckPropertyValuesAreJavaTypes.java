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
import java.util.Enumeration;
import java.util.Properties;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.CompositeDiagnosticTaskResult;
import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckPropertyValuesAreJavaTypes is a DiagnosticTask that verifies that each Java type name is loadable. The pattern
 * parameter is used to filter only those keys for which the value represent a Java type.
 * <p/>
 * <pre>
&lt;checkpropertyvaluesarejavatypes name="some.properties" filter=".*[.]class" /&gt;
 * </pre>
 * @author E.M.Micklei
 */
public class CheckPropertyValuesAreJavaTypes extends DiagnosticTask {
	private static final String PARAMETER_RESOURCE = "name";
	private static final String PARAMETER_FILTER = "filter";
	
	private String resourceName;
	private String filter = ".*";

	/**
	 * This method is redefined such that the run method will get a composed result for its argument.
	 * @return CompositeDiagnosticTaskResult
	 */
	public DiagnosticTaskResult createResult(){
		return new CompositeDiagnosticTaskResult(this);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
	    super.initializeFromAttributes(attributes);
		this.setResourceName(attributes.getValue(PARAMETER_RESOURCE));
		this.setFilter(attributes.getValue(PARAMETER_FILTER));
	}
	/**
	 * Return the description of this task.
	 */
	public String getDescription() {
		return "Check that all values for which the key matches the given pattern, are loadable Java types.";
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#setUp()
	 */
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_RESOURCE, resourceName, this.getClass());
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_FILTER, filter, this.getClass());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run() @todo common code to put in DiagnoseUtil
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		CompositeDiagnosticTaskResult composedResult = (CompositeDiagnosticTaskResult)result;
		URL url = DiagnoseUtil.findResource(resourceName,false);
		if (url == null)
			throw new DiagnoseException("Unable to find [" + resourceName + "]");
		Properties props = new Properties();
		try {
			InputStream is = url.openStream();
			props.load(is);
		} catch (IOException e) {
			throw new DiagnoseException("Unable to load properties", e);
		}
		int count = 0;
		Enumeration penum = props.keys();
		while (penum.hasMoreElements()) {
			String key = (String) penum.nextElement();
			if (key.matches(filter)) {
				this.checkClassLoadable(props, key,composedResult);
				count++;
			}
		}
		int failed = composedResult.howManyErrors();
		if (failed > 0) {
			result.setFailedMessage(failed + " out of " + count + " property values from [" + url + "] for keys that match [" + filter + "] are non-loadable Java Types.");
		} else {
			result.setPassedMessage("All " + count + " property values from [" + url + "] for keys that match [" + filter + "] are loadable Java Types.");
		}
	}
	/**
	 * Run a sub-task to verify that the value of a properties key is a loadable type.
	 * 
	 * @param props
	 *        Properties
	 * @param key
	 *        String
	 * @param result
	 * 			DiagnosticTaskResult 
	 */
	private void checkClassLoadable(Properties props, String key, CompositeDiagnosticTaskResult 	result) {
		CheckClassLoadable task = new CheckClassLoadable();
		task.setClassName(props.getProperty(key));
		task.setRequestor(this.getRequestor());
		result.addResult(task.run());
	}
	/**
	 * Get the regular expression pattern.
	 * 
	 * @return String
	 */
	public String getFilter() {
		return filter;
	}
	/**
	 * Get the name of the resource that contains the properties.
	 * 
	 * @return String name of resource
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * Set the regular expression pattern.
	 * 
	 * @param string
	 */
	public void setFilter(String string) {
		filter = string;
	}
	/**
	 * Set the name of the resource that contains the properties.
	 * 
	 * @param string
	 */
	public void setResourceName(String string) {
		resourceName = string;
	}
}
