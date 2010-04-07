package test;

import org.restlet.Context;
import org.restlet.data.*;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Apr 6, 2010 12:57:27 PM)
 */
public class UserResource extends Resource {
    SimpleUser user;

    public UserResource(Context context, Request request, Response response) {
        super(context, request, response);
        String name = (String) getRequest().getAttributes().get("name");
        this.user = findUser(name);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    }

    /**
     * Represent the user object in the requested format.
     *
     * @param variant
     * @return
     * @throws ResourceException
     */
    public Representation represent(Variant variant) throws ResourceException {
        Representation result = null;
        if (null == this.user) {
            RestErrorMessage em = new RestErrorMessage();
            return representError(variant, em);
        } else {
            if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
                result = new JsonRepresentation(this.user.toJSON());
            } else {
                result = new StringRepresentation(this.user.toString());
            }
        }
        return result;
    }

    /**
     * Handle a POST Http request. Create a new user
     * We handle only a form request in this example. Other types could be
     * JSON or XML.
     *
     * @param entity
     * @throws ResourceException
     */
    public void acceptRepresentation(Representation entity) throws ResourceException {
        try {
            if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM,
                    true)) {
                AtomicReference<Form> form = new AtomicReference<Form>(new Form(entity));
                SimpleUser u = new SimpleUser();
                u.setName(form.get().getFirstValue("user[name]"));
                getResponse().setStatus(Status.SUCCESS_OK);
                // We are setting the representation in the example always to
                // JSON.
                // You could support multiple representation by using a
                // parameter in the request like "?response_format=xml"
                Representation rep = new JsonRepresentation(u.toJSON());
                getResponse().setEntity(rep);
            } else {
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

    /**
     * Represent an error message in the requested format.
     *
     * @param variant
     * @param em
     * @return
     * @throws ResourceException
     */
    private Representation representError(Variant variant, RestErrorMessage em)
            throws ResourceException {
        return variant.getMediaType().equals(MediaType.APPLICATION_JSON) ?
                new JsonRepresentation(em.toJSON()) :
                new StringRepresentation(em.toString());
    }


    protected Representation representError(MediaType type, RestErrorMessage em)
            throws ResourceException {

        return type.equals(MediaType.APPLICATION_JSON) ?
                new JsonRepresentation(em.toJSON()) :
                new StringRepresentation(em.toString());
    }

    private SimpleUser findUser(String name) {
        return name == null ? null :
                new SimpleUser(name, "florin.patrascu@gmail.com");
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public boolean allowGet() {
        return true;
    }


}
