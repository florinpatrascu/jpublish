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

/** Utility class which represents an input/output encoding mapped to a path.

    @author Anthony Eden
*/

public class CharacterEncodingMap{
    
    private String path;
    private String pageEncoding;
    private String templateEncoding;
    private String requestEncoding;
    private String responseEncoding;
    
    /** Get the path.
    
        @return The path
    */
    
    public String getPath(){
        return path;
    }
    
    /** Set the path.
    
        @param path The path
    */
    
    public void setPath(String path){
        this.path = path;
    }
    
    /** Get the page encoding.  This value is used as the default for all
        content retrieved from the repository.
    
        @return The page encoding
    */
    
    public String getPageEncoding(){
        return pageEncoding;
    }
    
    /** Set the page encoding.
    
        @param pageEncoding The new page encoding
    */
    
    public void setPageEncoding(String pageEncoding){
        this.pageEncoding = pageEncoding;
    }
    
    /** Get the template encoding.  This value is used as the default for the
        template.
    
        @return The template encoding
    */
    
    public String getTemplateEncoding(){
        return templateEncoding;
    }
    
    /** Set the template encoding.
    
        @param templateEncoding The new template encoding
    */
    
    public void setTemplateEncoding(String templateEncoding){
        this.templateEncoding = templateEncoding;
    }
    
    /** Get the request encoding.
    
        @return The request encoding
    */
    
    public String getRequestEncoding(){
        return requestEncoding;
    }
    
    /** Set the request encoding.
    
        @param requestEncoding The new request encoding
    */
    
    public void setRequestEncoding(String requestEncoding){
        this.requestEncoding = requestEncoding;
    }
    
    /** Get the response encoding.
    
        @return The response encoding
    */
    
    public String getResponseEncoding(){
        return responseEncoding;
    }
    
    /** Set the response encoding.
    
        @param responseEncoding The new response encoding
    */
    
    public void setResponseEncoding(String responseEncoding){
        this.responseEncoding = responseEncoding;
    }
    
}
