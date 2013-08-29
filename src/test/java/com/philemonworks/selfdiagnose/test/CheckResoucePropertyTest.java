/*
    Copyright 2006,2008 Ernest Micklei @ PhilemonWorks.com

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

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.check.CheckResourceProperty;

public class CheckResoucePropertyTest extends BasicDiagnosticTaskTest {

    public void testNormal() throws DiagnoseException {
    	CheckResourceProperty prop = new CheckResourceProperty();
    	prop.setName("some.properties");
    	prop.setProperty("mykey");
    	prop.setPattern("my.*");
    	prop.setVariableName("var");
    	this.run(prop);    			
    	assertEquals("myvalue", ctx.resolveString("${var}"));
    }
    
    public void testMissing() throws DiagnoseException {
        CheckResourceProperty prop = new CheckResourceProperty();
        prop.setName("some.properties");
        prop.setProperty("nokey");
        DiagnosticTaskResult result = this.run(prop);             
        assertTrue(result.isFailed());
    }    
}

