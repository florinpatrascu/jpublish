/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */

package ca.flop.jpublish.tags;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * key object offering support for keywords/tag ranking
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Aug 20, 2006 1:04:58 PM)
 */
public class Tag implements Serializable, Comparable {
    protected static final Log log = LogFactory.getLog(Tag.class);

    /**
     * the owning article
     */
    private Article article;

    /**
     * the name of the tag
     */
    private String name = TagCloudComponent.EMPTY_STRING;

    /**
     * the encoded name of the tag
     */
    private String encodedName = TagCloudComponent.EMPTY_STRING;

    /**
     * the popularity of this Tag
     */
    private long popularity = 0;

    /**
     * the rank for this tag
     */
    private int rank;

    private Map properties = null;

    /**
     * Creates a new tag with the specified properties.
     *
     * @param name    the name
     * @param article a Article instance
     */
    public Tag(String name, Article article) {
        this.article = article;
        setName(name);
    }

    /**
     * Gets the name of this tag.
     *
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    public String getAnyName(String name, int maxChars) {
        return getAnyName(name, maxChars, TagCloudComponent.TRAILING_CHARS);
    }

    public String getAnyName(String name, int maxChars, String trailingChars) {
        if (name != null && maxChars >= 0)
            try {
                if (name.trim().length() > maxChars)
                    return name.trim().substring(0, maxChars) +
                            (trailingChars != null ? trailingChars :
                                    TagCloudComponent.TRAILING_CHARS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return name;
    }


    /**
     * this method returns maximum characters from the tag name and is used to
     * help Velocity templates
     *
     * @param maxChars
     * @return maxChars from the tag name
     */
    public String getName(int maxChars) {
        return getAnyName(name, maxChars);
    }

    /**
     * this method returns maximum characters from the tag name and is used to
     * help Velocity templates
     *
     * @param maxChars
     * @param trailingChars
     * @return maxChars from the tag name
     */
    public String getName(int maxChars, String trailingChars) {
        return getAnyName(name, maxChars, trailingChars);
    }

    /**
     * Gets the encoded name of this tag.
     *
     * @return the name as a String
     */
    public String getEncodedName() {
        return encodedName;
    }

    public String getEncodedName(int maxChars, String trailingChars) {
        return getAnyName(encodedName, maxChars, trailingChars);
    }

    public String getEncodedName(int maxChars) {
        return getAnyName(encodedName, maxChars);
    }


    /**
     * Sets the name of this tag.
     *
     * @param name the new tag name
     */
    public void setName(String name) {
        this.name = name;
        this.encodedName = encode(name);
    }

    /**
     * Gets the permalink for this object.
     *
     * @return a URL as a String
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Gets the hashcode of this object.
     *
     * @return the hashcode as an int
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Determines whether the specified object is equal to this one.
     *
     * @param o the object to compare against
     * @return true if Object o represents the same tag, false otherwise
     */
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) {
            return false;
        }

        Tag tag = (Tag) o;
        return tag.getName().equals(name);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this Object.
     */
    public int compareTo(Object o) {
        Tag tag = (Tag) o;
        return getName().compareTo(tag.getName());
    }

    /**
     * Returns a String representation of this object.
     *
     * @return a String
     */
    public String toString() {
        return this.name;
    }


    /**
     * Gets the number of article entries associated with this tag.
     *
     * @return an int
     */
    public long getPopularity() {
        return this.popularity;
    }

    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }

    /**
     * Gets the rank for this tag.
     *
     * @return an int representing the tag rank value;
     */
    public int getRank() {
        return this.rank;
    }

    /**
     * @param maxPopularity
     * @param normalizer
     */
    public void calculateRank(long maxPopularity, int normalizer) {
        if (maxPopularity == 0 || getPopularity() == 0) {
            this.rank = 1;
        } else {
            //double factor = maxPopularity / normalizer;
            // rank = (int) Math.ceil((double) getMaxPopularity() / factor);
            // smoother distribution:
            rank = (int) Math.ceil((double) getPopularity() * (double) normalizer / maxPopularity);

            if (rank < 1) {
                this.rank = 1;
            } else if (rank > normalizer) {
                this.rank = normalizer;
            }
        }
    }

    /**
     * Sets the rank for this tag.
     *
     * @param maxPopularity a long representing the maximum value for popularity for this tag
     */
    public void calculateRank(long maxPopularity) {
        calculateRank(maxPopularity, 10);
    }

    /**
     * Given a string containing comma separated tags, this method returns a
     * List containing the tags.
     *
     * @param tags a comma separated list of tags
     * @return a List of Tag instances
     * @param article
     */
    public static List parse(Article article, String tags) {
        List list = new ArrayList();

        if (tags != null && tags.trim().length() > 0) {
            String s[] = tags.split(",");
            for (int i = 0; i < s.length; i++) {
                Tag tag = article.getTag(s[i].trim());
                if (!list.contains(tag)) {
                    list.add(tag);
                }
            }
        }

        return list;
    }

    /**
     * Encodes a tag.
     *
     * @param tag a String
     * @return
     */
    public String encode(String tag) {
        if (tag == null) {
            return TagCloudComponent.EMPTY_STRING;
        } else {
            return tag.trim().replaceAll(TagCloudComponent.BLANK, "\\+");
        }
    }

    public void setProperty(String propertyName, Object propertyValue) {
        if (properties == null)
            properties = new HashMap();
        properties.put(propertyName, propertyValue);
    }

    public Map getProperties() {
        return properties;
    }
}
