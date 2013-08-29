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

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.check.CheckBeanProperty;
import com.philemonworks.selfdiagnose.check.CheckSystemProperty;

public class CheckReportSystemPropertyTest  extends BasicDiagnosticTaskTest {

    public void testNormal() throws DiagnoseException {
    	CheckSystemProperty prop = new CheckSystemProperty();
    	prop.setProperty("java.class.path");
    	assertTrue(this.run(prop).isPassed());    
    }
    
    public void testChained() throws DiagnoseException {
        CheckSystemProperty prop = new CheckSystemProperty();
    	prop.setProperty("java.class.path");
    	prop.setVariableName("classpath");
    	assertTrue(this.run(prop).isPassed());
    	
    	CheckBeanProperty cb = new CheckBeanProperty();
    	cb.setBean("${classpath}");
    	cb.setPattern(".*\\p-3\\p.8\\p.1");
//    	assertTrue(this.run(cb).isPassed());
    }    
    
}
