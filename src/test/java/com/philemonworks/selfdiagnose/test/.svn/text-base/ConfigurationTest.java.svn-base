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

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import com.philemonworks.selfdiagnose.CollectionIteratorTask;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.SelfDiagnose;
import com.philemonworks.selfdiagnose.SelfDiagnoseHandler;
import com.philemonworks.selfdiagnose.check.CheckClassLoadable;

public class ConfigurationTest extends TestCase {
	public void doReadConfig(String xmlresource) {
	    SelfDiagnose.flush();
		SelfDiagnoseHandler s = new SelfDiagnoseHandler();
		try {
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlresource);
			if (is.available() == 0) throw new RuntimeException("missing test resource");
			p.parse(is, s);
		} catch (Exception e) {
			e.printStackTrace();
			fail("config load failed:" + xmlresource);
		}
		SelfDiagnose.run();
	}
	public void testReadAll(){
	    doReadConfig("selfdiagnose-test.xml");
	}
    public void testReadMissing(){
        // doReadConfig("selfdiagnose-missing.xml");
    }	
    public void testReadIterator(){       
        doReadConfig("selfdiagnose-iterator.xml");
        CollectionIteratorTask task = (CollectionIteratorTask)SelfDiagnose.getTasks().get(0);
        assertEquals(task.getTasks().size(), 1);
        DiagnosticTask first = (DiagnosticTask)task.getTasks().get(0);
        assertTrue(first.getTaskName().startsWith("checkvaluematches"));
        
    }
	public void testNoConfig(){
		SelfDiagnose.configure("noconfig.xml");
	}
	
	public void testRegister(){
		CheckClassLoadable ccl = new CheckClassLoadable();
		SelfDiagnose.register(ccl);
	}
	
	public void testAddBinding() {
	    SelfDiagnoseHandler.addBindingFor(CheckClassLoadable.class);
	}
}
