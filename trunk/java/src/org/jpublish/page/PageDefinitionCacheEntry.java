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

package org.jpublish.page;

/** A cache entry for a PageDefinition.  Each page definition should have
    one corresponding cache entry.  The cache entry must be reloaded if 
    the last modification date of the page definition in the file system 
    does not match the last modification date in the cache entry.

    @author Anthony Eden
*/

public class PageDefinitionCacheEntry{

    private long lastModified;
    private PageDefinition pageDefinition;

    /** Construct a new PageCacheEntry for the given page.

        @param pageDefinition The page definition
        @param lastModified The last modification time in milliseconds
    */

    public PageDefinitionCacheEntry(PageDefinition pageDefinition,
                                    long lastModified){
        this.pageDefinition = pageDefinition;
        this.lastModified = lastModified;
    }

    /** Get the last modification time.  This value should be compared
        against the file system's last modification time.

        @return The last modification time
    */

    public long getLastModified(){
        return lastModified;
    }

    /** Set the last modification time (in milliseconds).

        @param lastModified The new last modification time
    */

    public void setLastModified(long lastModified){
        this.lastModified = lastModified;
    }

    /** Get the page definition for this cache entry.

        @return The PageDefinition
    */

    public PageDefinition getPageDefinition(){
        return pageDefinition;
    }

}
