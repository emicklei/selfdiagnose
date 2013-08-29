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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.PatternMatchingTask;
import com.philemonworks.selfdiagnose.SelfDiagnoseServlet;

/**
 * CheckURLReachable is a DiagnosticTask that verifies that an URL is reachable
 * by connecting to it and inspecting the (http) response code.
 * The url parameter may refer to a variable in the execution context with a String or URL value.
 * <p/>
 * <pre>
&lt;checkurlreachable url="http://www.philemonworks.com" /&gt;
 * </pre>
 * Stores the contents returned from the URL into the (optional) specified variable.  
 * @author emicklei
 */
public class CheckURLReachable extends PatternMatchingTask {
    private static final long serialVersionUID = 5997980187300815588L;
    private static final String PARAMETER_URL = "url";
	private String url;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		super.initializeFromAttributes(attributes);
		this.setUrl(attributes.getValue(PARAMETER_URL));
	}
	public String getDescription() {
		return "Check whether the url is reachable and data is available";
	}
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, url, CheckURLReachable.class);
	}
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		Object urlOrString = ctx.resolveValue(url);
		boolean urlIsString = false;
		URL newURL = null;
		try {
			if (urlOrString instanceof String) {
				urlIsString = true;
				newURL = new URL((String)urlOrString);				
			} else if (urlOrString instanceof URL)
				newURL = (URL)urlOrString;
			else throw new DiagnoseException(DiagnoseUtil.format(
					"Variable for url [{0}] is [{1}] but a String or URL was expected.", 
					 url, urlOrString == null ? "null" : urlOrString.toString()));
		} catch (MalformedURLException e) {
			if (urlIsString) {
				// Try expanding the URL with an URL base taken from the request
				HttpServletRequest request = SelfDiagnoseServlet.getCurrentRequest();
				String base=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();			
				try {
					newURL = new URL(base + "/" + url);
				} catch (MalformedURLException ex) {
					throw new DiagnoseException(ex);
				}
			}
		}
		URLConnection newConnection = null;
		try {
			newConnection = newURL.openConnection();			
			InputStream content = (InputStream)newConnection.getContent();
			if (this.getPattern() != null || this.getVariableName() != null) {
			    InputStreamReader isr = new InputStreamReader(content);
                StringBuilder sb = new StringBuilder();
                while (isr.ready()) { sb.append((char)isr.read()); }
                isr.close();
                String data = sb.toString();
                ctx.setValue(this.getVariableName(),data);
                this.checkValueAgainstPattern(result, url, "downloaded","contents", data);  
			}
		} catch (IOException e1) {
			result.setFailedMessage("URL [" + newURL + "] is not reachable or no connection could be opened because [" + e1.getMessage() +"]");
			return;
		} finally {
		    newConnection = null;
		}
		result.setPassedMessage("URL [" + newURL + "] is reachable and content could be retrieved.");
	}
	/**
	 * Set the URL to test.
	 * 
	 * @param url : String
	 */
	public void setUrl(String urlSpec) {
		url = urlSpec;
	}
}
