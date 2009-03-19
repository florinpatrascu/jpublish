package org.jpublish.module.restlet.demo;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.jpublish.JPublishContext;
import org.jpublish.module.restlet.JPRestletModule;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Very simple restlet test used to check if the JPublish RESTLET integration
 * works. Example taken from:
 * http://temporary.name/java/index.php/spring/restlet-spring-integration
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jun 6, 2008 12:32:05 PM)
 */
public class HelloJPRestlet extends Restlet {
    public static final Log log = LogFactory.getLog(HelloJPRestlet.class);

    public void handle(Request request, Response response) {
        // get value of 'name' url fragment, using pattern '/hello/{name}'
        String name = (String) request.getAttributes().get("name");
        JPublishContext context = (JPublishContext) request.getAttributes().get(JPRestletModule.JPCONTEXT_RESTLET_TAG);

        if (context != null) {
            log.info("got a JPublish context ....");
        }
        // send simple string as response to client
        response.setEntity("Hello " + name, MediaType.TEXT_PLAIN);
    }
}
