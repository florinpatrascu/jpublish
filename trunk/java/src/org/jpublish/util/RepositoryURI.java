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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Implementation of the InternalURI interface which represents a content
    repository.
    
    @author Anthony Eden
    @since 2.0
*/

public class RepositoryURI extends InternalURI{
    
    private Log log = LogFactory.getLog(RepositoryURI.class);
    
    protected String repositoryName;
    
    /** Get the repository name.
    
        @return The repository name
    */
    
    public String getRepositoryName(){
        return repositoryName;
    }
    
    /** Set the repository name.
    
        @param repositoryName The new repository name
    */
    
    public void setRepositoryName(String repositoryName){
        this.repositoryName = repositoryName;
    }
    
    /** Construct a String version of the URI.
    
        @return The URI as a String
    */
    
    public String toURI(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(protocol);
        buffer.append(":");
        buffer.append(repositoryName);
        buffer.append("://");
        buffer.append(path);
        return buffer.toString();
    }
    
    /** Set the URI to the specified String.  Invoking this method will
        parse the specified String.
        
        @param uriString The URI string
    */
    
    public void setURI(String uriString){
        if(log.isDebugEnabled())
            log.debug("setURI(" + uriString + ")");
        
        int protocolTerminatorIndex = uriString.indexOf(PROTOCOL_SEPARATOR);
        int nameTerminatorIndex = uriString.indexOf(PROTOCOL_SEPARATOR, 
            protocolTerminatorIndex + 1);
        
        protocol = uriString.substring(0, protocolTerminatorIndex);
        repositoryName = uriString.substring(protocolTerminatorIndex + 1, 
            nameTerminatorIndex);
        path = uriString.substring(nameTerminatorIndex + 
            URI_SEPARATOR.length());
        
        
    }
    
}
