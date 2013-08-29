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

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
/**
 * CheckResourceProperty is a DiagnosticTask that verifies the availability of a property in a resource.
 * 
 * @author Ernest Micklei
 */
public class CheckResourceProperty extends CheckProperty {
    private static final long serialVersionUID = -5176698762404603297L;
    protected static final String PARAMETER_NAME = "name";
	protected static final String PARAMETER_URL = "url";
	// parameters
	protected String name;		
	protected String url;	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#getDescription()
	 */
	public String getDescription() {
		return "Check that the value of a property matches this pattern. The properties resource must be on the classpath.";
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		// store variable if specified
		super.initializeFromAttributes(attributes);
		this.setName(attributes.getValue(PARAMETER_NAME));
	}
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		if (url == null)
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, CheckProperty.class);
		if (name == null)
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, url, CheckProperty.class);
	}
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run(ExecutionContext)
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		String value = null;
		Properties properties = this.getProperties(ctx);
		value = properties.getProperty(property);
        // make available in the context if needed (even if null)
        ctx.setValue(this.getVariableName(), value);
		if (value == null) {
		    result.setFailedMessage("No such property available from resource [" + url + "]");
		    return;
		}
		if (pattern != null) {
			if (value.matches(pattern))
				result.setFailedMessage("Property value [" + value + "] does not match pattern [" + pattern + "] from resource [" + url + "]");
				return;
		}
		String msg = "Property [" + property + "] with value ["+value+"] exists in resource [" + url + "]";
		if (pattern != null) msg += " and matches [" + pattern + "]";
		result.setPassedMessage(msg);
	}
	
	protected Properties getProperties(ExecutionContext ctx) throws DiagnoseException {
		try {
			URL theUrl = DiagnoseUtil.retrieveURL(ctx,name,url);
			if (theUrl == null)
				throw new DiagnoseException("Unable to find resource [" + (name==null?url:name) + "]");
			Properties properties = new Properties();
			InputStream input = theUrl.openStream();		
			properties.load(input);
			return properties;
		} catch (Exception ex) {
			throw new DiagnoseException(ex);
		}		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url){ 
		this.url = url;
	}	
}
