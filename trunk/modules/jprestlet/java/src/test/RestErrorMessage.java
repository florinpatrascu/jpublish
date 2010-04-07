package test;

import org.json.JSONObject;

/**
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Apr 6, 2010 1:14:21 PM)
 */
public class RestErrorMessage {

    public JSONObject toJSON() {
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("error", "An error occured");
            return jsonobj;
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("error:");
        sb.append("An error occured");
        return sb.toString();
    }
}