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

import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckValidURL is a DiagnosticTask that verifies the syntax of a given URL.
 * <p/>
 * <pre>
&lt;checkvalidurl url="http://www.nu.nl" /&gt;
 * </pre>
 * Stores the URL (java.net.URL) found into the (optional) specified variable. 
 * @author emicklei
 */
public class CheckValidURL extends DiagnosticTask {
    private static final long serialVersionUID = 8214638095945660357L;
    private static final String PARAMETER_URL = "url";
	private String url = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		super.initializeFromAttributes(attributes);
		this.setUrl(attributes.getValue(PARAMETER_URL));
	}
	/* (non-Javadoc)
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#preRun()
	 */
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, url, CheckValidURL.class);
	}
	/* 
	 * (non-Javadoc)
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run()
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {		
		if (url.indexOf(" ") != -1) {
			result.setFailedMessage("parameter [url] cannot have space characters");
			return;
		}
		try {
			URL theURL = new URL(url);
			// make available in the context if needed
			ctx.setValue(this.getVariableName(), theURL);
		} catch (MalformedURLException e) {
			result.setFailedMessage("URL [" + url + "] has no valid syntax." + e.getMessage());
		}
		// success
		result.setPassedMessage("URL [" + url + "] has valid syntax.");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#getDescription()
	 */
	public String getDescription() {
		return "Check that this URL spec is syntactically valid. It uses the URL class for this validation.";
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}
}
