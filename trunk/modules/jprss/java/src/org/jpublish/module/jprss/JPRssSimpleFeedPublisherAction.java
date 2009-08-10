package org.jpublish.module.jprss;

import com.anthonyeden.lib.config.Configuration;
import org.jpublish.JPublishContext;
import org.jpublish.module.jprss.model.JPRSSFeed;
import org.jpublish.action.Action;

import javax.servlet.http.HttpServletRequest;

/**
 * This path Action will manage the news feeds
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2009.02.07)
 */
public class JPRssSimpleFeedPublisherAction implements Action {
    private JPRSSModule jpRSSModule;
    private JPRSSFeed feed;

    public JPRssSimpleFeedPublisherAction(JPRSSModule jpRSSModule, JPRSSFeed feed) {
        this.jpRSSModule = jpRSSModule;
        this.feed = feed;
    }

    /**
     * One execution point for all the calls to the newsfeed
     *
     * @param context       the JPublish context
     * @param configuration Action's specific config, if any
     * @throws Exception
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        HttpServletRequest request = context.getRequest();
        if (jpRSSModule.getLog().isDebugEnabled()) {
            jpRSSModule.getLog().debug("Publishing: " + feed);
        }
        context.setStopProcessing();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("JPRssSimpleFeedPublisherAction");
        sb.append("{feedHeader=").append(feed);
        sb.append('}');
        return sb.toString();
    }
}
