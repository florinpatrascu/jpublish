package test;

import org.json.JSONObject;

/**
 * used for the REST testing
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Apr 6, 2010 1:11:52 PM)
 */
public class SimpleUser {
    String name;
    String email;

    public SimpleUser() {
    }

    public SimpleUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Convert this object to a JSON object for representation
     */
    public JSONObject toJSON() {
        try {
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("name", this.name);
            jsonobj.put("email", this.email);
            return jsonobj;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("SimpleUser");
        sb.append("{name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
