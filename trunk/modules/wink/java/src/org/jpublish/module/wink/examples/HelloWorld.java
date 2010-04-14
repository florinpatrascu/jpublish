package org.jpublish.module.wink.examples;

import org.apache.wink.common.model.synd.SyndEntry;
import org.apache.wink.common.model.synd.SyndText;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Simple example - Hello World!
 * <p/>
 * The SDK dispatches HTTP requests for URI
 * <code>http://[host]:[port]/HelloWorld/rest/world</code>, where
 * <code>HelloWorld</code> is the context root, to this class. A simple Atom
 * entry is returned in HTTP response.
 * <p/>
 * The service document is available at URI
 * <code>http://[host]:[port]/HelloWorld/rest</code> but it is empty because
 * this simple demo doesn't contain any collection of resources.
 * <p/>
 * This resource must be registered within a JAX-RS application, this example
 * uses the default usage of application /WEB-INF/application
 */


//

/**
 * if you add this: @Path("/wink/world")
 * and ask:
 * $ curl -H "Accept:application/atom+xml; charset=utf-8" "http://localhost:8080/wink/world"
 * you get this:
 * <?xml version="1.0" encoding="utf-8" standalone="yes"?>
 * <entry xmlns:ns3="http://www.w3.org/1999/xhtml" xmlns:ns2="http://a9.com/-/spec/opensearch/1.1/" xmlns="http://www.w3.org/2005/Atom">
 *   <id>helloworld:1</id>
 *   <updated>2010-04-12T16:50:31.958-04:00</updated>
 *   <title type="text">Hello World!</title>
 * </entry>
 */
@Path("/wink/world")
public class HelloWorld {
    public static final String ID = "helloworld:1";

    /**
     * This method is called by the SDK for HTTP GET method requests where the
     * Accept header allows the Atom media type application/atom+xml. A
     * SyndEntry is created with basic information. Serialization of the
     * SyndEntry to Atom entry is performed by the SDK automatically. The
     * default status code of 200 (OK) is returned in the response.
     *
     * @return SyndEntry of the requested resource
     */
    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public SyndEntry getGreeting() {
        // create and return a syndication entry with a "Hello World!" title,
        // some ID and the current time.
        SyndEntry synd = new SyndEntry(new SyndText("Hello World!"), ID, new Date());
        return synd;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getGreetingHtml() {
        return "Hello World!";
    }
}