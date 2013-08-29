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
package com.philemonworks.selfdiagnose;

import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

public class ExecutionContext {
	private Map values = new HashMap();

	/**
	 * Store the value for a variable unless no variable was specified (== null)
	 * @param variableName : String || null
	 * @param newValue : Object
	 * @throws DiagnoseException
	 */
	public void setValue(String variableName, Object newValue) throws DiagnoseException {
		// only store the value if a variable name was specified
		if (variableName != null)
			values.put(variableName, newValue);
	}
	/**
	 * Return the stored value for a variable. Null is allowed for the value.
	 * Throws an exception if this value is not available
	 * @param expression : String
	 * @return Object
	 * @throws DiagnoseException
	 */
	public Object getValue(String expression) throws DiagnoseException {
        try {
            return Ognl.getValue(expression, values);
        } catch (OgnlException e) {
            throw new DiagnoseException("Unable to evaluate expression ["+expression+"]",e);
        }
	}
	/**
	 * The argument is either a value or an expression.
	 * Expressions are specified using the syntax ${myexpression}
	 * Return the stored value for a variable.
	 * @param valueOrExpression
	 * @return Object
	 * @throws DiagnoseException
	 */
	public Object resolveValue(String valueOrExpression) throws DiagnoseException {
		if (valueOrExpression == null) return null; // should not happen?
		if (valueOrExpression.startsWith("${")) {
			String var = valueOrExpression.substring(2,valueOrExpression.length()-1);
			return this.getValue(var);
		}
		return valueOrExpression;
	}
	/**
	 * Checks and makes sure the return value is a String
	 * @param valueOrExpression : String
	 * @return String
	 * @throws DiagnoseException
	 */
	public String resolveString(String valueOrExpression) throws DiagnoseException {
		Object value = this.resolveValue(valueOrExpression);
		if (!(value instanceof String))
			throw new DiagnoseException("String value expected for ["+valueOrExpression+"] but value is ["+value+"]");
		return (String)value;
	}	
}
