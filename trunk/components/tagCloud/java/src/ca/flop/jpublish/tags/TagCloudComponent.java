/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */

package ca.flop.jpublish.tags;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.util.StringUtils;
import org.jpublish.JPublishContext;
import org.jpublish.Repository;
import org.jpublish.component.AbstractComponent;

import java.util.List;
import java.util.Map;

/**
 * JPublish component used to create a tag cloud containing keywords and
 * their popularity.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$
 */
public class TagCloudComponent extends AbstractComponent {
    private static final Log log = LogFactory.getLog(TagCloudComponent.class);

    public static final String TAG_SEPARATOR = ",";
    public static final String ARTICLE_ID = "articleId";
    public static final String TAGS = "tags";
    public static final String DEFAULT_NORMALIZER = "10";
    public static final String ARTICLE_KEY = "article";
    public static final String TRAILING_CHARS = "...";
    public static final String BLANK = " ";

    public static final String EMPTY_STRING = "";

    private static final String NAME = "TagCloudComponent";
    private static final String DESCRIPTION = "This module offers Tags support to JPublish applications.";
    private String viewPath;
    private Repository repository;
    private String actionName;
    private String tagSeparator = TAG_SEPARATOR;
    private int normalizer;

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
        super.loadConfiguration(configuration);

        Configuration repositoryConfiguration = configuration.getChild("repository");
        if (repositoryConfiguration != null) {

            String repositoryName = repositoryConfiguration.getAttribute("name");
            if (repositoryName == null) {
                throw new ConfigurationException("Repository name is required");
            } else {

                repository = siteContext.getRepository(repositoryName);
                if (repository == null) {
                    throw new ConfigurationException("Repository not found: " + repositoryName);
                } else {
                    viewPath = repositoryConfiguration.getAttribute("view");
                    if (viewPath == null) {
                        throw new ConfigurationException("View path is required");
                    }

                    Configuration tagSeparatorConfiguration = configuration.getChild("tag-separator");
                    if (tagSeparatorConfiguration != null)
                        tagSeparator = tagSeparatorConfiguration
                                .getAttribute("separator", TAG_SEPARATOR);

                    actionName = repositoryConfiguration.getAttribute("execute", "");
                    normalizer = Integer.parseInt(configuration.getChildValue("normalizer", DEFAULT_NORMALIZER));
                }
            }

        } else {
            throw new ConfigurationException("general error encountered while loading the TagCloudComponent");
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
        // execute the action supposed to bring in the tag elements
        if (actionName.trim().length() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("executing: " + actionName);
            }
            siteContext.getActionManager().execute(actionName, context);
        }
        String articleId = (String) context.get(ARTICLE_ID);
        context.put(ARTICLE_KEY, newArticle(articleId, context));
        return repository.get(viewPath, context);
    }

    /**
     * Process the text parameter and Render the component
     *
     * @param text    the string to process
     * @param context The context
     * @return The rendered component
     * @throws Exception any Exception
     */
    public String renderText(String text, JPublishContext context) throws Exception {
        return null;
    }

    /**
     * Process the View from at the given path and Render the component
     *
     * @param path    the View to process
     * @param context The context
     * @return The rendered component
     * @throws Exception any Exception
     */
    public String renderPath(String path, JPublishContext context) throws Exception {
        return null;
    }

    private Article newArticle(String articleId, JPublishContext context) {
        Article article = null;

        if (articleId != null) {
            Map articleProperties = (Map) context.get(articleId);
            List tags = (List) articleProperties.get(TAGS);
            article = new Article(articleId);

            for (int i = 0; i < tags.size(); i++) {
                String sTag = (String) tags.get(i);
                String[] props = StringUtils.split(sTag, tagSeparator);

                //Tag tag =
                article.addTag(props[0], Long.parseLong(props[1]));
                // tag.setProperty("score", props[2]);
                // todo think about implementing an API used to store arbitrary TAG attributes as defined by the user
            }
            article.calculateTags(normalizer);
        }
        return article;
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }
}
