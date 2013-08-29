/*
    Copyright 2006-2011 Ernest Micklei @ PhilemonWorks.com

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
package com.philemonworks.selfdiagnose;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CompositeDiagnosticTaskResult is
 *
 * @author E.M.Micklei
 */
public class CompositeDiagnosticTaskResult extends DiagnosticTaskResult {
	
	private List results = new ArrayList();
	public boolean includeCompositeResult = true;
	/**
	 * @param task
	 */
	public CompositeDiagnosticTaskResult(DiagnosticTask task) {
		super(task);		
	}
	public void addResult(DiagnosticTaskResult result){
		results.add(result);
	}
	public List getResults(){
		return results;
	}
	public int howManyErrors(){
		int sum=0;
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			DiagnosticTaskResult element = (DiagnosticTaskResult) iter.next();
			if (!element.isPassed()) sum++;			
		}
		return sum;
	}
	/**
	 * Add all results to the list.
	 */
    public void addToResults(List newResults) {
    	if (includeCompositeResult) newResults.add(this);
        // add all or mine
        newResults.addAll(results);  
    }	
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(super.toString());
    	for (int i=0;i<results.size();i++) {
    		sb.append("\n\t");
    		sb.append(results.get(i));
    	}
    	return sb.toString();
    }
}
