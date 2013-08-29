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

import java.util.ResourceBundle;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/**
 * CheckResourceBundleKey is a DiagnosticTask that verifies the existence of a given key for a given resource 
 *
 * <pre>
&lt;checkresourcebundlekey name="some.properties" key="welcom" /&gt;
 * </pre>
 * @author E.M.Micklei
 */
public class CheckResourceBundleKey extends DiagnosticTask {
	private static final String PARAMETER_KEY = "key";
	private static final String PARAMETER_NAME = "name";
	private String name;
	private String key;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		super.initializeFromAttributes(attributes);
		this.setName(attributes.getValue(PARAMETER_NAME));
		this.setKey(attributes.getValue(PARAMETER_KEY));
	}
	public String getDescription() {
		return "Check whether this Resource Bundle is found on the classpath and that it has a value for this key";
	}
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, CheckResourceBundleKey.class);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_KEY, key, CheckResourceBundleKey.class);
	}
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		ResourceBundle bundle;
		try {
			bundle = ResourceBundle.getBundle(name);
		} catch (java.util.MissingResourceException ex) {
			throw new DiagnoseException(ex);
		}
		try {
			Object value = bundle.getObject(key);
			ctx.setValue(this.getVariableName(),value);
		} catch (java.util.MissingResourceException ex) {
			throw new DiagnoseException("No such key [" + key + "] in bundle [" + name + "]", ex);
		}
		result.setPassedMessage("Key [" + key + "] is available in resource bundle [" + name + "]");
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTaskName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
