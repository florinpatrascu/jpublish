/*-- 

 Copyright (C) 2000-2003 Anthony Eden.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "EdenLib" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact me@anthonyeden.com.
 
 4. Products derived from this software may not be called "EdenLib", nor
    may "EdenLib" appear in their name, without prior written permission
    from Anthony Eden (me@anthonyeden.com).
 
 In addition, I request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 For more information on EdenLib, please see <http://edenlib.sf.net/>.
 
 */

package com.anthonyeden.lib.config;

import org.znerd.xmlenc.XMLOutputter;

import java.io.*;
import java.util.*;

/**
 * The ConfigurationBase is a base implementation of the MutableConfiguration
 * interface.
 *
 * @author Anthony Eden
 */

public class ConfigurationBase implements MutableConfiguration {

    public static final String ENCODING = "UTF-8";
    private static final String EMPTY_STRING = "";

    private String name;
    private Object value;
    private List children;
    private Map attributes;
    private Configuration parent;
    private Location location;

    /**
     * Construct a new ConfigurationBase object.
     *
     * @param name   The configuration object name
     * @param value  The configuration object value
     * @param parent The parent Configuration
     */

    public ConfigurationBase(String name, Object value, Configuration parent) {
        this(name, value, new ArrayList(), new HashMap(), parent, null);
    }

    /**
     * Construct a new ConfigurationBase object.
     *
     * @param name     The configuration object name
     * @param value    The configuration object value
     * @param parent   The parent Configuration
     * @param location The Location object or null
     */

    public ConfigurationBase(String name, Object value, Configuration parent,
                             Location location) {
        this(name, value, new ArrayList(), new HashMap(), parent, location);
    }

    /**
     * Construct a new ConfigurationBase object.
     *
     * @param name       The configuration object name
     * @param value      The configuration object value
     * @param children   The List of children
     * @param attributes The Map of attributes
     * @param parent     The parent Configuration
     */

    public ConfigurationBase(String name, Object value, List children,
                             Map attributes, Configuration parent) {
        this(name, value, children, attributes, parent, null);
    }

    /**
     * Construct a new ConfigurationBase object.
     *
     * @param name       The configuration object name
     * @param value      The configuration object value
     * @param children   The List of children
     * @param attributes The Map of attributes
     * @param parent     The parent Configuration
     * @param location   The Location or null
     */

    public ConfigurationBase(String name, Object value, List children,
                             Map attributes, Configuration parent, Location location) {
        this.name = name;
        this.value = value;
        this.children = children;
        this.attributes = attributes;
        this.parent = parent;
        this.location = location;
    }

    /**
     * Get the node's name.
     *
     * @return The node's name
     */

    public String getName() {
        return name;
    }

    /**
     * Set the node name.
     *
     * @param name The new node name
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the parent configuration object.  This method will return null
     * if this configuration object is the top-most configuration object
     * in the configuration tree.
     *
     * @return The parent configuration object or null
     */

    public Configuration getParent() {
        return parent;
    }

    /**
     * Get the child configuration object with the given name.  If the
     * child with the name does not exist then this method returns
     * null.  If more than one child with the given name exists then
     * this method returns the first child.
     *
     * @param name The child name
     * @return The first named child or null
     */

