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
package com.philemonworks.selfdiagnose.check;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.PatternMatchingTask;

/**
 * CheckValueMatches evaluates an OGNL expression and matches its String value against a pattern.
 * 
 * @author ronaldpulleman
 */
public class CheckValueMatches extends PatternMatchingTask {
    private static final long serialVersionUID = 5112361798086920923L;
    protected static final String PARAMETER_VALUE = "value";

    private String value;
    private String accessKind = "Context";
    private String accessName = "variable";
    
    public void initializeFromAttributes(Attributes attributes) {
        // store variable if specified
        super.initializeFromAttributes(attributes);
        this.setValue(attributes.getValue(PARAMETER_VALUE));
    }    
    
    public String getDescription() {
        return "Evaluates an expression and perform a match against a reqular expression pattern";
    }

    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        Object contextValue = ctx.resolveValue(this.getValue());
        this.checkValueAgainstPattern(result, accessKind, accessName, this.getValue(), contextValue);
        ctx.setValue(this.getVariableName(), contextValue);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

	public String getAccessKind() {
		return accessKind;
	}

	public void setAccessKind(String accessKind) {
		this.accessKind = accessKind;
	}

	public String getAccessName() {
		return accessName;
	}

	public void setAccessName(String accessName) {
		this.accessName = accessName;
	}
}
