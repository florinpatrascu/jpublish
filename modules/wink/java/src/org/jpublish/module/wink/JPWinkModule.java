package org.jpublish.module.wink;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.RequestProcessor;
import org.apache.wink.server.internal.application.ServletWinkApplication;
import org.apache.wink.server.internal.utils.ServletFileLoader;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * A Wink based solution for implementing and consuming REST based Web Services in JPublish.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Apr 11, 2010 7:01:55 PM)
 */
public class JPWinkModule implements JPublishModule {
    private static final Log log = LogFactory.getLog(JPWinkModule.class);
    public static final String JPWINK_CONTEXT_TAG = "__JPWinkContext__";

    private static final String NAME = "JPWink Module";
    private static final String VERSION = "1.0";
    private static final String WINK_DEFAULT_REST_URL = "/rest";

    private static final String DESCRIPTION = "WINK support for JPublish";
    private SiteContext site;
    private Configuration configuration;

    private static String requestProcessorAttribute;
    private String path;

    public void init(SiteContext site, Configuration configuration) throws Exception {
        this.site = site;
        this.configuration = configuration;
        requestProcessorAttribute = configuration.getChildValue("requestProcessorAttribute");
        path = configuration.getChildValue("path", "/wink/*");

        RequestProcessor requestProcessor = getRequestProcessor();
        if (requestProcessor == null) {
            // create the request processor
            requestProcessor = createRequestProcessor();
            if (requestProcessor == null) {
                throw new IllegalStateException("Request processor could not be created.");
            }
            storeRequestProcessorOnServletContext(requestProcessor);
        }

        site.getActionManager().getPathActions().add(
                new ActionWrapper(
                        new PathAction(path, new JPWinkAction(this)), configuration) );

        log.info(String.format("**** %s is mapped on: %s", NAME, path));
        log.info(this.toString() + " is ready.");
    }


    public Map getDefinedActions() {
        return null;
    }

    public void destroy() {
        log.info(this.toString() + ", destroyed.");
    }

    public String toString() {
        return NAME + " version: " + VERSION + ", description: " + DESCRIPTION;
    }

    protected RequestProcessor getRequestProcessor() {
        return RequestProcessor.getRequestProcessor(getServletContext(), requestProcessorAttribute);
    }

    protected void storeRequestProcessorOnServletContext(RequestProcessor requestProcessor) {
        requestProcessor.storeRequestProcessorOnServletContext(getServletContext(), requestProcessorAttribute);
    }

    protected RequestProcessor createRequestProcessor() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException {

        DeploymentConfiguration deploymentConfiguration = getDeploymentConfiguration();
        RequestProcessor requestProcessor = new RequestProcessor(deploymentConfiguration);
        deploymentConfiguration.addApplication(getApplication(), false);
        return requestProcessor;
    }

    protected DeploymentConfiguration getDeploymentConfiguration() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException {

        DeploymentConfiguration deploymentConfiguration = createDeploymentConfiguration();
        deploymentConfiguration.setServletConfig(getServletConfig());
        deploymentConfiguration.setServletContext(getServletContext());
        deploymentConfiguration.setProperties(getProperties());
        deploymentConfiguration.init();
        return deploymentConfiguration;
    }

    protected Properties getProperties() throws IOException {
        Properties defaultProperties = loadProperties(
                configuration.getChildValue("propertiesLocation", "WEB-INF/wink.properties"), null);
        return defaultProperties;
    }

    protected DeploymentConfiguration createDeploymentConfiguration()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        return new DeploymentConfiguration();
    }

    @SuppressWarnings("unchecked")
    protected Application getApplication() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        return new ServletWinkApplication(getServletContext(),
                configuration.getChildValue("applicationConfigLocation", "WEB-INF/wink-application"));
    }

    private Properties loadProperties(String resourceName, Properties defaultProperties)
            throws IOException {
        Properties properties =
                defaultProperties == null ? new Properties() : new Properties(defaultProperties);

        InputStream is = null;
        try {
            is = ServletFileLoader.loadFileAsStream(getServletContext(), resourceName);
            properties.load(is);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.warn("cannot close the config file: " + resourceName, e);
            }
        }
        return properties;
    }

    public ServletConfig getServletConfig() {
        return site.getServletConfig();
    }

    public ServletContext getServletContext() {
        return site.getServletContext();
    }
}
