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
import com.philemonworks.selfdiagnose.check.CheckClassLoadable;

public class MyOtherApplication {
	// These are the dependencies (other that the classes listed by imports)
	static {
		// explicity add new tasks
		CheckClassLoadable task = new CheckClassLoadable();
		task.setClassName("java.net.URLS");
		SelfDiagnose.register(task);
		// multiple keys
		Check.fileContainsString("data.xml", "\"object\"");
		Check.fileContainsString("data.xml", "any");
	}

	public static void main(String[] args) {
		SelfDiagnose.run();
	}
}
