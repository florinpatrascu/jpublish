package org.jpublish.module.jprss;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishModule;
import org.jpublish.SiteContext;
import org.jpublish.action.ActionWrapper;
import org.jpublish.action.PathAction;
import org.jpublish.module.jprss.model.JPRSSFeedImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A very simple RSS support for JPublish
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (2009.08.06)
 */
public class JPRSSModule implements JPublishModule {
    private static final Log log = LogFactory.getLog(JPRSSModule.class);

    public static final String JPRSS_MODULE_TAG = "JPRSSModule";
    public static final String JPCONTEXT_RSS_TAG = "JPRSSJPContext";
    public static final String EMPTY_STRING = "";

    private static final String NAME = "JPRSS Module";
    private static final String VERSION = "1.0";
    private static final String JPUBLISH_DEFAULT_RSS_URL = "/rss";

    private static final String DESCRIPTION = "simple JPublish RSS support";

    private SiteContext site;
    private Map actions = new HashMap(5);
    public boolean debug = false;
    private Map availableFeeds = new HashMap();

    private String repository = "content";
    private static final String JPUBLISH_DEFAULT_FEED_TITLE = "RSS";
    public static final String JPUBLISH_DEFAULT_FEED_TYPE = "application/rss+xml";
    public static final String JPUBLISH_DEFAULT_FEED_EXT = "rss.xml";
    public static final String JPRSSDEMO_FEED_PUBLISHER_ACTION = "org.jpublish.module.jprss.JPRssSimpleFeedPublisherAction";

    public void init(SiteContext site, Configuration configuration) throws Exception {
        log.info(NAME + " starting for: " + site.getServletContext().getServletContextName());
        this.site = site;

        /*
          <link rel="alternate" type="application/rss+xml" title="RSS" href="http://weblog.flop.ca/rss.xml" />
          <link rel="alternate" type="application/rdf+xml" title="RDF" href="http://weblog.flop.ca/rdf.xml" />
          <link rel="alternate" type="application/atom+xml" title="Atom" href="http://weblog.flop.ca/atom.xml" />
         */
        if (configuration.getChild("feeds") != null) {
            List rssURLSConfig = configuration.getChild("feeds").getChildren();
            JPRSSFeedImpl feedHeader;

            log.info("Mapping JPRSS feedHeader creator for all *.xml requests on: ");
            for (int i = 0; rssURLSConfig != null && i < rssURLSConfig.size(); i++) {
                Configuration urlConfig = (Configuration) rssURLSConfig.get(i);
                String url = urlConfig.getAttribute("url", JPUBLISH_DEFAULT_RSS_URL);
                String title = urlConfig.getAttribute("title", JPUBLISH_DEFAULT_FEED_TITLE);
                String description = urlConfig.getAttribute("description", EMPTY_STRING);
                String readFeedActionName = JPRSSDEMO_FEED_PUBLISHER_ACTION;
                if (urlConfig.getChild("read-feed-action") != null) {
                    readFeedActionName = urlConfig.getChild("read-feed-action")
                            .getAttribute("name", JPRSSDEMO_FEED_PUBLISHER_ACTION);
                }

                if (readFeedActionName == null || readFeedActionName.trim().length() == 0) {
                    readFeedActionName = JPRSSDEMO_FEED_PUBLISHER_ACTION;
                }
                // todo read-story actions

                feedHeader = new JPRSSFeedImpl(url, title, description);
                feedHeader.setReadFeedActionName(readFeedActionName);

                log.info("    url: " + url + ", title: " + feedHeader.getTitle() +
                        ", description: " + feedHeader.getDescription() + "; " + readFeedActionName);
                site.getActionManager().getPathActions().add(
                        new ActionWrapper(
                                new PathAction(feedHeader.getUrl() + "/*",
                                        new JPRSSFeedHeaderAction(this, feedHeader)), urlConfig)
                );

                site.getActionManager().getPathActions().add(
                        new ActionWrapper(
                                new PathAction(feedHeader.getUrl() + "/*.xml",
                                        new JPRSSFeedPublisherAction(this, readFeedActionName)), urlConfig)
                );

                availableFeeds.put(url, feedHeader);
            }
        }

        site.setAttribute(JPRSS_MODULE_TAG, this);
        log.info(this.toString() + ", is ready.");
    }

    public String feedExtension(String type) {
        String extension = JPUBLISH_DEFAULT_FEED_EXT;
        if (type != null && type.equalsIgnoreCase("RDF")) {
            extension = "rdf.xml";
        } else if (type != null && type.equalsIgnoreCase("Atom")) {
            extension = "atom.xml";
        }
        return extension;
    }


    public String feedType(String type) {
        String headerType = "application/rss+xml";
        if (type != null && type.equalsIgnoreCase("RDF")) {
            headerType = "application/rdf+xml";
        } else if (type != null && type.equalsIgnoreCase("Atom")) {
            headerType = "application/atom+xml";
        }
        return headerType;
    }

    public Map getDefinedActions() {
        return actions;
    }

    public Map getAvailableFeeds() {
        return availableFeeds;
    }

    public void destroy() {
        log.info(this.toString() + ", destroyed.");
    }

    public boolean isDebug() {
        return debug;
    }

    public String getRepository() {
        return repository;
    }

    public SiteContext getSite() {
        return site;
    }

    public Log getLog() {
        return log;
    }

    public String toString() {
        return NAME + " version: " + VERSION + ", description: " + DESCRIPTION;
    }
}
