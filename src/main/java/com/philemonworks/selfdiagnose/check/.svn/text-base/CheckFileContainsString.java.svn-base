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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckFileContainsString is a task that scans a file for at least one occurrence of a String. The file must be
 * character-based and is found as a resource (on the classpath) or a simple file.
 * <p/>
 * <pre>
 &lt;checkfilecontainsstring name="environment.properties" string="smtp.xs4all.nl" /&gt; 
 &lt;checkfilecontainsstring url="${urlvar}" string="smtp.xs4all.nl" /&gt;
 * </pre> 
 * Stores the URL (java.net.URL) of the file into the (optional) specified variable. 
 * @author emicklei
 */
public class CheckFileContainsString extends DiagnosticTask {
    private static final long serialVersionUID = 5554946205887093136L;
    private static final String PARAMETER_STRING = "string";
	private static final String PARAMETER_NAME = "name";
	private static final String PARAMETER_URL = "url";	
	private String url;
	private String name;
	private String string;

	public void initializeFromAttributes(Attributes attributes) {
		// variable
		super.initializeFromAttributes(attributes);
		this.setName(attributes.getValue(PARAMETER_NAME));
		this.setString(attributes.getValue(PARAMETER_STRING));
		this.setUrl(attributes.getValue(PARAMETER_URL));
	}
	/**
	 * Return the description of this task.
	 */	
	public String getDescription() {
		return "Check that the (sub)string can be found in the contents of this file";
	}	
	
	public String getConfiguration() {
		return "[" + string + "] pattern occurs in file [" + name + "]";
	}
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		if (url == null)
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, CheckFileContainsString.class);
		if (name == null )
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, url, CheckFileContainsString.class);		
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_STRING, string, CheckFileContainsString.class);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run()
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		DataInputStream dis = null;
		URL theUrl = null;
		// how to detect absolute file or resource?
		try {
			theUrl = DiagnoseUtil.retrieveURL(ctx,name,url);
			if (theUrl == null) {
				String msg = "Unable to find file";
				if (name == null) msg += " by url ["+url+"]"; else msg += " on classpath [" + name + "]";
				throw new DiagnoseException(msg);
			}
			ctx.setValue(this.getVariableName(), theUrl);
			InputStream is = theUrl.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
		} catch (Exception ex){
		    try {
                dis.close();
            } catch (IOException e) {
                throw new DiagnoseException(e);
            }
			throw new DiagnoseException(ex);
		}
		char firstChar = string.charAt(0);
		boolean found = false;
		// initialize buffer
		StringWriter buffer = new StringWriter();
		buffer.write(firstChar);
		try {
			while (dis.available() != 0 && !found) {
				char scanChar = (char) dis.read();
				if (scanChar == firstChar) {
					// remember this position for rollback
					dis.mark(string.length());
					// fetch the word
					for (int i = 0; i < string.length() - 1; i++)
						buffer.write(dis.read());
					// do the string match
					if (buffer.toString().equals(string)) {
						
						result.setPassedMessage("File ["+theUrl+"] contains string ["+string+"]");
						return;
					}
					// no success, re-initialize buffer and reset to marker
					buffer = new StringWriter();
					buffer.write(firstChar);
					dis.reset();
				}
			}
			dis.close();
		} catch (IOException ex) {
			throw new DiagnoseException(ex);
		}
		throw new DiagnoseException("No occurrences found of string [" + string + "] of file ["+theUrl+"]");		
	}
	/**
	 * Return the file name.
	 * 
	 * @return String the name of the file/resource
	 */
	public String getName() {
		return name;
	}
	/**
	 * Return the substring to search.
	 * 
	 * @return String substring
	 */
	public String getString() {
		return string;
	}
	/**
	 * Set the name of the file.
	 * 
	 * @param string
	 *        the name of the file/resource
	 */
	public void setName(String newFileName) {
		name = newFileName;
	}
	/**
	 * Set the substring to search.
	 * 
	 * @param string
	 */
	public void setString(String newString) {
		string = newString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
