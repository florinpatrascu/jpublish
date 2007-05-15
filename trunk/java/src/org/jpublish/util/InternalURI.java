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

/** Object representation of an internal JPublish URI.

    @author Anthony Eden
    @since 2.0
*/

public class InternalURI{
    
    public static final String PROTOCOL_SEPARATOR = ":";
    public static final String URI_SEPARATOR = "://";
    
    protected String protocol;
    protected String path;
    
    /** Get the URI protocol.
    
        @return The protocol
    */
    
    public String getProtocol(){
        return protocol;
    }
    
    /** Set the URI protocol.
    
        @param protocol The new protocol
    */
    
    public void setProtocol(String protocol){
        this.protocol = protocol;
    }
    
    /** Get the URI path.
        
        @return The path
    */
    
    public String getPath(){
        return path;
    }
    
    /** Set the URI path.
    
        @param path The URI path
    */
    
    public void setPath(String path){
        this.path = path;
    }
    
    /** Construct a String version of the URI.  This method should be 
        overloaded by implementations to provide specific URI construction
        behavior.
    
        @return The URI as a String
    */
    
    public String toURI(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(protocol);
        buffer.append(URI_SEPARATOR);
        buffer.append(path);
        return buffer.toString();
    }
    
    /** Set the URI to the specified String.  Invoking this method will
        parse the specified String.  This method should overloaded by
        implementations to provide the specific URI parsing behavior.
        
        @param uriString The URI string
    */
    
    public void setURI(String uriString){
        int protocolTerminatorIndex = uriString.indexOf(PROTOCOL_SEPARATOR);
        
        protocol = uriString.substring(0, protocolTerminatorIndex);
        path = uriString.substring(protocolTerminatorIndex + 
            URI_SEPARATOR.length());
    }
    
}
