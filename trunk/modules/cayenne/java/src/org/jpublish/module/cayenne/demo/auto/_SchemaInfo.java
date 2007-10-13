package org.jpublish.module.cayenne.demo.auto;

/** Class _SchemaInfo was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _SchemaInfo extends org.apache.cayenne.CayenneDataObject {

    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String ID_PROPERTY = "id";

    public static final String ID_PK_COLUMN = "id";

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }
    
    
    public void setId(Integer id) {
        writeProperty("id", id);
    }
    public Integer getId() {
        return (Integer)readProperty("id");
    }
    
    
}
