package com.philemonworks.selfdiagnose.report.util;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.report.util.exception.ErrorMessageException;
import com.philemonworks.selfdiagnose.report.util.exception.FailedMessageException;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class MavenPomPropertiesUtil {
    public static Properties readMavenPomPropertiesFile(ExecutionContext ctx, String fileLocation) throws FailedMessageException, ErrorMessageException {
        InputStream is = null;
        try {
            Object servletContext = ctx.getValue("servletcontext");
            if (servletContext != null && servletContext instanceof ServletContext) {
                is = ((ServletContext) servletContext).getResourceAsStream(fileLocation);
            }
            if (is == null) {
                is = MavenPomPropertiesUtil.class.getClass().getResourceAsStream(fileLocation);
            }
            // if needed retry using system resource
            if (is == null) {
                is = ClassLoader.getSystemResourceAsStream(fileLocation);
            }
            // if needed retry using contextClassLoader
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileLocation);
            }
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                // Build time is necessary for report.
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                int linenumber = 0;
                while ((line = br.readLine()) != null) {
                    linenumber++;
                    if (linenumber == 2 && line.startsWith("#")) {
                        prop.setProperty("buildtime", line.substring(1));
                        break;
                    }
                }
                return prop;
            } else {
                throw new FailedMessageException("Could not find version properties file (tried all classloaders i know of): [" + fileLocation + "]");
            }
        } catch (final IOException e) {
            throw new ErrorMessageException("Could not load version properties file");
        } catch (final DiagnoseException e) {
            throw new ErrorMessageException("Could not load version properties file");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException e) {
                    throw new ErrorMessageException("Could not close input stream");
                }
            }
        }
    }
}
