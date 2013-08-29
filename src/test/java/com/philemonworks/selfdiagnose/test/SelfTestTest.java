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
import junit.framework.TestResult;

import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.SelfTest;
/**
 * Class SelfTestTest tests the selftest testcase class. (get it?)
 * 
 * @author E.M.Micklei
 *
 */
public class SelfTestTest extends TestCase {
	
	public void testMySelf(){
		SelfDiagnose.configure("selfdiagnose-test.xml");
		SelfTest selfTest = new SelfTest();
		selfTest.setName("testConfiguration");
		TestResult run = selfTest.run();
        assertTrue(run.wasSuccessful());
		SelfDiagnose.run();
	}
}
