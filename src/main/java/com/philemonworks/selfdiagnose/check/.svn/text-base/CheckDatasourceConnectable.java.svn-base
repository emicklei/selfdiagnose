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

import java.sql.Connection;
import java.sql.SQLException;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckDatasourceConnectable is a DiagnosticTask that verifies the availability
 * and ability to create a connection to a DataSource. 
 * <p/>
 * <pre>
 &lt;checkdatasourceconnectable name="jdbc/demo" /&gt;
 * </pre> 
 * @author emicklei
 */
public class CheckDatasourceConnectable extends DiagnosticTask {
    private static final long serialVersionUID = 1493998427707118229L;
    private static final String PARAMETER_NAME = "name";
	private String datasourceName;

	public void initializeFromAttributes(Attributes attributes) {
		this.setDatasourceName(attributes.getValue(PARAMETER_NAME));
		super.initializeFromAttributes(attributes);
	}

	public String getDescription() {
		return "Check that a datasource is available in the naming server and a connection can be created.";
	}	
	
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(
				PARAMETER_NAME, datasourceName,
				CheckDatasourceConnectable.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#run()
	 */
	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		try {
			Connection con = DiagnoseUtil.getDataSourceConnection(datasourceName);
			if (con != null)
				con.close();
		} catch (SQLException e) {
			throw new DiagnoseException(
					DiagnoseUtil.format(
					"Unable to connect to [{0}]",
					datasourceName), e);
		}
		result.setPassedMessage(DiagnoseUtil.format(
				"Datasource [{0}] is found and a connection can be created.",datasourceName));
	}

	/**
	 * @return String
	 */
	public String getDatasourceName() {
		return datasourceName;
	}

	/**
	 * @param string
	 */
	public void setDatasourceName(String string) {
		datasourceName = string;
	}
}
