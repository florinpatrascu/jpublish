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

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/** Class which represents a MimeType including the suffixes which are
    mapped to the mime type.

    @author Anthony Eden
*/

public class MimeType{
    
    /** Delimiter for separating suffixes in a suffix string (,). */
    public static final String DELIMITER = ",";
    
    private List suffixes;

    /** Construct a MimeType object. */

    public MimeType(){
        suffixes = new ArrayList();
    }
    
    /** Create a MimeType with the suffixes specified in the given
        String.  Suffixes are delimited by one of the characters in the
        DELIMITER constant.
        
        @param suffixString The suffix String
    */
    
    public MimeType(String suffixString){
        suffixes = new ArrayList();
        addSuffixes(suffixString);
    }
    
    /** Return a list of suffixes for this mime type.  For example, the
        text/html mime type could have the suffixes <code>html</code> and
        <code>htm</code> associated with it.
    
        @return A List of suffixes
    */
    
    public List getSuffixes(){
        return suffixes;
    }
    
    /** Add suffixes defined in the suffix String to the MimeType's current
        suffix list.  Each suffix will be trimmed to remove leading and
        trailing spaces.
        
        @param suffixString The suffix String
    */
    
    public void addSuffixes(String suffixString){
        StringTokenizer tk = new StringTokenizer(suffixString, DELIMITER);
        while(tk.hasMoreTokens()){
            suffixes.add(tk.nextToken().trim());
        }
    }

}
