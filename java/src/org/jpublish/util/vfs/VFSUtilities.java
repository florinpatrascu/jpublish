/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jpublish.util.vfs;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.StringTokenizer;

/** Utilities which can be used with the JPublish Virtual File System.

    @author Anthony Eden
    @since 1.3
*/

public class VFSUtilities{
    
    private VFSUtilities(){
        // no op
    }
    
    public static VFSFile findRoot(VFSFile file){
        VFSFile parent = file.getParent();
        while(parent != null){
            file = parent;
            parent = file.getParent();
        }
        return file;
    }
    
    public static VFSFile pathToFile(String path, VFSFile root)
    throws FileNotFoundException{
        if(path.equals("/")){
            return root;
        }
        
        VFSFile parent = root;
        
        StringTokenizer tk = new StringTokenizer(path, "\\/");
        while(tk.hasMoreTokens()){
            String pathPart = tk.nextToken();
            Iterator children = parent.getChildren().iterator();
            while(children.hasNext()){
                VFSFile file = (VFSFile)children.next();
                if(file.getName().equals(pathPart)){
                    if(tk.hasMoreTokens()){
                        parent = file;
                    } else {
                        return file;
                    }
                }
            }
        }
        
        if(path.endsWith("/")){
            return parent;
        } else {
            throw new FileNotFoundException("Cannot find the file: " + path);
        }
    }
    
    public static Hashtable vfsToHash(VFSFile file){
        //log.info("Converting VFS to Hashtable");
        //log.info("VFS Root: " + file);
        
        Hashtable fileHash = new Hashtable();
        fileHash.put("name", file.getName());
        fileHash.put("isDirectory", new Boolean(file.isDirectory()));
        
        Vector childVector = new Vector();
        Iterator children = file.getChildren().iterator();
        while(children.hasNext()){
            childVector.addElement(vfsToHash((VFSFile)children.next()));
        }
        fileHash.put("children", childVector);
        
        //log.info("Returning file as hashtable");
        
        return fileHash;
    }
    
    public static VFSFile hashToVFS(Hashtable hash){
        return hashToVFS(hash, null);
    }
    
    protected static VFSFile hashToVFS(Hashtable hash, VFSFile parent){
        //log.info("hashToVFS()");
        String name = (String)hash.get("name");
        boolean isDirectory = ((Boolean)hash.get("isDirectory")).booleanValue();
        
        //log.debug("Creating VFSFile");
        VFSFile file = new VFSFile(name, parent, isDirectory);
        
        List children = file.getChildren();
        Iterator iter = ((Vector)hash.get("children")).iterator();
        while(iter.hasNext()){
            VFSFile child = hashToVFS((Hashtable)iter.next(), file);
            children.add(child);
        }
        
        //log.info("Returning file: " + file);
        return file;
        
    }
    
}
