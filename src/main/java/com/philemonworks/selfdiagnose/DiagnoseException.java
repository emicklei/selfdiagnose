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
package com.philemonworks.selfdiagnose;

/**
 * DiagnoseException is a checked exception for notifying failed tasks.
 * 
 * @author emicklei
 */
public class DiagnoseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3258409530248213557L;

	/**
	 * 
	 */
	public DiagnoseException() {
		super();
	}

	/**
	 * @param message
	 */
	public DiagnoseException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DiagnoseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DiagnoseException(Throwable cause) {
		super(cause);
	}

}