    public Configuration getChild(String name) {
        Iterator iter = children.iterator();
        while (iter.hasNext()) {
            Configuration child = (Configuration) iter.next();
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Get the value of the first child configuration object with the
     * given name.  If the child cannot be found or the child had no
     * data then this method returns null.
     *
     * @param name The child name
     * @return The value or null
     */

    public String getChildValue(String name) {
        Configuration child = getChild(name);
        if (child == null) {
            return null;
        } else {
            return child.getValue();
        }
    }

    /**
     * Get the value of the first child configuration object with the
     * given name.  If the child cannot be found or the child had no
     * data then this method returns the given default value.
     *
     * @param name         The child name
     * @param defaultValue The default value
     * @return The value
     */

    public String getChildValue(String name, String defaultValue) {
        Configuration child = getChild(name);
        if (child == null) {
            return defaultValue;
        } else {
            String value = child.getValue();
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        }
    }

    /**
     * Get a list of all child nodes.
     *
     * @return A List of Configuration objects
     */

    public List getChildren() {
        return children;
    }

    /**
     * Get a list of all child nodes with the given name.
     *
     * @param name The child node name
     * @return A List of Configuration objects
     */

    public List getChildren(String name) {
        List filteredChildren = new ArrayList();
        Iterator iter = children.iterator();
        while (iter.hasNext()) {
            Configuration child = (Configuration) iter.next();
            if (child.getName().equals(name)) {
                filteredChildren.add(child);
            }
        }
        return filteredChildren;
    }

    /**
     * Add a child node with no child value to the configuration.  This
     * method should be the same as calling
     * <code>addChild(name, null)</code>
     *
     * @param name The name of the new configuration node
     * @return The configuration node
     */

    public MutableConfiguration addChild(String name) {
        ConfigurationBase child = new ConfigurationBase(name, EMPTY_STRING, this);
        children.add(child);
        return child;
    }

    /**
     * Add a child node to the configuration.  The value's toString() method
     * will be used to convert the value to a String.
     *
     * @param name  The name of the new configuration node
     * @param value The value of the new configuration node
     * @return The configuration node
     */

    public MutableConfiguration addChild(String name, Object value) {
        ConfigurationBase child = new ConfigurationBase(name, value, this);
        children.add(child);
        return child;
    }

    /**
     * Add the configuration object as a child of this configuration object.
     *
     * @param configuration The child configuration object
     */

    public void addChild(Configuration configuration) {
        children.add(configuration);
    }

    /**
     * Remove the specified configuration object.
     *
     * @param configuration The child configuration object
     */

    public void removeChild(Configuration configuration) {
        children.remove(configuration);
    }

    /**
     * Add an attribute with the given name.  The value's toString() method
     * will be used to convert the value to a String.
     *
     * @param name  The attribute name
     * @param value The attribute value
     */

    public void addAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Set the configuration object's value.
     *
     * @param value The new value
     */

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Remove all of the children of this configuration node.
     */

    public void clearChildren() {
        children.clear();
    }

    /**
     * Get a List of attribute names.
     *
     * @return A List of attribute names
     */

    public List getAttributeNames() {
        List attributeNames = new ArrayList();
        Iterator attributeKeys = attributes.keySet().iterator();
        while (attributeKeys.hasNext()) {
            attributeNames.add((String) attributeKeys.next());
        }
        return attributeNames;
    }

    /**
     * Get the named attribute or null.
     *
     * @param name The attribute name
     * @return The attribute value
     */

    public String getAttribute(String name) {
        Object value = attributes.get(name);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * Get the named attribute.  If the attribute is not found then
     * return the given default value.
     *
     * @param name         The attribute name
     * @param defaultValue The default value
     * @return The attribute value
     */

    public String getAttribute(String name, String defaultValue) {
        Object value = getAttribute(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

    /**
     * Get the node's value or null if the node contains no data.
     *
     * @return The node value or null
     */

    public String getValue() {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * Get the node's value.  If the node contains no data then return
     * the given default value.
     *
     * @param defaultValue The default value
     * @return The node value
     */

    public String getValue(String defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

    /**
     * Get location information for this configuration object.
     *
     * @return Location
     */

    public Location getLocation() {
        return location;
    }

    /**
     * Save the configuration data to the specified output stream.  Please note
     * that the caller must close the OutputStream.
     *
     * @param out The OutputStream
     * @throws ConfigurationException
     */

    public void save(OutputStream out) throws ConfigurationException {
        save(new OutputStreamWriter(out));
    }

    /**
     * Save the configuration data to the specified output stream.  Please note
     * that the caller must close the writer.
     *
     * @param out The Writer
     * @throws ConfigurationException
     */

    public void save(Writer out) throws ConfigurationException {
        try {
            BufferedWriter bout = new BufferedWriter(out);
            XMLOutputter xmlOut = new XMLOutputter(bout, ENCODING);
            write(this, xmlOut);
            xmlOut.endDocument();
        } catch (IOException e) {
            throw new ConfigurationException(
                    "IO error while writing configuration", e);
        }
    }

    public void setLocation(String id) {
        if (id != null)
            location.setSourceId(id);
    }

    /**
     * Write the given Configuration object to the XMLOutputter.  This method
     * is recursive and will call itself until all children are printed.
     *
     * @param configuration The Configuration object
     * @param xmlOut        The XMLOutputter
     * @throws IOException
     */

    void write(Configuration configuration, XMLOutputter xmlOut)
            throws IOException {
        xmlOut.startTag(configuration.getName());
        xmlOut.pcdata(configuration.getValue());

        Iterator attributeNames = configuration.getAttributeNames().iterator();
        while (attributeNames.hasNext()) {
            String attributeName = (String) attributeNames.next();
            String attributeValue = configuration.getAttribute(attributeName);
            xmlOut.attribute(attributeName, attributeValue);
        }

        Iterator children = configuration.getChildren().iterator();
        while (children.hasNext()) {
            Configuration child = (Configuration) children.next();
            write(child, xmlOut);
        }

        xmlOut.endTag();
    }

    /**
     * This would be a deep copy if you used immutable objects such as Strings for values and attributes,
     * and shallow otherwise.
     * It clones the Location object, however it sets the parent to null.
     *
     * @return copy of this Configuration
     */
    public Configuration copy() {
        return copy(null);
    }

    /**
     * This would be a deep copy if you used immutable objects such as Strings for values and attributes,
     * and shallow otherwise.
     * It clones the Location object, however it sets the parent to null.
     *
     * @return copy of this Configuration
     */
    public Configuration copy(Configuration parentConfig) {
        ArrayList childrenCopy = new ArrayList();
        HashMap attributesCopy = new HashMap();
        Iterator attributeKeys = attributes.keySet().iterator();
        while (attributeKeys.hasNext()) {
            Object key = attributeKeys.next();
            attributesCopy.put(key, attributes.get(key));
        }
        Iterator childIter = children.iterator();
        while (childIter.hasNext()) {
            childrenCopy.add(((Configuration) childIter.next()).copy());
        }
        Location locationCopy = null;
        if (this.location != null) {
            locationCopy = (Location) location.clone();
        }
        return new ConfigurationBase(this.name, this.value, childrenCopy, attributesCopy, parentConfig, locationCopy);
    }
}
