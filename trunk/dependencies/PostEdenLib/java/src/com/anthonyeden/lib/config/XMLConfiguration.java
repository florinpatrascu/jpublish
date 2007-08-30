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

import com.anthonyeden.lib.config.sax.SAXConfigurationFactory;
import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.util.IOUtilities;
import org.apache.commons.vfs.*;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Implementation of the Configuration interface which uses an XML
 * document as its data source.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class XMLConfiguration implements MutableConfiguration {
    private Configuration configuration;
    private static final String EMPTY_STRING = "";


    /**
     * Construct an "anonymous" configuration object from the given XML String
     *
     * @param xmlString The xml string we want to load
     * @deprecated please use the XMLConfiguration(id, xmlString)
     */
    public XMLConfiguration(String xmlString) throws ConfigurationException {
        this(EMPTY_STRING, xmlString, ConfigurationBase.ENCODING);
    }

    /**
     * Construct a configuration object from the given XML String
     * with the given id.
     *
     * @param id
     * @param xmlString The xml string we want to load
     */
    public XMLConfiguration(String id, String xmlString) throws ConfigurationException {
        this(id, xmlString, ConfigurationBase.ENCODING);
    }

    /**
     * Construct a configuration object from the given XML String
     * with the given id.
     *
     * @param id        used to identify the parsing sequence
     * @param xmlString The xml string we want to load
     * @param encoding  character encoding
     * @throws ConfigurationException
     */

    public XMLConfiguration(String id, String xmlString, String encoding)
            throws ConfigurationException {

        try {
            load(id, new ByteArrayInputStream(xmlString.getBytes(encoding)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ConfigurationException("Unsupported encoding: " + encoding +
                    ", while parsing id: " + id + ", containing: " + xmlString);
        }
    }

    /**
     * Construct an XMLConfiguration from the given File.
     *
     * @param file The file
     * @throws ConfigurationException
     */

    public XMLConfiguration(File file) throws ConfigurationException {
        load(file);
    }

    public XMLConfiguration(String id, File file) throws ConfigurationException {
        load(id, file);
    }

    private void load(String id, File file) throws ConfigurationException {
        if (file == null) {
            throw new ConfigurationException("File cannot be null");
        }

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            load(id, in);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            IOUtilities.close(in);
        }

    }

    /**
     * Construct an XMLConfiguration from the given FileObject.
     *
     * @param file The file
     * @throws ConfigurationException
     */

    public XMLConfiguration(FileObject file) throws ConfigurationException {
        load(file);
    }

    /**
     * Construct an XMLConfiguration using the data at the given
     * URL.
     *
     * @param url The URL
     * @throws ConfigurationException
     */

    public XMLConfiguration(URL url) throws ConfigurationException {
        load(url);
    }

    /**
     * Construct an XMLConfiguration using the given InputStream.
     *
     * @param in The InputStream
     * @throws ConfigurationException
     */

    public XMLConfiguration(InputStream in) throws ConfigurationException {
        load(in);
    }

    /**
     * @param id
     * @param in
     * @throws ConfigurationException
     */
    public XMLConfiguration(String id, InputStream in) throws ConfigurationException {
        load(id, in);
    }

    /**
     * Construct an XMLConfiguration using the given reader.
     *
     * @param reader The Reader
     * @throws ConfigurationException
     */

    public XMLConfiguration(Reader reader) throws ConfigurationException {
        load(reader);
    }

    /**
     * Get the node's name.
     *
     * @return The node's name
     */

    public String getName() {
        return configuration.getName();
    }

    /**
     * Get the parent configuration object.  This method will return null
     * if this configuration object is the top-most configuration object
     * in the configuration tree.
     *
     * @return The parent configuration object or null
     */

    public Configuration getParent() {
        return configuration.getParent();
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
        return configuration.getChild(name);
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
        return configuration.getChildValue(name);
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
        return configuration.getChildValue(name, defaultValue);
    }

    /**
     * Get a list of all child nodes.
     *
     * @return A List of Configuration objects
     */

    public List getChildren() {
        return configuration.getChildren();
    }

    /**
     * Get a list of all child nodes with the given name.
     *
     * @param name The child node name
     * @return A List of Configuration objects
     */

    public List getChildren(String name) {
        //log.debug("getChildren(" + name + ")");
        return configuration.getChildren(name);
    }

    /**
     * Get a List of attribute names.
     *
     * @return A List of attribute names
     */

    public List getAttributeNames() {
        return configuration.getAttributeNames();
    }

    /**
     * Get the named attribute or null.
     *
     * @param name The attribute name
     * @return The attribute value
     */

    public String getAttribute(String name) {
        return configuration.getAttribute(name);
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
        return configuration.getAttribute(name, defaultValue);
    }

    /**
     * Get the node's value or null if the node contains no data.
     *
     * @return The node value or null
     */

    public String getValue() {
        return configuration.getValue();
    }

    /**
     * Get the node's value.  If the node contains no data then return
     * the given default value.
     *
     * @param defaultValue The default value
     * @return The node value
     */

    public String getValue(String defaultValue) {
        return configuration.getValue(defaultValue);
    }

    public Location getLocation() {
        return configuration.getLocation();
    }

    public void setLocation(String id) {
        if (id != null)
            configuration.setLocation(id);
    }

    // MutableConfiguration implementation

    /**
     * Set the node name.
     *
     * @param name The new node name
     */

    public synchronized void setName(String name) {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) configuration).setName(name);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Add a child node with no child value to the configuration.  This
     * method should be the same as calling
     * <code>addChild(name, "")</code>
     *
     * @param name The name of the new configuration node
     * @return The configuration node
     */

    public MutableConfiguration addChild(String name) {
        if (configuration instanceof MutableConfiguration) {
            return ((MutableConfiguration) configuration).addChild(name, EMPTY_STRING);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Add a child node to the configuration.
     *
     * @param name  The name of the new configuration node
     * @param value The value of the new configuration node
     * @return The configuration node
     */

    public synchronized MutableConfiguration addChild(String name, Object value) {
        if (configuration instanceof MutableConfiguration) {
            return ((MutableConfiguration) configuration).addChild(name, value);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Add the configuration object as a child of this configuration object.
     *
     * @param child The child configuration object
     */

    public synchronized void addChild(Configuration child) {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) configuration).addChild(child);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Add an attribute with the given name.
     *
     * @param name  The attribute name
     * @param value The attribute value
     */

    public synchronized void addAttribute(String name, Object value) {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) configuration).addAttribute(name, value);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Set the configuration object's value.
     *
     * @param value The new value
     */

    public synchronized void setValue(String value) {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) configuration).setValue(value);
        } else {
            throw new UnsupportedOperationException(
                    "This configuration is not mutable.");
        }
    }

    /**
     * Remove the specified configuration object.
     *
     * @param configuration The child configuration object
     */

    public void removeChild(Configuration configuration) {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) this.configuration).removeChild(configuration);
        } else {
            throw new UnsupportedOperationException("This configuration is not mutable.");
        }
    }

    /**
     * Remove all of the children of this configuration node.
     */

    public void clearChildren() {
        if (configuration instanceof MutableConfiguration) {
            ((MutableConfiguration) configuration).clearChildren();
        } else {
            throw new UnsupportedOperationException(
                    "This configuration is not mutable.");
        }
    }

    /**
     * Load the configuration from the file represented by the
     * given file name.  A File object is constructed using the
     * given String.  If the File exists then it is used to load
     * the configuration information.  If the File does not exist
     * then the classpath will be searched for the given file name.
     * <p/>
     * <p>The filename cannot be null.
     *
     * @param filename The filename
     * @throws ConfigurationException
     */

    public void load(String filename) throws ConfigurationException {
        if (filename == null) {
            throw new ConfigurationException("Filename cannot be null");
        }

        try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject file = fsManager.resolveFile(filename);
            if (file.exists()) {
                load(file);
            } else {
                InputStream in = ClassUtilities.getResourceAsStream(filename);
                if (in != null) {
                    try {
                        load(in);
                    } catch (Exception e) {
                        IOUtilities.close(in);
                    }
                } else {
                    throw new ConfigurationException("Configuration " +
                            filename + " not found");
                }
            }
        } catch (FileSystemException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Load the configuration from the given file.  The file
     * cannot be null.
     *
     * @param file The file
     * @throws ConfigurationException
     */

    public void load(File file) throws ConfigurationException {
        if (file == null) {
            throw new ConfigurationException("File cannot be null");
        }

        try {
            FileSystemManager fsManager = VFS.getManager();
            load(fsManager.toFileObject(file));
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Load the configuration from the given FileObject.  The FileObject
     * cannot be null.
     *
     * @param file The FileObject
     * @throws ConfigurationException
     */

    public void load(FileObject file) throws ConfigurationException {
        if (file == null) {
            throw new ConfigurationException("File cannot be null");
        }

        InputStream in = null;
        try {
            FileContent content = file.getContent();
            in = content.getInputStream();
            load(in);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            IOUtilities.close(in);
        }
    }

    /**
     * Load the configuration from the given URL.  The URL
     * cannot be null.
     *
     * @param url The URL
     * @throws ConfigurationException
     */

    public void load(URL url) throws ConfigurationException {
        if (url == null) {
            throw new ConfigurationException("URL cannot be null");
        }

        InputStream in = null;
        try {
            in = url.openStream();
            load(in);
        } catch (Exception e) {
            throw new ConfigurationException(e);
        } finally {
            IOUtilities.close(in);
        }
    }

    /**
     * Load the configuration from the given InputStream.  The
     * InputStream cannot be null.
     *
     * @param in The InputStream
     * @throws ConfigurationException
     */

    public void load(String id, InputStream in) throws ConfigurationException {
        ConfigurationFactory configFactory =
                SAXConfigurationFactory.getInstance();
        configuration = configFactory.getConfiguration(id, in);

    }

    public void load(InputStream in) throws ConfigurationException {
        load("", in);

    }

    /**
     * Load the configuration from the given Reader.  The reader
     * cannot be null.
     *
     * @param reader The Reader
     * @throws ConfigurationException
     */

    public void load(Reader reader) throws ConfigurationException {
        ConfigurationFactory configFactory =
                SAXConfigurationFactory.getInstance();
        configuration = configFactory.getConfiguration("", reader);

    }

    /**
     * Save the configuration data to the given OutputStream.
     *
     * @param out The OutputStream
     * @throws ConfigurationException
     */

    public void save(OutputStream out) throws ConfigurationException {
        if (!(configuration instanceof MutableConfiguration)) {
            throw new UnsupportedOperationException(
                    "Configuration is not mutable");
        }

        if (out == null) {
            throw new ConfigurationException("OutputStream cannot be null");
        }

        try {
            ((MutableConfiguration) configuration).save(out);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Save the configuration data to the given Writer.
     *
     * @param out The Writer
     * @throws ConfigurationException
     */

    public void save(Writer out) throws ConfigurationException {
        if (!(configuration instanceof MutableConfiguration)) {
            throw new UnsupportedOperationException(
                    "Configuration is not mutable");
        }

        if (out == null) {
            throw new ConfigurationException("Writer cannot be null");
        }

        try {
            ((MutableConfiguration) configuration).save(out);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Returns this XMLConfiguration as a string.
     * Internally it uses the save method, to obtain the string.
     *
     * @return this XMLConfiguration as a string
     * @throws ConfigurationException
     */
    public String toXMLString() throws ConfigurationException {
        StringWriter sw = new StringWriter();
        this.save(sw);
        return sw.toString();
    }

    /**
     * This is a deep copy of XMLConfiguration since atrributes and values are Strings and therefore immutable.
     * Unless of course you modified the object manually and entered complex objects as values or attribute values.
     * All this does is return the result of configuration.copy() call on the wrapped configuration object.
     *
     * @return copy of the wrapped Configuration object
     */
    public Configuration copy(){
        return this.configuration.copy();
    }
}
