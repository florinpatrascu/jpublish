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

package org.jpublish.util.vfs.provider.filesystem;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jpublish.util.vfs.VFSFile;
import org.jpublish.util.vfs.VFSProvider;

/**	Implementation of the VFSProvider interface which is backed by the 
	local file system.
	
	@author Anthony Eden
	@since 1.3
*/

public class FileSystemProvider implements VFSProvider{
    
    private static final Log log = LogFactory.getLog(FileSystemProvider.class);
	
	private File root;
	private VFSFile virtualRoot;
	
	/**	Construct a new FileSystemProvider at the specified root.
	
		@param root The root
	*/
	
	public FileSystemProvider(File root){
		this.root = root;
		this.virtualRoot = new VFSFile("", null, true, this);
		refresh();
	}
	
	/**	Get the root of the VFS.
	
		@return The VFSFile representing the root
	*/
	
	public VFSFile getRoot(){
		return virtualRoot;
	}
	
	/**	Refresh the VFS. */
	
	public void refresh(){
		virtualRoot.getChildren().clear();
		build(root, virtualRoot);
	}
	
	/**	Build the virtual file system tree.
	
		@param file The parent file
		@param parent The virtual parent file
	*/
	
	protected void build(File parent, VFSFile virtualParent){
		log.info("Building file tree");
		File[] files = parent.listFiles();
		for(int i = 0; i < files.length; i++){
			VFSFile vfsFile = new VFSFile(
				files[i].getName(), virtualParent, files[i].isDirectory());
			virtualParent.getChildren().add(vfsFile);
			if(vfsFile.isDirectory()){
				build(files[i], vfsFile);
			}
		}
		log.info("Tree build complete");
	}
	
}
