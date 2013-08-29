/*
    Copyright 2008 Ernest Micklei @ PhilemonWorks.com

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
package com.philemonworks.selfdiagnose;

import org.xml.sax.Attributes;
/**
 * PatternMatchingTask is an abstract class that provides the functionality 
 * of matching a String value against a pattern (regular expression).
 * 
 * @author ernestmicklei
 */
public abstract class PatternMatchingTask extends DiagnosticTask {
    protected static final String PARAMETER_PATTERN = "pattern";
    protected String pattern;

    /*
     * (non-Javadoc)
     * 
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
     */
    public void initializeFromAttributes(Attributes attributes) {
        // store variable if specified
        super.initializeFromAttributes(attributes);
        // optional
        this.setPattern(attributes.getValue(PARAMETER_PATTERN));
    }
    /**
     * @return
     */
    public String getPattern() {
    	return pattern;
    }
    /**
     * @param string
     */
    public void setPattern(String string) {
    	pattern = string;
    }
    protected void checkValueAgainstPattern(DiagnosticTaskResult result, String receiver, String accessKind, String propertyName, Object value) {
        String regex = this.getPattern();
        String stringValue = "";
        if (!(value instanceof String)) {
            stringValue = String.valueOf(value);
        } else {
            stringValue = (String) value;
        }
        if (regex != null) {
            // test against given pattern but first make sure it is a String
            if (stringValue.matches(regex))
                result.setPassedMessage(DiagnoseUtil.format(
                		"{0} {1} [ {2} = {3} ] matches [{4}]", 
                        receiver,accessKind,propertyName,stringValue,regex));
            else
                result.setFailedMessage(DiagnoseUtil.format(
                        "{0} {1} [{2}] with value [{3}] does not match with [{4}]", 
                        receiver,accessKind,propertyName,stringValue,regex));
        } else {
            result.setPassedMessage(DiagnoseUtil.format(
                    "{0} {1} [ {2} = {3} ]", receiver, accessKind, propertyName, stringValue));
        }
    }
}
