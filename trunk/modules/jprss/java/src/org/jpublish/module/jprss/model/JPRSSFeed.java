package org.jpublish.module.jprss.model;

import java.io.Serializable;

/**
 * the definition of a simple feed
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 10.08.2009 10:12:34)
 */
public interface JPRSSFeed extends Serializable {
    void setId(int id);

    int getId();

    String getName();

    void setName(String name);

    String getUrl();

    void setUrl(String url);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    String getReadFeedActionName();

    void setReadFeedActionName(String readFeedActionName);

    String getReadStoryActionName();

    void setReadStoryActionName(String readStoryActionName);

    @Override
    String toString();
}
