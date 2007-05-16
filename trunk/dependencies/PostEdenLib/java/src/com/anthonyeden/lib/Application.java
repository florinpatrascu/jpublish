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

package com.anthonyeden.lib;

import java.io.File;
import java.awt.Component;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.util.ClassUtilities;

/**	This class provides basic services which all applications may find useful.  
    The services include location or creation of the home directory,
    installation of additional look and feels, and a mutable map of application
    attributes.
	
	@author Anthony Eden
*/

public class Application{
    
    private static final Log log = LogFactory.getLog(Application.class);
    private static final String[] additionalLookAndFeels = {
		"com.incors.plaf.kunststoff.KunststoffLookAndFeel"
	};
	
	private static HashMap apps = new HashMap();
	
	private ArrayList windows = new ArrayList();
    private HashMap attributes = new HashMap();
	private File homeDirectory;
    private FileObject vfsHomeDirectory;
	private String appName;

	/**	Construct an application with the given name.
	
		@param appName The name of the application
	*/

	public Application(String appName){
		this.appName = appName;
		this.homeDirectory = new File(
            System.getProperty("user.home"), "." + appName);
	}
	
	/**	Get a instance of an Application object for the named application.  If 
        the application does not exist then one will be created.
	
		@return The Application instance
	*/
	
	public static synchronized Application getInstance(String appName){
		Application app = (Application)apps.get(appName);
		if(app == null){
			app = new Application(appName);
			apps.put(appName, app);
		}
		return app;
	}
	
	/**	Get the current user's home directory.  If the directory does
		not exist then it will be created.
		
		@return The home directory
	*/

	public synchronized File getHomeDirectory(){
		if(!homeDirectory.exists()){
			homeDirectory.mkdirs();
		}
		return homeDirectory;
	}
    
    /** Get the current user's virtual home directory.  If the virtual 
        directory does not exist then it will be created.
        
        @return The virtual home directory
        @throws FileSystemException
    */
    
    public synchronized FileObject getVFSHomeDirectory() 
    throws FileSystemException{
        if(!vfsHomeDirectory.exists()){
			vfsHomeDirectory.createFolder();
		}
		return vfsHomeDirectory;
    }
	
	/**	Initialize the application using the given command line arguments.
	
		@param args The command line arguments
	*/

	public void initialize(String[] args){
		// install additional Look and Feels
		for(int i = 0; i < additionalLookAndFeels.length; i++){
			try{
				String className = additionalLookAndFeels[i];
                LookAndFeel lookAndFeel = 
                    (LookAndFeel)ClassUtilities.loadClass(
                    className, this).newInstance();
				UIManager.installLookAndFeel(lookAndFeel.getName(), className);
			} catch(Exception e){
				log.error("Error installing look and feel: " + e.getMessage());
			}
		}
	}
	
	/**	Get a list of open window objects.
	
		@return A List of window objects
	*/
	
	public List getWindows(){
		return windows;
	}
	
	/**	Add a window to the list of window objects.
	
		@param window The window
	*/
	
	public void addWindow(Component window){
		windows.add(window);
		
		log.debug("Window added");
	}
	
	/**	Remove a window from the list of window objects.
	
		@param window The window
	*/
	
	public void removeWindow(Component window){
		windows.remove(window);
		
		log.debug("Window removed");
		log.debug("Window count: " + windows.size());
		
		if(windows.size() == 0){
			System.exit(0);
		}
	}
    
    /** Get the attribute for the specified key.
    
        @param key The attribute key
        @return The attribute value or null
    */
    
    public Object getAttribute(Object key){
        return attributes.get(key);
    }
    
    /** Set the attribute.
    
        @param key The attribute key
        @param value The attribute value
    */
    
    public void setAttribute(Object key, Object value){
        attributes.put(key, value);
    }
    
    /** Return an Iterator for all attribute keys.
    
        @return An Iterator of attribute keys
    */
    
    public Iterator getAttributeKeys(){
        return attributes.keySet().iterator();
    }
    
    /** Remove the specified attribute.
    
        @param key The attribute key
        @return The removed object or null
    */
    
    public Object removeAttribute(Object key){
        return attributes.remove(key);
    }
    
    /** Clear all attributes. */
    
    public void clearAttributes(){
        attributes.clear();
    }

}
