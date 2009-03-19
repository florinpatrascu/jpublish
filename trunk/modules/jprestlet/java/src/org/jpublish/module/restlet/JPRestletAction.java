package org.jpublish.module.restlet;

import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.action.Action;
import org.restlet.Restlet;
import org.restlet.resource.Resource;

import javax.servlet.http.HttpServletRequest;

/**
 * This path Action will forward the request to the Restlet support and notify JPublish to stop
 * any post process task. It will also augument the request with some JPublish specific objects:
 * - context, JPRestletModule and so on.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jun 6, 2008 5:24:00 PM)
 */
public class JPRestletAction extends Restlet implements Action {
    private static final Log log = LogFactory.getLog(JPRestletAction.class);
    private JPRestletModule jpRestletModule;

    public JPRestletAction(JPRestletModule jpRestletModule) {
        this.jpRestletModule = jpRestletModule;
    }

    /**
     * One execution point for all the Rest calls
     *
     * @param context       the JPublish context
     * @param configuration Action's specific config, if any
     * @throws Exception
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        HttpServletRequest request = context.getRequest();

        request.setAttribute(JPRestletModule.JPRESTLET_MODULE_TAG, jpRestletModule);
        request.setAttribute(JPRestletModule.JPCONTEXT_RESTLET_TAG, context);

        // not sure if the next line will still be here
        context.put(JPRestletModule.JPRESTLET_MODULE_TAG, jpRestletModule);

        jpRestletModule.getConverter().service(request, context.getResponse());
        context.setStopProcessing(); //inform JPublish that somebody else is handling the rest of the workflow
    }
}
