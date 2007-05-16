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

package com.anthonyeden.lib.util;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** The IconManager provides a central location for loading and caching
    Icons loaded from the class loader as resources.
    
    @author Anthony Eden
*/

public class IconManager{
    
    private static Log log = LogFactory.getLog(IconManager.class);
    private static String defaultBasePath = "";
    private static HashMap icons;
    
    private String basePath = "";

    /** Default constructor. The base path will be set to the value returned from
        the <code>getDefaultBasePath()</code> method.
    */

    public IconManager(){
        setBasePath(getDefaultBasePath());
    }
    
    /** Construct an IconManager with the given base path.
    
        @param basePath The base path
    */
    
    public IconManager(String basePath){
        setBasePath(basePath);
    }
    
    /** Get the base path for finding icons.  The base path should be a relative
        path with the '/' character as a separator.
    
        @return The base path
    */
    
    public String getBasePath(){
        return basePath;
    }
    
    /** Set the base path for finding icons.  The base path should be a relative
        path with the '/' character as a separator.
    
        @param basePath The base path
    */
    
    public void setBasePath(String basePath){
        if(basePath == null){
            basePath = "";
        }
        this.basePath = basePath;
    }
    
    /** Get the icon at the given path.  The path should point to a GIF in the class
        path.  If no image is located at the given location, then this method returns
        null.
        
        @return An icon or null
    */

    public Icon getIcon(String path){
        path = basePath + path;
    
        Map icons = getIcons();
        Icon icon = (Icon)icons.get(path);
        if(icon == null){
            log.debug("Loading icon: " + path);
            URL resourceURL = ClassUtilities.getResource(path, this);
            if(resourceURL != null){
                log.debug("Icon resource URL: " + resourceURL);
                icon = new ImageIcon(resourceURL);
                icons.put(path, icon);
            } else {
                return null;
            }
        }
        return icon;
    }
    
    /** Get the default base path for loading icons.  The value
        of defaultBasePath will be appended to the <code>path</code>
        argument of <code>getIcon</code> if the base path of the current
        IconManager instance is not set.  By default the value is an
        empty String.
    
        @return The default base path
    */
    
    public static String getDefaultBasePath(){
        return defaultBasePath;
    }
    
    /** Set the default base path for loading icons. The value
        of defaultBasePath will be appended to the <code>path</code>
        argument of <code>getIcon</code> if the base path of the current
        IconManager instance is not set.  By default the value is an
        empty String.
        
        @param _defaultBasePath The default base path
    */
    
    public static void setDefaultBasePath(String _defaultBasePath){
        defaultBasePath = _defaultBasePath;
    }
    
    /** Get a Map of all cached icons.
    
        @return The cached icon Map
    */
    
    protected static synchronized Map getIcons(){
        if(icons == null){
            icons = new HashMap();
        }
        return icons;
    }

}
