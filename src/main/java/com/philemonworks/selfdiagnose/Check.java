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

import com.philemonworks.selfdiagnose.check.CheckClassLoadable;
import com.philemonworks.selfdiagnose.check.CheckDatasourceConnectable;
import com.philemonworks.selfdiagnose.check.CheckDirectoryAccessible;
import com.philemonworks.selfdiagnose.check.CheckFileContainsString;
import com.philemonworks.selfdiagnose.check.CheckJNDIBinding;
import com.philemonworks.selfdiagnose.check.CheckResourceAccessible;
import com.philemonworks.selfdiagnose.check.CheckResourceBundleKey;
import com.philemonworks.selfdiagnose.check.CheckResourceProperty;
import com.philemonworks.selfdiagnose.check.CheckURLReachable;
import com.philemonworks.selfdiagnose.check.CheckValidURL;
/** 
 * Check is a Factory to the creation of all DiagnosticTasks of type "Check". 
 *
 * @author E.M.Micklei
 */
public class Check {
	private Check(){} /* prevent creating an instance */
	
	/**
	 * @param className
	 * @return String className
	 */
	public static String classLoadable(String className) {
		CheckClassLoadable task = new CheckClassLoadable();
		task.setClassName(className);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return className;
	}	
	
	/**
	 * @param dataSourceName
	 * @return String dataSourceName
	 */
	public static String dataSourceName(String dataSourceName) {
		CheckDatasourceConnectable task = new CheckDatasourceConnectable();
		task.setDatasourceName(dataSourceName);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return dataSourceName;
	}

	/**
	 * @param pathName
	 * @return String pathName
	 */
	public static String directory(String pathName) {
		return Check.directory(pathName, true);
	}
	/**
	 * @param pathName
	 * @param mustBeWriteable
	 * @return String pathName
	 */
	public static String directory(String pathName, boolean mustBeWriteable) {
		CheckDirectoryAccessible task = new CheckDirectoryAccessible();
		task.setPath(pathName);
		task.setMustBeWriteable(mustBeWriteable);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return pathName;
	}
	/**
	 * @param fileName
	 * @param substring
	 * @return String substring
	 */
	public static String fileContainsString(String fileName, String substring) {
		CheckFileContainsString task = new CheckFileContainsString();
		task.setName(fileName);
		task.setString(substring);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return substring;
	}
	/**
	 * Register a task that checks that naming server has a binding with the correct type. Task parameters are not
	 * validated until the task is running.
	 * 
	 * @param namingServerURL
	 *        String
	 * @param initalContextFactory
	 *        String
	 * @param jndiName
	 *        String
	 * @param valueClassName
	 *        String
	 * @return String the jndiName parameter
	 */
	public static String jndiBinding(String namingServerURL, String initalContextFactory, String jndiName, String valueClassName) {
		CheckJNDIBinding task = new CheckJNDIBinding();
		task.setNamingServerURL(namingServerURL);
		task.setInitalContextFactory(initalContextFactory);
		task.setJndiName(jndiName);
		task.setValueClassName(valueClassName);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return jndiName;
	}
	/**
	 * @param key
	 * @return String key
	 */
	public static String nlsKey(String key) {
		CheckResourceBundleKey task = new CheckResourceBundleKey();
		task.setName("com.philemonworks.util.NLS");
		task.setKey(key);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return key;
	}
	/**
	 * @param propertiesResourceName
	 * @param key
	 * @return String key
	 */
	public static String property(String propertiesResourceName, String key) {
		CheckResourceProperty task = new CheckResourceProperty();
		task.setName(propertiesResourceName);
		task.setProperty(key);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return key;
	}
	/**
	 * @param resourceName
	 * @param key
	 * @return String key
	 */
	public static String propertyIsEmailAddress(String resourceName, String key){
		return Check.propertyMatches(resourceName,key,"[0-9a-zA-Z]@[0-9a-zA-Z].[a-zA-Z]"); // todo: figure out the email regex
	}
	/**
	 * Register a task that checks that the value of the property at this key matches a pattern. Task parameters are not
	 * validated until the task is running.
	 * 
	 * @param resourceName
	 *        String
	 * @param key
	 *        String
	 * @param pattern
	 *        String
	 * @return String the key parameter
	 */
	public static String propertyMatches(String resourceName, String key, String pattern) {
		CheckResourceProperty task = new CheckResourceProperty();
		task.setName(resourceName);
		task.setProperty(key);
		task.setPattern(pattern);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return key;
	}
	/**
	 * @param name
	 * @param key
	 * @return String name
	 */
	public static String resourceBundleKey(String name, String key) {
		CheckResourceBundleKey task = new CheckResourceBundleKey();
		task.setName(name);
		task.setKey(key);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return name;
	}	
	/**
	 * @param resourceName
	 * @return String resourceName
	 */
	public static String resourceName(String resourceName) {
		CheckResourceAccessible task = new CheckResourceAccessible();
		task.setName(resourceName);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return resourceName;
	}
	/**
	 * @param urlSpec
	 * @return String urlSpec
	 */
	public static String url(String urlSpec) {
		CheckURLReachable task = new CheckURLReachable();
		task.setUrl(urlSpec);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return urlSpec;
	}
	/**
	 * Register a task that checks that the url is syntactically correct. Task parameters are not validated until the
	 * task is running.
	 * 
	 * @param urlSpec
	 * @return String urlSpec
	 */
	public static String validURL(String urlSpec) {
		CheckValidURL task = new CheckValidURL();
		task.setUrl(urlSpec);
		SelfDiagnose.register(task, DiagnoseUtil.detectRequestorClass().getName());
		return urlSpec;
	}
}
