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

import java.net.URL;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.check.CheckURLReachable;

public class CheckURLReachableTest extends BasicDiagnosticTaskTest {
	public void testURLString(){
		CheckURLReachable task = new CheckURLReachable();
		task.setUrl("http://www.philemonworks.com");
		this.run(task);
	}
    public void testGoogle(){
        CheckURLReachable task = new CheckURLReachable();
        task.setUrl("http://www.google.com");
        task.setPattern(".*");
        DiagnosticTaskResult result = this.run(task);
        assertTrue(result.isPassed());
    }	
	public void testURLInContext() throws Exception {
		CheckURLReachable task = new CheckURLReachable();
		ctx.setValue("theurl", new URL("http://www.philemonworks.com"));
		task.setUrl("${theurl}");
		this.run(task);
	}	
}
