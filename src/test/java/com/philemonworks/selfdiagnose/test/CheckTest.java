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
package com.philemonworks.selfdiagnose.test;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.Check;
import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.check.CheckDirectoryAccessible;

public class CheckTest extends TestCase {
	public void setUp() {
		System.out.println("Test:" + getName());
	}

	public void tearDown() {
		SelfDiagnose.run();
		SelfDiagnose.flush();
	}

	public void testCheckValidURLNull() {
		Check.validURL(null);
	}

	public void testPropertyValue() {
		Check.propertyMatches("some.properties", "mykey", "myvalue");
	}

	public void testDirectoryJava() {
		Check.directory(System.getProperty("ser.dir"));
	}

	public void testDirectoryXML() {
		CheckDirectoryAccessible c = new CheckDirectoryAccessible();
		c.setPath("ser.dir");
		SelfDiagnose.register(c);
	}

	public void testDirectoryValid() {
		Check.directory(System.getProperty("user.dir"));
	}

	public void testVarResolving() throws DiagnoseException {
		ExecutionContext ctx = new ExecutionContext();
		ctx.setValue("myvar", "myvarvalue");
		assertEquals("myvarvalue", ctx.resolveValue("${myvar}"));
		
	}
	
	public void testDatasource() {
	    String name = Check.dataSourceName("somename");
	    assertEquals(name, "somename");
	}
	public void testFileContains(){
	    assertEquals(Check.fileContainsString("file:///here.txt", "cafebabe"),"cafebabe");
	}
	public void testJNDI(){
	    Check.jndiBinding("url","ctx","jndi","Class");
	}
}
