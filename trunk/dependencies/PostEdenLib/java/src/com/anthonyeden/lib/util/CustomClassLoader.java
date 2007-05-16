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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomClassLoader extends ClassLoader{
    
    private static final Log log = LogFactory.getLog(CustomClassLoader.class);

    private File searchDirectory;

    /** Construct a new CustomClassLoader which searches for classes in the
        given directory.
        
        @param searchDirectory The root directory where classes may be found
    */

    public CustomClassLoader(String searchDirectory){
        this(new File(searchDirectory));
    }
    
    /** Construct a new CustomClassLoader which searches for classes in the parent
        ClassLoader first and then in the given directory.
        
        @param searchDirectory The root directory where classes may be found
        @param parent The parent ClassLoader
    */

    public CustomClassLoader(String searchDirectory, ClassLoader parent){
        this(new File(searchDirectory), parent);
    }

    /** Construct a new CustomClassLoader which searches for classes in the
        given directory.
        
        @param searchDirectory The root directory where classes may be found
    */

    public CustomClassLoader(File searchDirectory){
        super();
        this.searchDirectory = searchDirectory;
    }
    
    /** Construct a new CustomClassLoader which searches for classes in the parent
        ClassLoader first and then in the given directory.
        
        @param searchDirectory The root directory where classes may be found
        @param parent The parent ClassLoader
    */
    
    public CustomClassLoader(File searchDirectory, ClassLoader parent){
        super(parent);
        this.searchDirectory = searchDirectory;
    }
    
    /** Get the directory which is searched for class files.
    
        @return The search directory
    */

    public File getSearchDirectory(){
        return searchDirectory;
    }
    
    /** Set the directory which is searched for class files.
    
        @param searchDirectory The new search directory
    */
    
    public void setSearchDirectory(File searchDirectory){
        this.searchDirectory = searchDirectory;
    }
    
    /** Find the class with the given class name.
    
        @param name The class name
        @return the Class object
        @throws ClassNotFoundException
    */

    public Class findClass(String name) throws ClassNotFoundException{
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }
    
    /** Find the resource URL with the given name.  If the resource URL
        cannot be found then this method returns null.
    
        @param name The resource name
        @return The URL
    */
    
    public URL findResource(String name){
        File file = findFile(name);
        if(file == null){
            return null;
        }
        
        try{
            String urlString = "jar:" + file.toURL() + "!/" + name;
            log.debug("URL: " + urlString);
        
            return new URL(urlString);
        } catch(MalformedURLException e){
            log.error("Malformed URL error: " + e.getMessage()); 
            return null;
        }
    }
    
    /** Load the class data for the given name.
    
        @param name The class name
        @return The class data as a byte array
        @throws ClassNotFoundException
    */
    
    private byte[] loadClassData(String name) throws ClassNotFoundException{
        log.debug("Loading class data for " + name);
    
        String path = name.replace('.', '/') + ".class";
        
        byte[] data = loadData(path);
        if(data == null){
            throw new ClassNotFoundException("Cannot find class: " + name);
        }
        
        return data;
    }
    
    /** Find the ZIP or JAR file which contains the given path.
    
        @param path The path
        @return The FIle
    */
    
    private File findFile(String path){
        File[] files = searchDirectory.listFiles();
        for(int i = 0; i < files.length; i++){
            if(files[i].getName().endsWith(".jar") || 
               files[i].getName().endsWith(".zip"))
            {
                //log.debug("Looking for data in " + files[i]);
                
                try{
                    ZipFile zipFile = new ZipFile(files[i]);
                    ZipEntry zipEntry = zipFile.getEntry(path);
                    
                    if(zipEntry != null){
                        return files[i];
                    }
                } catch(IOException e){
                    log.error("IO error: " + e.getMessage());
                }
            }
        }
        return null;
    }
    
    /** Load the data from the given path.  This method first tries to find
        the path in any JAR or ZIP files in the specified directory.  If a JAR
        or ZIP file is found then the data is read from the file and returned
        as a byte array.
    
        @param path The path
        @return The byte array of data
    */
    
    private byte[] loadData(String path){
        File file = findFile(path);
        if(file == null){
            return null;
        }
        
        try{
            ZipFile zipFile = new ZipFile(file);
            ZipEntry zipEntry = zipFile.getEntry(path);
                    
            long dataSize = zipEntry.getSize();
            log.debug("ZIP entry size: " + dataSize);
                        
            ArrayList dataList = new ArrayList();
            InputStream in = zipFile.getInputStream(zipEntry);
            int b = -1;
            while((b = in.read()) != -1){
                dataList.add(new Byte((byte)b));
            }
                
            byte[] data = new byte[dataList.size()];
            for(int j = 0; j < data.length; j++){
                data[j] = ((Byte)dataList.get(j)).byteValue();
            }
                
            return data;
        } catch(IOException e){
            log.error("IO error: " + e.getMessage());
        }

        return null;
    }

}
