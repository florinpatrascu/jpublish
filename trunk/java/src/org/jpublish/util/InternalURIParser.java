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

/** Parser which converts a String to an InternalURI.

    @author Anthony Eden
    @since 2.0
*/

public class InternalURIParser{
    
    private static final InternalURIParser INSTANCE = new InternalURIParser();
    //private HashMap uriClasses;
    
    /** Construct a new InternalURIParser.  Classes which need an instance of 
        this class should use the static <code>getInstance()</code> method.  
    */
    
    protected InternalURIParser(){
        //uriClasses = new HashMap();
        //uriClasses.put("template", InternalURI.class);
        //uriClasses.put("repository", RepositoryURI.class);
    }
    
    /** Get a shared InternalURIParser instance.
    
        @return An instance of the InternalURIParser class
    */
    
    public static InternalURIParser getInstance(){
        return INSTANCE;
    }
    
    /** Parse the given URI string.
    
        @param uriString The URI String
        @return The InternalURI object
        @throws Exception Any Exception
    */
    
    public InternalURI parse(String uriString) throws Exception{
        int protocolTerminatorIndex = uriString.indexOf(":");
        if(protocolTerminatorIndex <= 0){
            throw new IllegalArgumentException("URI string is not a valid URI");
        }
        
        String protocol = uriString.substring(0, protocolTerminatorIndex);
        //Class uriClass = (Class)uriClasses.get(protocol);
        //InternalURI uri = (InternalURI)uriClass.newInstance();
        InternalURI uri = null;
        if ("template".equals(protocol)) {
            uri = new InternalURI();
        } else if ("repository".equals(protocol)) {
            uri = new RepositoryURI();
        } else {
            throw new IllegalArgumentException(
                "Unsupported protocol: " + protocol);
        }
        uri.setURI(uriString);
        return uri;
    }

}
