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

import java.io.OutputStream;
import java.io.Writer;

/**
 * Interface implemented by configuration objects which are mutable.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public interface MutableConfiguration extends Configuration {

    /**
     * Set the node name.
     *
     * @param name The new node name
     */

    public void setName(String name);

    /**
     * Add a child node with no child value to the configuration.  This
     * method should be the same as calling
     * <code>addChild(name, null)</code>
     *
     * @param name The name of the new configuration node
     * @return The configuration node
     */

    public MutableConfiguration addChild(String name);

    /**
     * Add a child node to the configuration.  The value's toString() method
     * will be used to convert the value to a String.
     *
     * @param name  The name of the new configuration node
     * @param value The value of the new configuration node
     * @return The configuration node
     */

    public MutableConfiguration addChild(String name, Object value);

    /**
     * Add a child node to the configuration.  The value's toString() method
     * will be used to convert the value to a String. If the value is null use the
     * default value. [patch from Jakub]
     *
     * @param name         The name of the new configuration node
     * @param value        The value of the new configuration node
     * @param defaultValue value to use if the value was null
     * @return The configuration node
     */
    public MutableConfiguration addChild(String name, Object value, Object defaultValue);

    /**
     * Add the configuration object as a child of this configuration object.
     *
     * @param configuration The child configuration object
     */

    public void addChild(Configuration configuration);

    /**
     * Remove the specified configuration object.
     *
     * @param configuration The child configuration object
     */

    public void removeChild(Configuration configuration);

    /**
     * Add an attribute with the given name.  The value's toString() method
     * will be used to convert the value to a String.
     *
     * @param name  The attribute name
     * @param value The attribute value
     */

    public void addAttribute(String name, Object value);

    /**
     * Set the configuration object's value.
     *
     * @param value The new value
     */

    public void setValue(String value);

    /**
     * Remove all of the children of this configuration node.
     */

    public void clearChildren();

    /**
     * Save the configuration data to the specified output stream.  Not all
     * implementations will support writing configuration data.
     *
     * @param out The OutputStream
     * @throws ConfigurationException
     */

    public void save(OutputStream out) throws ConfigurationException;

    /**
     * Save the configuration data to the specified output stream.  Not all
     * implementations will support writing configuration data.
     *
     * @param out The Writer
     * @throws ConfigurationException
     */

    public void save(Writer out) throws ConfigurationException;

}

