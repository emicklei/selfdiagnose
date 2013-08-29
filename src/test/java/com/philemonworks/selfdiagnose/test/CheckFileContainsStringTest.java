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

import com.philemonworks.selfdiagnose.check.CheckFileContainsString;
import com.philemonworks.selfdiagnose.check.CheckResourceAccessible;

public class CheckFileContainsStringTest extends BasicDiagnosticTaskTest {
	public void testNormal(){
		CheckFileContainsString task = new CheckFileContainsString();
		task.setName("checkfilecontainsstringtest.txt");
		task.setString("Ruby");
		this.run(task);
	}
	public void testWithURL(){
		CheckResourceAccessible cra = new CheckResourceAccessible();
		cra.setVariableName("theurl");
		cra.setName("checkfilecontainsstringtest.txt");
		this.run(cra);
		
		CheckFileContainsString task = new CheckFileContainsString();
		task.setUrl("${theurl}");
		task.setString("Ruby");
		this.run(task);		
	}
}
