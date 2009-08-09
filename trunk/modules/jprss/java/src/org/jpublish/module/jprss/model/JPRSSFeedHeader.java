package org.jpublish.module.jprss.model;

/**
 * simple feed descriptor
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 07.08.2009 12:14:57)
 */
public class JPRSSFeedHeader {
    private String url;
    private String title;
    private String description;
    private String readFeedActionName;
    private String readStoryActionName;

    public JPRSSFeedHeader(String url, String title, String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadFeedActionName() {
        return readFeedActionName;
    }

    public void setReadFeedActionName(String readFeedActionName) {
        this.readFeedActionName = readFeedActionName;
    }

    public String getReadStoryActionName() {
        return readStoryActionName;
    }

    public void setReadStoryActionName(String readStoryActionName) {
        this.readStoryActionName = readStoryActionName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("JPRSSFeedHeader");
        sb.append("{url='").append(url).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", readFeedActionName='").append(readFeedActionName).append('\'');
        sb.append(", readStoryActionName='").append(readStoryActionName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
