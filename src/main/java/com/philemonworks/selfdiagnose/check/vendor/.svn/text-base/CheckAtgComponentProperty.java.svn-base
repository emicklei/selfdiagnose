/*
 Copyright 2008 Ernest Micklei @ PhilemonWorks.com

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
package com.philemonworks.selfdiagnose.check.vendor;

import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckProperty;

/**
 * CheckAtgComponentProperty is a task specific to the ATG framework.
 * This task can access a component and its properties to match against an expected pattern.
 * 
 * <pre>
 * &lt;checkatgcomponentproperty component="/atg/dynamo/Configuration" property="siteHttpServerName"/&gt;
 * </pre>
 * 
 * @author ernestmicklei
 */
public class CheckAtgComponentProperty extends CheckProperty {
    private static final long serialVersionUID = 4171798048549804812L;

    private String componentName;


    public String getDescription() {
        return "Access a property of an ATG component";
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
     */
    public void initializeFromAttributes(Attributes attributes) {
        super.initializeFromAttributes(attributes);
        this.setComponentName(attributes.getValue("component"));
    }
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        DiagnoseUtil.verifyNonEmptyString("component", componentName, CheckAtgComponentProperty.class);
    }
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        // emulate Nucleus.getGlobalNucleus().resolveName()
        Class nucleus = null;
        try {
            nucleus = DiagnoseUtil.classForName("atg.nucleus.Nucleus");
        } catch(ClassNotFoundException e) {
            throw new DiagnoseException("ATG Nucleus not available");
        }
        Object globarNucleus = DiagnoseUtil.perform(nucleus, "getGlobalNucleus", new Object[0]);
        if (globarNucleus == null) {
            throw new DiagnoseException("Unable to access getGlobalNucleus from ATG Nucleus");
        }
        Object receiver = DiagnoseUtil.perform(globarNucleus, "resolveName", new String[] { componentName });
        if(receiver == null) {
            throw new DiagnoseException("No such component [" + getComponentName() + "]");
        }
        Object value = null;
        try {
            value = DiagnoseUtil.perform(receiver, DiagnoseUtil.getPropertyAccessMethodName(this
                .getProperty()), new Object[0]);
        } catch(Exception ex) {
            throw new RuntimeException("Unable to get property [" + getProperty() + "] in component ["
                    + getComponentName() + "]", ex);
        }
        if(value == null)
            throw new DiagnoseException("No such property [" + getProperty() + "] in component ["
                    + getComponentName() + "]");
        // make available in the context if needed
        ctx.setValue(this.getVariableName(), value);
        String stringValue = "";
        if (!(value instanceof String)) {
            stringValue = String.valueOf(value);
          } else {
            stringValue = (String) value;
          }
        if (this.getPattern() != null) {
            // test against given pattern but first make sure it is a String            
            if (stringValue.matches(this.getPattern()))
              result.setPassedMessage(DiagnoseUtil.format(
                  "Atg component.property [{1}] with value [{0}] matches [{2}]", 
                  stringValue, getComponentName() + "." + this.getProperty(), this.getPattern()));
            else
              result.setFailedMessage(DiagnoseUtil.format(
                  "Atg component.property [{1}] with value [{0}] does not match with [{2}]", 
                  stringValue, getComponentName() + "." + this.getProperty(), this.getPattern()));
          } else {        
              result.setPassedMessage(DiagnoseUtil.format(
                      "Atg component.property [{1}] has value [{0}]", 
                      stringValue, getComponentName() + "." + this.getProperty()));
          }
    }
    public String getComponentName() {
        return componentName;
    }
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
