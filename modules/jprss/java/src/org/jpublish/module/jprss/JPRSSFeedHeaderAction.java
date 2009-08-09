package org.jpublish.module.jprss;

import com.anthonyeden.lib.config.Configuration;
import org.jpublish.JPublishContext;
import org.jpublish.module.jprss.model.JPRSSFeedHeader;
import org.jpublish.action.Action;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2009.02.07)
 */
public class JPRSSFeedHeaderAction implements Action {
    private JPRSSModule jpRSSModule;
    private JPRSSFeedHeader feedHeader;

    public JPRSSFeedHeaderAction(JPRSSModule jpRSSModule, JPRSSFeedHeader feedHeader) {
        this.jpRSSModule = jpRSSModule;
        this.feedHeader = feedHeader;
    }

    /**
     * One execution point for all the calls to the newsfeed
     *
     * @param context       the JPublish context
     * @param configuration Action's specific config, if any
     * @throws Exception
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        context.put("JPRSSENABLED", Boolean.TRUE);
        context.put("JPRSSFEED", feedHeader);
        context.put("JPRSSModule", jpRSSModule);
    }

}