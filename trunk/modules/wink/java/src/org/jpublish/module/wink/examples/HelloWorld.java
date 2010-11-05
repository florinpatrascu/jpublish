package org.jpublish.module.wink.examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.common.internal.utils.MediaTypeUtils;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.apache.wink.common.model.synd.SyndEntry;
import org.apache.wink.common.model.synd.SyndText;
import org.jpublish.util.FileCopyUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <id>helloworld:1</id>
 * <updated>2010-04-12T16:50:31.958-04:00</updated>
 * <title type="text">Hello World!</title>
 * </entry>
 */
@Path("/wink/world")
public class HelloWorld {
    protected static final Log log = LogFactory.getLog(HelloWorld.class);
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

    /**
     * demonstrating a multipart transfer (file upload) with Wink. Expects a file name as a
     * path parameter. The file name will be used to create a temporary file. Example of usage:
     *
     * curl -H "Accept:text/plain" -F "name=@text.txt"  http://localhost:8080/wink/world/file/file_out
     *
     * @param inMultiPart the multipart form data
     * @return a string confirming the file uploaded and its size
     * @throws IOException
     */

    @Path("file/{fileName}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaTypeUtils.MULTIPART_FORM_DATA)
    public String uploadFiles(InMultiPart inMultiPart, @PathParam("fileName") String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();

        while (inMultiPart.hasNext()) {
            File f = File.createTempFile(fileName, ".tmp");
            FileOutputStream fos = new FileOutputStream(f);

            InPart part = inMultiPart.next();
            MultivaluedMap<String, String> headers = part.getHeaders();

            String CDHeader = headers.getFirst("Content-Disposition");
            InputStream is = part.getInputStream();

            int size = FileCopyUtils.copy(is, fos);

            String fn = "";
            Pattern p = Pattern.compile("filename=\".*\"");
            Matcher m = p.matcher(CDHeader);

            if (m.find()) {
                fn = m.group();
            }

            //String filename = CDHeader.
            sb.append("uploaded a file, ").append(fn).append(", size = ").append(size).append(" bytes\n");
            log.info(String.format("%s, created", f.getAbsolutePath()));
        }
        return sb.toString();
    }

}