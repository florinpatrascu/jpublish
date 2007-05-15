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

package org.jpublish.util;

import java.io.Serializable;

/**	A generic cache entry.
	
	@author Anthony Eden
	@since 2.0
*/

public class CacheEntry implements Serializable{
    
    private long lastModified;
	private Object object;

	/**	Construct a new TemplateCacheEntry for the given template.
	
		@param template The template
		@param lastModified The last modification time in milliseconds
	*/

	public CacheEntry(Object template, long lastModified){
		this.object = template;
		this.lastModified = lastModified;
	}
	
	/**	Get the last modification time.  This value should be compared
		against the file system's last modification time.
	
		@return The last modification time
	*/

	public long getLastModified(){
		return lastModified;
	}
	
	/**	Set the last modification time (in milliseconds).
	
		@param lastModified The new last modification time
	*/
	
	public void setLastModified(long lastModified){
		this.lastModified = lastModified;
	}
	
	/**	Get the object for this cache entry.
	
		@return The Object
	*/
	
	public Object getObject(){
		return object;
	}

}
