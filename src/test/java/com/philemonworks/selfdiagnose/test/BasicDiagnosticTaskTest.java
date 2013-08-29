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

import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

public abstract class BasicDiagnosticTaskTest extends TestCase {  
	public ExecutionContext ctx;
	
	public void setUp(){
		ctx = new ExecutionContext();
	}
	
	public DiagnosticTaskResult run(DiagnosticTask task){
		System.out.println("Testing:"+(DiagnoseUtil.shortName(this.getClass()))+">>" + this.getName()+" ------------------------");
		task.setRequestor(this.getClass().getName());
		DiagnosticTaskResult result = task.run(ctx);
		if (result.isError()){
			result.logReport();
		} else {
			System.out.println(result.getMessage());
		}
		return result;
	}
}
