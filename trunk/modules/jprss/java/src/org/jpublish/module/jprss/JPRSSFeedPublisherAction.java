package org.jpublish.module.jprss;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.action.Action;

/**
 * This action will delegate the execution to a user defined action that will publish
 * a feed
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 07.08.2009 16:07:32)
 */
public class JPRSSFeedPublisherAction implements Action {
    private static final Log log = LogFactory.getLog(JPRSSFeedPublisherAction.class);
    private JPRSSModule jpRSSModule;
    private String userActionName;

    public JPRSSFeedPublisherAction(JPRSSModule jpRSSModule, String userActionName) {
        this.jpRSSModule = jpRSSModule;
        this.userActionName = userActionName;
    }

    /**
     * delegate the execution point
     *
     * @param context       the JPublish context
     * @param configuration Action's specific config, if any
     * @throws Exception
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Executing: " + userActionName);
        }
        context.getSiteContext().getActionManager().execute(userActionName, context);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("JPRSSFeedPublisherAction");
        sb.append("{jpRSSModule=").append(jpRSSModule);
        sb.append(", userActionName='").append(userActionName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
