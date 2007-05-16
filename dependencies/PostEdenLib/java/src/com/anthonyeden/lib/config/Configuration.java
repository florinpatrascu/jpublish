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

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/** 
 * The Configuration interface represents a node in a configuration tree.
 *
 * @author Anthony Eden
 */

public interface Configuration {
    
    /** 
     * Get the node's name.
     *  
     * @return The node's name
     */
    
    public String getName();
    
    /** 
     * Get the parent configuration object.  This method will return null
     * if this configuration object is the top-most configuration object
     * in the configuration tree.
     * 
     * @return The parent configuration object or null
     */
    
    public Configuration getParent();

    /** 
     * Get the child configuration object with the given name.  If the
     * child with the name does not exist then this method returns
     * null.  If more than one child with the given name exists then
     * this method returns the first child.
     *  
     * @param name The child name
     * @return The first named child or null
     */

    public Configuration getChild(String name);
    
    /** 
     * Get the value of the first child configuration object with the 
     * given name.  If the child cannot be found or the child had no
     * data then this method returns null.
     *   
     * @param name The child name
     * @return The value or null
     */
    
    public String getChildValue(String name);
    
    /** 
     * Get the value of the first child configuration object with the 
     * given name.  If the child cannot be found or the child had no
     * data then this method returns the given default value.
     *  
     * @param name The child name
     * @param defaultValue The default value
     * @return The value
     */
    
    public String getChildValue(String name, String defaultValue);
    
    /** 
     * Get a list of all child nodes.
     * 
     * @return A List of Configuration objects
     */

    public List getChildren();
    
    /** 
     * Get a list of all child nodes with the given name.
     * 
     * @param name The child node name
     * @return A List of Configuration objects
     */
    
    public List getChildren(String name);
    
    /** 
     * Get a List of attribute names.
     * 
     * @return A List of attribute names
     */
    
    public List getAttributeNames();
    
    /** 
     * Get the named attribute or null.
     * 
     * @param name The attribute name
     * @return The attribute value
     */
    
    public String getAttribute(String name);
    
    /** 
     * Get the named attribute.  If the attribute is not found then
     * return the given default value.
     * 
     * @param name The attribute name
     * @param defaultValue The default value
     * @return The attribute value
     */
    
    public String getAttribute(String name, String defaultValue);
    
    /** 
     * Get the node's value or null if the node contains no data.
     * 
     * @return The node value or null
     */
    
    public String getValue();
    
    /** 
     * Get the node's value.  If the node contains no data then return
     * the given default value.
     * 
     * @param defaultValue The default value
     * @return The node value
     */
    
    public String getValue(String defaultValue);

    public Location getLocation();
    public void setLocation( String id);
    

    
}
