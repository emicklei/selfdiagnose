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
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.check.CheckClassLoadable;

public class DiagnoseUtilTest extends TestCase {
    public void testReport() {
        CheckClassLoadable task = new CheckClassLoadable();
        DiagnosticTaskResult result = task.createResult();
        assertTrue(result.getMessage().length() == 0);
        System.out.println(result.getMessage());
    }

    public void testGetter() {
        assertEquals("getMyProperty", DiagnoseUtil.getPropertyAccessMethodName("myProperty"));
    }

    public void testPerformThis() throws Exception {
        String me = "me";
        assertEquals(me, DiagnoseUtil.perform(me, "this"));
    }
}
