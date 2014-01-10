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

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.XMLUtils;

/**
 * CheckXMLContent is a Diagnostic Task that verifies the presence of an element or attribute 
 * (by evaluating an XPath) and optionally test is against a given pattern.
 * <p/>
 * <pre>
&lt;checkxmlcontent name="resource.xml" xpath="/root/child" pattern="childvalue" /&gt;
&lt;checkxmlcontent url="URL string" xpath="/root/child" pattern="childvalue" /&gt; 
&lt;checkxmlcontent url="${variable with URL value}" xpath="/root/child" pattern="childvalue" /&gt;
 * </pre>
 * Stores the String value found into the (optional) specified variable. 
 * @author E.M.Micklei
 */
public class CheckXMLContent extends DiagnosticTask {
    private static final long serialVersionUID = 8914666406032427064L;
    private static final String PARAMETER_XPATH = "xpath";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_URL = "url";
    private static final String PARAMETER_PATTERN = "pattern";
    private String name;
    private String url;
    private String xpath;
    private String pattern;

    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#getDescription()
     */
    public String getDescription() {
        return "Check the contents of an XML resource using an Xpath match.";
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public String getUrl() {
        return url;
    }

    public String getXpath() {
        return xpath;
    }

    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(org.xml.sax.Attributes)
     */
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        xpath = attributes.getValue(PARAMETER_XPATH);
        url = attributes.getValue(PARAMETER_URL);
        name = attributes.getValue(PARAMETER_NAME);
        pattern = attributes.getValue(PARAMETER_PATTERN);
    }

    /* (non-Javadoc)
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#run(com.philemonworks.selfdiagnose.DiagnosticTaskResult)
     */
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        URL theURL;
        String value;
        try {
            theURL = DiagnoseUtil.retrieveURL(ctx, name, url);
            if (theURL == null)
                throw new DiagnoseException("Unable to find resource [" + (name == null ? url : name) + "]");
            InputStream inputStream = theURL.openStream();
            value = XMLUtils.valueForXPath(xpath, inputStream);
            // make available in the context before testing against the optional pattern
            ctx.setValue(this.getVariableName(), value);
            if (pattern != null) {
                if (!(value.matches(pattern))) {
                    result.setFailedMessage("Xpath value [" + value + "] does not match pattern [" + pattern + "] from resource [" + theURL + "]");
                } else {
                    result.setPassedMessage("Xpath value [" + value + "] matches pattern [" + pattern + "] from resource [" + theURL + "]");
                }
            } else {
                result.setPassedMessage("Resource is found [" + theURL + "]");
            }
        } catch (Exception e) {
            throw new DiagnoseException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        DiagnoseUtil.verifyNonEmptyString(PARAMETER_XPATH, xpath, CheckXMLContent.class);
        if (url == null)
            DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, CheckProperty.class);
        if (name == null)
            DiagnoseUtil.verifyNonEmptyString(PARAMETER_URL, url, CheckProperty.class);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

}