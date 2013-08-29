/*
    Copyright 2006-2010 Ernest Micklei @ PhilemonWorks.com

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

/**
 * CheckDatabaseTableExists is a DiagnosticTask that verifies whether a database
 * schema has a definition for a table. This task requires a datasource.
 * On default, yt uses the basic COUNT(*) query to detect table presence. 
 * If found then the number of records is reported. 
 * <p/>
 * <pre>
&lt;checkdatabasetableexists name="BUZ.CUSTOMERS" datasource="BusinessDs" /&gt;
 * </pre>
 * If the count(*) implies a heavy load then alternatively you can specifiy a different query name.
 * Possible values are: IN_ALL_TABLES (Oracle), FETCH_FIRST (ANSI), COUNT (default)
 * <pre>
&lt;checkdatabasetableexists name="BUZ.CUSTOMERS" datasource="BusinessDs" queryname="IN_ALL_TABLES" /&gt;
 * </pre>
 * @author ernest.mickei@philemonworks.com
 */
public class CheckDatabaseTableExists extends com.philemonworks.selfdiagnose.DiagnosticTask {
	private static final long serialVersionUID = 3171806700398661642L;
	private static final String PARAMETER_DATASOURCE = "datasource";
	private static final String PARAMETER_NAME = "name";	
	private static final String PARAMETER_QUERY = "queryname";
	private static final String[] PARAMETER_QUERY_SORTED_VALUES = new String[]{"IN_ALL_TABLES","COUNT","FETCH_FIRST"};
	// http://en.wikipedia.org/wiki/Select_%28SQL%29
	private static final String QUERY_FETCH_FIRST = "SELECT COUNT(*) FROM {0} FETCH FIRST 1 ROWS ONLY";
	private static final String QUERY_COUNT = "SELECT COUNT(*) FROM {0}";
    private static final String QUERY_ALL_TABLES = "SELECT COUNT(*) FROM all_tables WHERE table_name = ''{0}''"; 
	private String tableName = null;
	private String datasource;
	private String queryTemplate = QUERY_COUNT;

	/**
	 * Return the description of this task.
	 */
	public String getDescription() {
		return "Check that a table is defined in a database.";
	}	
	
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, tableName, CheckDatabaseTableExists.class);		
		DiagnoseUtil.verifyNonEmptyString(PARAMETER_DATASOURCE, datasource, CheckDatabaseTableExists.class);
		DiagnoseUtil.verifyNotNull("queryTemplate", queryTemplate, CheckDatabaseTableExists.class);
		int index = Arrays.binarySearch(PARAMETER_QUERY_SORTED_VALUES, queryTemplate);
	    if (index == 0) {
	        throw new DiagnoseException("CheckDatabaseTableExists parameter [" + PARAMETER_QUERY + "] has an invalid value. Must be one of "
	                + PARAMETER_QUERY_SORTED_VALUES);
	    }
	}

	public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
		Connection con = null;
		try {
			con = DiagnoseUtil.getDataSourceConnection(datasource);
			PreparedStatement stmt = con.prepareStatement(this.getQuery());
			this.runStatement(result, stmt);
		} catch (SQLException ex) {
			result.setErrorMessage(DiagnoseUtil.format(
				"Error in detecting table exists via datasource [{1}] because: {2}",
				datasource, DiagnoseUtil.getErrorMessage(ex)));
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {// eat it
					Logger.getLogger(this.getClass()).warn("Exception during connection close:"+ex.getMessage());
				}
			}
		}		
	}

	private void runStatement(DiagnosticTaskResult result, PreparedStatement stmt){
		boolean exists = false;
		try {
			ResultSet rst = stmt.executeQuery();
			exists = rst.next();
			if (exists) {
				result.setPassedMessage(DiagnoseUtil.format(
						"Table [{0}] exists via datasource [{1}]",tableName,datasource));
			} else {
			    result.setPassedMessage(DiagnoseUtil.format(
                        "Table [{0}] exists via datasource [{1}] but is empty",tableName,datasource));
			}
		} catch (SQLException ex) { 
			// exception when running query => assume no table (could be no read rights)
			exists = false;
		}
		if (!exists) {
			result.setFailedMessage(DiagnoseUtil.format(
						"No such table [{0}] exists via datasource [{1}]",
						tableName,datasource));
		}
	}
	
	public void initializeFromAttributes(Attributes attributes) {
		this.setTableName(attributes.getValue(PARAMETER_NAME));
		this.setDatasource(attributes.getValue(PARAMETER_DATASOURCE));
		String queryNameOrNull = attributes.getValue(PARAMETER_QUERY);
		if (queryNameOrNull != null)
		    if ("IN_ALL_TABLES".equals(queryNameOrNull))
		        this.setQueryTemplate(QUERY_ALL_TABLES);
		    else if ("COUNT".equals(queryNameOrNull))
                this.setQueryTemplate(QUERY_COUNT);
            else if ("FETCH_FIRST".equals(queryNameOrNull))
                this.setQueryTemplate(QUERY_FETCH_FIRST);
		super.initializeFromAttributes(attributes);
	}

	public String getQuery(){	  
		return DiagnoseUtil.format(queryTemplate,tableName);
	}
	
	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    public String getQueryTemplate() {
        return queryTemplate;
    }

    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }
}
