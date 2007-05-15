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

package org.jpublish.page.servletcontext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishRuntimeException;
import org.jpublish.util.PathUtilities;

import java.util.Iterator;

/** Iterator which iterates through the Page objects known to the specified
	PageManager.  This implementation wraps an iterator which returns paths
	and loads the Page object for each path.  The wrapped Iterator must return 
    Strings.
	
	@author Anthony Eden
	@since 2.0
*/

public class ServletContextPageIterator implements Iterator{
    
    private static final Log log = LogFactory.getLog(
		ServletContextPageIterator.class);
	
	private ServletContextPageManager pageManager;
	private Iterator iter;
	
	/**	Construct a new ServletContextPageIterator.
	
		@param pageManager The PageManager
		@param iter The Iterator to wrap
	*/

	public ServletContextPageIterator(ServletContextPageManager pageManager, 
	Iterator iter){
		this.pageManager = pageManager;
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
		String root = PathUtilities.toResourcePath(pageManager.getRoot());
		String path = (String)iter.next();
		String relativePath = path.substring(root.length());
		
		try{
			return pageManager.getPage(relativePath);
		} catch(Exception e){
			log.error("Error getting page: " + relativePath);
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
