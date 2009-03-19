package org.jpublish.module.restlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import java.util.Iterator;
import java.util.Map;

/**
 * This is a very simple support for publishing basic text, html content obtained
 * from a JPublish "rest" repository. For more advanced features use an ExtendedFileSystemRepository
 * repository.
 *
 * A future version will allow any JPublish response to be published as a Representation, in Restlet parlance
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Mar 13, 2009 10:06:10 AM)
 */
public class JPSimpleRestlet extends Restlet {
    public static final Log log = LogFactory.getLog(JPSimpleRestlet.class);
    private JPRestletModule module;
    private String path;
    private String action;

    public JPSimpleRestlet(JPRestletModule module, String path, String action) {
        this.module = module;
        this.path = path;
        this.action = action;
    }

    public void handle(Request request, Response response) {
        JPublishContext context = (JPublishContext) request.getAttributes().get(JPRestletModule.JPCONTEXT_RESTLET_TAG);
        MediaType valueType = MediaType.TEXT_PLAIN;
        String value = JPRestletModule.EMPTY_STRING;
        boolean actionExecuted = false;
        boolean actionOnly = true;


        if (context != null) {
            try {
                Map reqAttributes = request.getAttributes();
                if (reqAttributes != null && !reqAttributes.isEmpty()) {
                    Iterator attributes = reqAttributes.keySet().iterator();
                    // keep Java 1.4 for now :(
                    while (attributes.hasNext()) {
                        String attribute = (String) attributes.next();
                        context.put(attribute, reqAttributes.get(attribute));
                    }
                }

                // 1st version; 2009.03.12
                // is just an action we need?
                if (!action.equals(JPRestletModule.EMPTY_STRING)) {
                    module.getSite().getActionManager().execute(action, context);
                    actionExecuted = true;
                }

                //or is it a page too?
                if (!path.equals(JPRestletModule.EMPTY_STRING)) {
                    actionOnly = false;
                    // render the page at the given path
                    value = module.getSite().getRepository(module.getRepository()).get(path, context);
                }

                if (actionExecuted && actionOnly) {
                    value = (String) context.get(JPRestletModule.JP_RESTLET_ACTION_RESPONSE);
                    //poor man validation (for now)
                    if (value == null) {
                        value = JPRestletModule.EMPTY_STRING;
                    }
                }

                if (context.get(JPRestletModule.JP_RESTLET_ACTION_RESPONSE_TYPE) != null) {
                    valueType = MediaType.valueOf(
                            (String) context.get(JPRestletModule.JP_RESTLET_ACTION_RESPONSE_TYPE));
                }

                response.setEntity(value, valueType);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(Status.CONNECTOR_ERROR_INTERNAL, e.getMessage());
            }
        }
    }


    public static Log getLog() {
        return log;
    }

    public String getPath() {
        return path;
    }

    public String getAction() {
        return action;
    }
}
