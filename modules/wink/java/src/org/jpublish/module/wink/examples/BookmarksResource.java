package org.jpublish.module.wink.examples;

import org.apache.wink.common.annotations.Workspace;
import org.apache.wink.common.http.HttpStatus;
import org.apache.wink.common.model.synd.SyndContent;
import org.apache.wink.common.model.synd.SyndEntry;
import org.apache.wink.common.model.synd.SyndFeed;
import org.apache.wink.common.model.synd.SyndText;
import org.apache.wink.server.utils.LinkBuilders;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.Map;

@Workspace(workspaceTitle = "Demo Bookmarks Service", collectionTitle = "My Bookmarks")
@Path("/wink/bookmarks")
public class BookmarksResource {
    private static final String SUB_RESOURCE_PATH = "{bookmark}";

    /**
     * This method is invoked when the HTTP GET method is issued by the client.
     * This occurs only when the requested representation (Http Accept header)
     * is Atom (application/atom+xml) or Json (application/json). The feed is
     * created with the mandatory fields. The feed will also contain the entries
     * found in the BookmarksStore. Links are generated for the feed and for the
     * entries in the response.
     *
     * @return the SyndFeed instance that will be serialized as an Atom or as
     *         Json
     */
    @GET
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public SyndFeed getBookmarks(@Context LinkBuilders linkProcessor, @Context UriInfo uriInfo) {
        SyndFeed feed = new SyndFeed();
        feed.setId("urn:collection:bookmarks");
        feed.setTitle(new SyndText("My Bookmarks"));
        feed.setUpdated(new Date());
        feed.setBase(uriInfo.getAbsolutePath().toString());

        // add entries to the feed, based on the existing bookmarks in the
        // memory store
        // (feed entries have no content, they have just metadata so there is no
        // need to set content
        // here)
        Map<String, String> bookmarks = BookmarkStore.getInstance().getBookmarks();

        for (String key : bookmarks.keySet()) {
            SyndEntry entry = createEntry(key, bookmarks.get(key), linkProcessor, null);
            feed.addEntry(entry);
        }
        // generate collection links in the response
        linkProcessor.createSystemLinksBuilder().build(feed.getLinks());
        return feed;
    }

    /**
     * This method is invoked when the HTTP POST method is issued by the client.
     * This occurs only when the requested representation (Http Accept header)
     * is Atom (application/atom+xml) or Json (application/json) and plain text
     * is provided in the HTTP request message body of content MIME type
     * "text/plain" (header "Content-Type" must be "text/plain"). This method
     * creates a new Bookmark resource based on the data in the request and puts
     * the new Bookmark into the BookmarkStore.
     *
     * @return response indicating the creation of the bookmark
     */
    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public Response createBookmark(String bookmark,
                                   @Context UriInfo uriInfo,
                                   @Context LinkBuilders linkProcessor) {
        if (bookmark == null || bookmark.length() == 0) {
            return Response.status(HttpStatus.BAD_REQUEST.getCode()).build();
        }

        String bookmarkId = BookmarkStore.getNewId();
        BookmarkStore.getInstance().putBookmark(bookmarkId, bookmark);

        SyndEntry entry = createEntry(bookmarkId, bookmark, linkProcessor, uriInfo);

        URI location = uriInfo.getAbsolutePathBuilder().segment(bookmarkId).build();
        return Response.created(location).entity(entry).build();
    }

    /**
     * This method is invoked when the HTTP GET method is issued by the client.
     * This occurs only when the requested representation (Http Accept header)
     * is Atom (application/atom+xml) or Json (application/json). In the case
     * that the requested bookmark is found in the BookmarkStore a synd entry is
     * created with mandatory metadata fields and with metadata content that is
     * taken from the BookmarkStore.
     *
     * @param bookmarkId the bookmark id to get as it appears on the request uri
     * @return SyndEntry with the information about the bookmark
     */
    @Path(SUB_RESOURCE_PATH)
    @GET
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public SyndEntry getBookmark(@Context LinkBuilders linkProcessor,
                                 @Context UriInfo uriInfo,
                                 @PathParam("bookmark") String bookmarkId) {
        // check whether the bookmark exists in the memory store
        String bookmark = BookmarkStore.getInstance().getBookmark(bookmarkId);
        if (bookmark == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return createEntry(bookmarkId, bookmark, linkProcessor, uriInfo);
    }

    /**
     * This method is invoked when the HTTP PUT method is issued by the client.
     * This occurs only when the requested representation (Http Accept header)
     * is Atom (application/atom+xml) or Json (application/json) and plain text
     * is provided in the HTTP request message body of content MIME type
     * "text/plain" (header "Content-Type" must be "text/plain"). This method
     * will update the requested bookmark in the BookmarkStore with new content
     * taken from the request message body.
     *
     * @param bookmarkId the bookmark id to update as it appears on the request
     *                   uri
     * @return SyndEntry with the information about the updated bookmark
     * @throws Exception a problem with reading the input stream
     */
    @Path(SUB_RESOURCE_PATH)
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public SyndEntry updateBookmark(String bookmark,
                                    @Context LinkBuilders linkProcessor,
                                    @Context UriInfo uriInfo,
                                    @PathParam("bookmark") String bookmarkId) {
        // check whether the bookmark exists for update
        String value = BookmarkStore.getInstance().getBookmark(bookmarkId);
        if (value == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // update existing bookmark in the memory store with new bookmark value
        BookmarkStore.getInstance().putBookmark(bookmarkId, bookmark);

        // create SyndEntry and return it
        return createEntry(bookmarkId, bookmark, linkProcessor, uriInfo);
    }

    /**
     * This method is invoked when the HTTP DELETE method is issued by the
     * client. This occurs only when the requested representation (Http Accept
     * header) is Atom (application/atom+xml) or Json (application/json). This
     * method deletes the bookmark from the BookmarkStore and returns the
     * deleted bookmark.
     *
     * @param bookmarkId the bookmark id to update as it appears on the request
     *                   uri
     * @return SyndEntry with the information about the deleted bookmark
     */
    @Path(SUB_RESOURCE_PATH)
    @DELETE
    @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    public SyndEntry deleteBookmark(@Context LinkBuilders linkProcessor,
                                    @Context UriInfo uriInfo,
                                    @PathParam("bookmark") String bookmarkId) {
        // check whether the bookmark exists for deletion
        String bookmark = BookmarkStore.getInstance().getBookmark(bookmarkId);
        if (bookmark == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Remove bookmark form the store
        BookmarkStore.getInstance().deleteBookmark(bookmarkId);

        // create SyndEntry and return it
        return createEntry(bookmarkId, bookmark, linkProcessor, uriInfo);
    }

    private SyndEntry createEntry(String bookmarkId,
                                  String content,
                                  LinkBuilders linkProcessor,
                                  UriInfo uriInfo) {
        SyndEntry entry = new SyndEntry();
        entry.setId(bookmarkId);
        entry.setTitle(new SyndText("My Bookmark " + bookmarkId));
        entry.setPublished(new Date());

        if (content != null) {
            entry.setContent(new SyndContent(content));
        }

        if (uriInfo != null) {
            entry.setBase(uriInfo.getAbsolutePath().toString());
        }

        linkProcessor.createSystemLinksBuilder().subResource(bookmarkId).build(entry.getLinks());
        return entry;
    }

}

