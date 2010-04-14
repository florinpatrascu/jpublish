package org.jpublish.module.wink;

import com.anthonyeden.lib.config.Configuration;
import org.jpublish.JPublishContext;
import org.jpublish.action.Action;

/**
 * A path action responsible for dispatching the REST calls to the Wink request processor
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Apr 12, 2010 7:43:08 PM)
 */

public class JPWinkAction implements Action {
    private JPWinkModule jpWinkModule;

    public JPWinkAction(JPWinkModule jpWinkModule) {
        this.jpWinkModule = jpWinkModule;
    }

    /**
     * dispatch teh execution flow to Wink.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        context.getRequest().setAttribute(JPWinkModule.JPWINK_CONTEXT_TAG, context);
        jpWinkModule.getRequestProcessor().handleRequest(context.getRequest(), context.getResponse());
        context.setStopProcessing();
    }
}
