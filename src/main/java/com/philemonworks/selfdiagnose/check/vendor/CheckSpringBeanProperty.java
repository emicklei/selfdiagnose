package com.philemonworks.selfdiagnose.check.vendor;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnoseUtil;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.PatternMatchingTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.Attributes;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

/**
 * CheckSpringBeanProperty is a task to check a property from any Spring configured bean object. Can use the result of an operation or the Spring bean itself too.
 *
 * <pre>
 *     &lt;checkspringbeanproperty id="myBean" property="someField" /&gt;
 *     &lt;checkspringbeanproperty id="myBean" operation="someMethod" /&gt;
 *     &lt;checkspringbeanproperty name="myBean" property="someField" pattern="[a-z-0-9]*" /&gt;
 *
 *     &lt;-- make the bean available by variable "myBeanVar" to the execution context of SelfDiagnose --&gt;
 *     &lt;checkspringbeanproperty id="myBean" var="myBeanVar" /&gt;
 * </pre>
 *
 * @author ernestmicklei
 */
public class CheckSpringBeanProperty extends PatternMatchingTask implements ApplicationContextAware {

    private static final long serialVersionUID = -8712795258282102071L;

    /**
     * Attribute specifying the Spring bean id. Either &quot;id&quot; or &quot;name&quot; is required.
     */
    protected static final String PARAMETER_ID = "id";
    /**
     * Attribute specifying the Spring bean name. Either &quot;id&quot; or &quot;name&quot; is required.
     */
    protected static final String PARAMETER_NAME = "name";
    /**
     * Attribute specifying the property on the Spring bean to resolve, if any. Takes precedence over &quot;operation&quot;.
     */
    protected static final String PARAMETER_PROPERTY = "property";
    /**
     * Attribute specifying the operation on the Spring bean, whose result is matched against the pattern. Either &quot;property&quot; or &quot;operation&quot; is required.
     */
    protected static final String PARAMETER_OPERATION = "operation";

    private String id;
    private String name;
    private String property;
    private String operation;

    // For accessing Spring beans by name or id
    private ApplicationContext applicationContext;

    @Override
    public String getDescription() {
        return "Check the property or method result of a bean created by the Spring Framework";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
     */
    @Override
    public void initializeFromAttributes(Attributes attributes) {
        // store variable if specified
        super.initializeFromAttributes(attributes);
        this.setId(attributes.getValue(PARAMETER_ID));
        this.setName(attributes.getValue(PARAMETER_NAME));
        this.setProperty(attributes.getValue(PARAMETER_PROPERTY));
        this.setOperation(attributes.getValue(PARAMETER_OPERATION));
    }

    @Override
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        if (id == null) {
            DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, name, this.getClass());
        } else {
            DiagnoseUtil.verifyNonEmptyString(PARAMETER_ID, id, this.getClass());
        }
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result)
            throws DiagnoseException {
        if (this.applicationContext == null) {
            result.setErrorMessage("No Spring ApplicationContext available");
            return;
        }
        Object bean = null;
        String beanName = "<not specified>";
        if (id != null) {
            bean = this.applicationContext.getBean(id);
            beanName = id;
        } else if (name != null) {
            bean = this.applicationContext.getBean(name);
            beanName = name;
        }
        if (null == bean) {
            result.setFailedMessage(DiagnoseUtil.format(
                    "Spring bean [{1}] not found", beanName));
            return;
        }

        String accessKind;
        String accessName;
        if (operation == null) {
            accessKind = "property";
            accessName = property;
        } else {
            accessKind = "operation";
            accessName = operation;
        }

        Object value = resolveValue(bean);
        super.checkValueAgainstPattern(result, beanName, accessKind, accessName, value);
        ctx.setValue(this.getVariableName(), value);
    }

    private Object resolveValue(Object bean) throws DiagnoseException {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            if (operation != null) {
                return executeMethod(beanInfo, bean, operation);
            } else if (property != null) {
                return resolveProperty(beanInfo, bean, property);
            } else {
                return bean;
            }
        } catch (IntrospectionException e) {
            throw new DiagnoseException("Failed to introspect bean.", e);
        }

    }

    private Object executeMethod(BeanInfo beanInfo, Object bean, String methodName) throws DiagnoseException {

        MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
        for (MethodDescriptor methodDescriptor : methodDescriptors) {

            if (methodDescriptor.getName().equals(methodName)) {
                return DiagnoseUtil.perform(bean, methodDescriptor.getMethod().getName());
            }
        }

        throw new DiagnoseException("Unknown method for JavaBean: " + methodName);
    }

    private Object resolveProperty(BeanInfo beanInfo, Object bean, String propertyName) throws DiagnoseException {

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            if (propertyDescriptor.getName().equals(propertyName)) {
                return DiagnoseUtil.perform(bean, propertyDescriptor.getReadMethod().getName());
            }
        }

        throw new DiagnoseException("Unknown property for JavaBean: " + propertyName);
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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
