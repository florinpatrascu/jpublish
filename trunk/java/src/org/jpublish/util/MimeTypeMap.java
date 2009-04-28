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

import java.util.HashMap;

/** Data structure representing a mime type map.

    @author Anthony Eden
*/

public class MimeTypeMap extends HashMap{
    
    /** The default mime type if none is set. */
    public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    
    private String defaultMimeType = DEFAULT_MIME_TYPE;
    
    /** Construct a new MimeTypeMap. */
    
    public MimeTypeMap(){
        init();
    }

    /** Get the mime type for the given file extension.
    
        @return The mime type
    */

    public String getMimeType(String extension){
        String mimeType = (String)get(extension);
        if(mimeType == null){
            return defaultMimeType;
        } else {
            return mimeType;
        }
    }
    
    /** Get the default mime type.
    
        @return The default mime type
    */
    
    public String getDefaultMimeType(){
        return defaultMimeType;
    }
    
    /** Set the default mime type.
    
        @param defaultMimeType The new default mime type
    */
    
    public void setDefaultMimeType(String defaultMimeType){
        if(defaultMimeType != null){
            this.defaultMimeType = defaultMimeType;
        } else {
            this.defaultMimeType = DEFAULT_MIME_TYPE;
        }
    }
    
    /** Initialize the mime type map.  This will create default mime-type
        mappings for common mime types.
    */
    
    protected void init(){
        put("html", "text/html");
        put("htm", "text/html");
        put("txt", "text/plain");
        put("xml", "text/xml");
        put("xls", "application/vnd.ms-excel");
        put("css", "text/css");
        put("gif", "image/gif");
        put("jpeg", "image/jpeg");
        put("jpg", "image/jpeg");
        put("png", "image/png");
        put("ico", "image/x-icon");
        put("js", "application/x-javascript");
        put("json", "text/plain");

        put("pdf", "application/pdf");
        put("mp3", "audio/mpeg");
        put("wav", "audio/x-wav");
        put("asf", "video/x-ms-asf");
        put("avi", "video/x-msvideo");
        put("mpg", "video/mpeg");
    }

}
