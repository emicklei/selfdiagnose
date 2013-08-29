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
package com.philemonworks.selfdiagnose.test.xtra;

import com.philemonworks.selfdiagnose.Check;
import com.philemonworks.selfdiagnose.SelfDiagnose;

public class MyApplication {
	// These are the dependencies (other that the classes listed by imports)
	static {
		Check.resourceName("some.properties");
		Check.resourceName("missing.properties");
		Check.resourceName("empty.properties");
		Check.property("some.properties","mykey");
		Check.property("some.properties","missingkey");
		Check.classLoadable("java.util.Vector");		
		Check.classLoadable("java.util.Vectors");
		Check.classLoadable("com.philemonworks.selfdiagnose.test.MyOtherApplication");
	}
	public static void main(String[] args){
		SelfDiagnose.run();
	}
}
