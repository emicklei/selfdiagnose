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
package com.philemonworks.selfdiagnose.check;

import java.io.File;
import java.io.IOException;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckDirectoryAccessible is a DiagnosticTask that verifies access properties of a directory in a File System
 * <p/>
 * <pre>
&lt;checkdirectoryaccessible path="/home/work" /&gt;
&lt;checkdirectoryaccessible path="/home/work" writeable="true" /&gt; 
 * </pre> 
 * Stores the directory (java.io.File) into the (optional) specified variable.
 * @author E.M.Micklei
 */
public class CheckDirectoryAccessible extends DiagnosticTask {
    private static final long serialVersionUID = 5489454256848022802L;

    private static final String PARAMETER_PATH = "path";

	private String path;

	private boolean mustBeWriteable;

	public void initializeFromAttributes(Attributes attributes) {
		// variable
		super.initializeFromAttributes(attributes);
		this.setPath(attributes.getValue(PARAMETER_PATH));
		this.setMustBeWriteable("true".equals(attributes.getValue("writeable")));
	}

	/**
	 * Return the description of this task.
	 */
	public String getDescription() {
		String desc = "Check that this path exists and is readable";
		if (mustBeWriteable)
			desc += " and is writeable";
		return desc;
	}

	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_PATH, path, CheckDirectoryAccessible.class);
	}

	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
	    String resolvedPath = ctx.resolveString(path);
		File dir = new File(resolvedPath);
		ctx.setValue(this.getVariableName(), dir);
		if (!dir.isDirectory()) {
			result.setFailedMessage("[" + resolvedPath + "] is not a resolvedPath or is not reachable");
			return;
		}
		if (!dir.exists()) {
			result.setFailedMessage("Directory [" + resolvedPath + "] does not exist");
			return;
		}
		if (!dir.canRead()) {
			result.setFailedMessage("Directory [" + resolvedPath + "] is not readable");
		}
		if (mustBeWriteable) {
			try {
				File writetest = File.createTempFile("temp_", this.getClass().getName(), dir);
				writetest.delete();
			} catch (IOException ex) {
				result.setFailedMessage("Directory [" + resolvedPath + "] is not writeable");
				return;
			}
		}
		String msg = "Directory [" + resolvedPath + "] is readable";
		if (mustBeWriteable)
			msg += " and writeable";
		result.setPassedMessage(msg);
	}

	/**
	 * @return String the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *        String
	 */
	public void setPath(String directory) {
		this.path = directory;
	}

	/**
	 * @return boolean mustBeWriteabl
	 */
	public boolean isMustBeWriteable() {
		return mustBeWriteable;
	}

	/**
	 * @param writeable
	 *        true if it must be writeable (i.e. a file can be created).
	 */
	public void setMustBeWriteable(boolean writeable) {
		this.mustBeWriteable = writeable;
	}
}
