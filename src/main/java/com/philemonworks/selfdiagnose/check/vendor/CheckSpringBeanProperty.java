package com.philemonworks.selfdiagnose.check.vendor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.Attributes;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.check.CheckProperty;
/**
 * CheckSpringBeanProperty is a task to check a property from any Spring configured bean object.
 * 
 * @author ernestmicklei
 *
 * <pre>
 * 	&lt;checkspringbeanproperty id="myBean" property="someField" /&gt;
 * 	&lt;checkspringbeanproperty id="myBean" operation="someMethod" /&gt;
 * 	&lt;checkspringbeanproperty name="myBean" property="someField" pattern="[a-z-0-9]*" /&gt;
 * 
 *  &lt;-- make the bean available by variable "myBeanVar" to the execution context of SelfDiagnose --&gt;
 * 	&lt;checkspringbeanproperty id="myBean" var="myBeanVar" /&gt;
 * </pre>
 */
public class CheckSpringBeanProperty extends CheckProperty implements ApplicationContextAware {
	private static final long serialVersionUID = 4820513792695319932L;
	protected static final String PARAMETER_NAME = "name";
	protected static final String PARAMETER_OPERATION = "operation";
	protected static final String PARAMETER_ID = "id";
	
	private String id;
	private String name;
	private String operation;
	
	// For accessing Spring beans by name or id
	private ApplicationContext applicationContext;
	
	public String getDescription() {
		return "Check the property or method result of a bean created by the Spring Framework";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
	 */
	public void initializeFromAttributes(Attributes attributes) {
		// store variable if specified
		super.initializeFromAttributes(attributes);
		this.setId(attributes.getValue(PARAMETER_ID));
		this.setName(attributes.getValue(PARAMETER_NAME));	
		this.setOperation(attributes.getValue(PARAMETER_OPERATION));
	}	
	
	public void setUp(ExecutionContext ctx) throws DiagnoseException {
		super.setUp(ctx);
		if (id == null) {
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name,this.getClass());	
		} else {
			DiagnoseUtil.verifyNonEmptyString(PARAMETER_ID, id,this.getClass());
		}		
	}

	public void run(ExecutionContext ctx, DiagnosticTaskResult result)
			throws DiagnoseException {
		if (this.applicationContext == null) {
		    result.setErrorMessage("No Spring ApplicationContext available");
		    return; 
		}
		Object bean = null;
		String beanName = "";
		if (id != null) {
			bean = this.applicationContext.getBean(id);
			beanName = id;
		} else if (name != null) {
			bean = this.applicationContext.getBean(name);
			beanName = name;
		}
		if (null == bean) {
			result.setFailedMessage(DiagnoseUtil.format(
					"Spring bean [{1}] not found", (id == null ? name : id )));
			return;
		}		
		Object value = DiagnoseUtil.perform(bean,this.methodName());
		if (pattern != null) {
			String stringValue = (String)value;
			if (stringValue.matches(this.getPattern()))
				result.setPassedMessage(DiagnoseUtil.format(
					"Spring bean [{1}] method [{0} = {2}]", 
					stringValue, this.methodName(), this.getPattern(), beanName));
			else
				result.setFailedMessage(DiagnoseUtil.format(
					"Method [{1}] of Spring bean [{3}] with value [{0}] does not match with [{2}]", 
					stringValue, this.methodName(), this.getPattern(), beanName));
		} else {
			result.setPassedMessage(DiagnoseUtil.format(
					"Spring bean [{1}] method [{0} = {2}]",
					this.methodName(), beanName, value.toString() ));
		}
		ctx.setValue(this.getVariableName(), value);
	}
	private String methodName(){
	    return this.operation == null 
	        ? DiagnoseUtil.getPropertyAccessMethodName(this.getProperty())
	        : this.operation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }    
}
