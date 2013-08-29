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

import java.util.Iterator;

import junit.framework.TestCase;
/**
 * SelfTest is a Unit testcase that can verify the diagnostic tasks
 * specified using an XML configuration. Projects may include this
 * class in their TestSuite
 * 
 * @author E.M.Micklei
 */
public class SelfTest extends TestCase {
	
	public void testConfiguration(){
		SelfDiagnose.configure("selfdiagnose-test.xml");
		Iterator it = SelfDiagnose.getTasks().iterator();
		boolean failed = false;
		while (it.hasNext()){
			DiagnosticTask each = (DiagnosticTask)it.next();
			try {
				each.setUp(new ExecutionContext());
			} catch (DiagnoseException e) {
				failed = true;
				System.err.println(e.toString());
			}
		}
		if (failed) fail("One or more diagnostic tasks have configuration problems, see System.err");
	}
}
