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

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.check.CheckDatasourceConnectable;

/**
 * CheckJ2EETest is a Unit tests for tasks that require the J2EE lib to run
 * 
 * @author E.M.Micklei
 */
public class CheckJ2EETest extends TestCase {

	public void testDatasource() {
		CheckDatasourceConnectable task = new CheckDatasourceConnectable();
		task.setRequestor("CheckTest");
		task.setDatasourceName("jdbc/active");
		DiagnosticTaskResult result = task.run();
		if (!result.isFailed())
			fail("testDatasource");
	}
}
