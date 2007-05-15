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

package org.jpublish.template.filesystem;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jpublish.JPublishRuntimeException;

/**	Iterator which uses the paths returned by the wrapped iterator to
	load Template objects from the specified template manager.  This iterator
	depends on a file tree Iterator to perform the actual iteration. 
	
	@author Anthony Eden
	@since 1.1
*/

public class FileSystemTemplateIterator implements Iterator{
    
    private static final Log log = LogFactory.getLog(
		FileSystemTemplateIterator.class);
	
	private FileSystemTemplateManager templateManager;
	private Iterator iter;
	
	/**	Construct a new FileSystemTemplateIterator which is backed by
		the specified FileSystemTemplateManager.
		
		@param templateManager The FileSystemTemplateManager
		@param iter The wrapped Iterator
	*/

	public FileSystemTemplateIterator(FileSystemTemplateManager templateManager, 
	Iterator iter){
		this.templateManager = templateManager;
		this.iter = iter;
	}
	
	/** Returns true if the iteration has more elements. (In other words, 
		returns true if next would return an element rather than throwing 
		an exception.)
		
		@return True if the iteration has more elements
	*/

	public boolean hasNext(){
		return iter.hasNext();
	}
	
	/**	Returns the next element in the iteration.
	
		@return The next element in the iteration
	*/
	
	public Object next(){
		String root = templateManager.getRoot().toString();
		File file = (File)iter.next();
		String path = file.toString().substring(root.length());
		log.info("Next path: " + path);
		
		try{
			return templateManager.getTemplate(path);
		} catch(Exception e){
			log.error("Error getting template: " + path);
			throw new JPublishRuntimeException(e.getMessage(), e);
		}
	}
	
	/**	Removes from the underlying collection the last element returned by 
		the iterator (optional operation). This method can be called only 
		once per call to next. The behavior of an iterator is unspecified 
		if the underlying collection is modified while the iteration is in 
		progress in any way other than by calling this method. 

		@throws UnsupportedOperationException
	*/
	
	public void remove(){
		throw new UnsupportedOperationException();
	}
	
}
