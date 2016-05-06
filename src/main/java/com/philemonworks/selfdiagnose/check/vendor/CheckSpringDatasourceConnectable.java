package com.philemonworks.selfdiagnose.check.vendor;

import com.philemonworks.selfdiagnose.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.Attributes;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * CheckSpringDatasourceConnectable is a diagnostic task that access a Datasource
 * from the application context and uses that to verify that a connection can be created (and closed).
 * 
 * &lt;checkspringdatasourceconnectable name="" /&gt;
 *
 *  &lt;-- Only do the check if the datasource is in a certain profile. Can be multiple profiles comma separated (using OR principle). --&gt;
 * 	&lt;checkspringdatasourceconnectable name="myBean" profile="nameOfTheProfile" /&gt;
 *
 * @author emicklei
 *
 */
public class CheckSpringDatasourceConnectable extends DiagnosticTask implements ApplicationContextAware {
    private final static Logger log = Logger.getLogger(CheckSpringDatasourceConnectable.class);    
    private static final long serialVersionUID = 3161059124031362279L;
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_PROFILE = "profile";
    private String datasourceName;
    private String profile;
    
    // For accessing Spring beans by name or id
    private ApplicationContext applicationContext;
    
    @Override
    public String getDescription() {
        return "a diagnostic task that access a Datasource from the application context and uses that to verify that a connection can be created";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.philemonworks.selfdiagnose.DiagnosticTask#initializeFromAttributes(Attributes)
     */
    public void initializeFromAttributes(Attributes attributes) {
        // store variable if specified
        super.initializeFromAttributes(attributes);
        this.setDatasourceName(attributes.getValue(PARAMETER_NAME));
        this.setProfile(attributes.getValue(PARAMETER_PROFILE));
    }
    
    public void setUp(ExecutionContext ctx) throws DiagnoseException {
        super.setUp(ctx);
        DiagnoseUtil.verifyNonEmptyString(PARAMETER_NAME, datasourceName, this.getClass());
    }
    
    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        if (this.applicationContext == null) {
            result.setErrorMessage("No Spring ApplicationContext available");
            return; 
        }

        if (!SpringProfileUtil.profileIsActive(this.applicationContext, this.profile)) {
            result.setPassedMessage(DiagnoseUtil.format("Datasource [{0}] ignored. Not in profile(s) [{1}]", this.datasourceName, this.profile));
            return;
        }

        DataSource bean = null;
        bean = (DataSource)this.applicationContext.getBean(this.datasourceName);        
        if (null == bean) {
            result.setFailedMessage(DiagnoseUtil.format(
                    "Spring datasource [{0}] not found", datasourceName));
            return;
        }        
        try {
            Connection conn = bean.getConnection();
            if (conn != null) {
                result.setPassedMessage(DiagnoseUtil.format(
                        "Datasource [{0}] is found and a connection can be created.",datasourceName));                
                conn.close();                
            } else {
                result.setFailedMessage(DiagnoseUtil.format(
                        "No connection could be create using the Datasource [{0}]",datasourceName));
            }
        } catch (Exception ex){
            log.error("failed to get connection",ex);
            result.setFailedMessage(ex.getMessage());
        }
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }    
}
