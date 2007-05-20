/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */

package ca.flop.jpublish.wiki;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.Repository;
import org.jpublish.component.AbstractComponent;

/**
 * A JPublish component used to render Textile syntax from text parameters or from
 * external views available in the give repository.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Sunday; May 20, 2007)
 */
public class JPTextileComponent extends AbstractComponent {
    private static final Log log = LogFactory.getLog(JPTextileComponent.class);
    public static final String BLANK = " ";

    public static final String EMPTY_STRING = "";

    private static final String NAME = "JPTextileComponent";
    private static final String DESCRIPTION = "This module offers Textile support to JPublish applications.";

    private Repository repository;

    /**
     * Load the component's configuration data.  Implementations should
     * override this method if they require configuration.  If an
     * implementation does override this method the implementation
     * should call <code>super.loadConfiguration(configuration)</code>
     * first.
     *
     * @param configuration The configuration data
     * @throws com.anthonyeden.lib.config.ConfigurationException
     *
     */
    public void loadConfiguration(Configuration configuration) throws ConfigurationException {
        log.info("loading the Textile support...");
        super.loadConfiguration(configuration);

        Configuration repositoryConfiguration = configuration.getChild("repository");
        if (repositoryConfiguration != null) {

            String repositoryName = repositoryConfiguration.getAttribute("name");
            if (repositoryName == null) {
                throw new ConfigurationException("Repository name is required");
            } else {
                repository = siteContext.getRepository(repositoryName);
            }
            log.info("Textile support available.");
        } else {
            throw new ConfigurationException("general error encountered while loading the JPTextileComponent");
        }

    }

    /**
     * Render the component for the specified path and return the result.
     *
     * @param path    The request path
     * @param context The context
     * @return The rendered component
     * @throws Exception
     */
    public String render(String path, JPublishContext context) throws Exception {
        return JTextile.textile( "h1. "+getName());
    }

    /**
     * textilize a given text containing Textile syntax
     *
     * @param text a string containing the Textile syntax
     * @return a textilized text
     * @throws Exception if any
     */
    public String renderText(String text, JPublishContext context) throws Exception {
        if (text != null && text.trim().length() > 0)
            return JTextile.textile(text);
        else
            return EMPTY_STRING;
    }

    /**
     * textilize a given text containing Textile syntax
     *
     * @param path the path to a file containing Textile syntax.
     * @return a textilized text
     * @throws Exception if any
     */
    public String renderPath(String path, JPublishContext context) throws Exception {
        return JTextile.textile(repository.get(path));
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }
}
