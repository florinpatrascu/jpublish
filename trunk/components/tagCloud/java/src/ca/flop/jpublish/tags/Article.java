/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */

package ca.flop.jpublish.tags;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is used to group a number of TAGS and store them by id for
 * later retrieval.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Aug 20, 2006 1:06:47 PM)
 */
public class Article implements Serializable {
    private String id;
    /**
     * the collection of tags associated with this article
     */
    private Map tags;
    private long maxPopularity = 0;
    private Map properties;
    private String blankReplacementForEncodedTagNames = TagCloudComponent.BLANK;

    public void calculateTags(int normalizer) {
        if (tags == null || tags.isEmpty())
            return;

        Iterator it = tags.values().iterator();
        // now rank the tags
        while (it.hasNext()) {
            Tag tag = (Tag) it.next();
            tag.calculateRank(maxPopularity, normalizer);
        }
    }

    public Article(String id) {
        this.id = id;
        tags = new LinkedHashMap();
    }


    /**
     * Gets the tag with the specified name.
     *
     * @param name the name as a String
     * @return a Tag instance
     */
    public synchronized Tag getTag(String name) {
        Tag tag = (Tag) tags.get(name);
        if (tag == null) {
            tag = new Tag(name, this);
            tags.put(name, tag);
        }

        return tag;
    }

    /**
     * Removes the specified tag.
     *
     * @param name the name as a String
     */
    public synchronized void removeTag(String name) {
        tags.remove(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Tag addTag(String name, long popularity) {
        Tag tag = getTag(name);
        tag.setPopularity(popularity);
        if (tag.getPopularity() > this.maxPopularity) {
            this.maxPopularity = tag.getPopularity();
        }
        return tag;
    }

    public Tag[] getTags() {
        return (Tag[]) tags.values().toArray(new Tag[tags.values().size()]);
    }

    public int getNumberOfTags() {
        if (tags != null && !tags.isEmpty())
            return tags.size();
        else
            return 0;
    }

    public boolean getHasTags() {
        return getNumberOfTags() > 0;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public long getMaxPopularity() {
        return maxPopularity;
    }

    public String getBlankReplacementForEncodedTagNames() {
        return blankReplacementForEncodedTagNames;
    }

    public void setBlankReplacementForEncodedTagNames(
            String blankReplacementForEncodedTagNames) {
        
        this.blankReplacementForEncodedTagNames = blankReplacementForEncodedTagNames;
    }

    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                '}';
    }

}
