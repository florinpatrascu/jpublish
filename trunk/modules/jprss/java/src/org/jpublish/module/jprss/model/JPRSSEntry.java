package org.jpublish.module.jprss.model;

import java.io.Serializable;
import java.util.Date;

/**
 * the simple definition of an RSS story; entry
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 10.08.2009 10:14:02)
 */
public interface JPRSSEntry extends Serializable {
    void setId(int id);

    int getId();
        
    String getUrl();

    void setUrl(String url);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    void setPublishedAt(Date date);

    Date getPublishedAt();

    void setType(String entryType);

    String getType();

    @Override
    String toString();
}
