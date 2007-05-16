/*-- 

 Copyright (C) 2000-2003 Anthony Eden.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "EdenLib" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact me@anthonyeden.com.
 
 4. Products derived from this software may not be called "EdenLib", nor
    may "EdenLib" appear in their name, without prior written permission
    from Anthony Eden (me@anthonyeden.com).
 
 In addition, I request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 For more information on EdenLib, please see <http://edenlib.sf.net/>.
 
 */

package com.anthonyeden.lib.util;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/** A simple tool for parsing files which use the Velocity templating
    language.
    
    @author Anthony Eden
*/

public class VelocityTool{

    private VelocityTool(){
        // no op
    }
    
    /** Parse the text file at the given path.
    
        @param path The path
        @return The resulting String
        @throws Exception
    */
    
    public static String parseTextFile(String path) throws Exception{
        return parseTextFile(new File(path));
    }
    
    /** Parse the text file at the given path.  Variables are specified as 
        name/value pairs in the contextMap.
        
        @param path The path
        @param contextMap The name/value variable pairs
        @return The resulting String
        @throws Exception
    */
    
    public static String parseTextFile(String path, Map contextMap) 
    throws Exception{
        return parseTextFile(path, contextMap);
    }
    
    /** Parse the text file at the given path.
        
        @param file The File
        @return The resulting String
        @throws Exception
    */

    public static String parseTextFile(File file) throws Exception{
        return parseTextFile(file, Collections.EMPTY_MAP);
    }
    
    /** Parse the text file.  Variables are specified as name/value pairs in 
        the contextMap.
        
        @param file The File
        @param contextMap The name/value variable pairs
        @return The resulting String
        @throws Exception
    */
    
    public static String parseTextFile(File file, Map contextMap)
    throws Exception{
        StringWriter writer = new StringWriter();
        FileReader reader = new FileReader(file);

        parse(reader, writer, contextMap);
        
        return writer.toString();
    }
    
    /** Parse the InputStream.
        
        @param in The InputStream
        @return The resulting String
        @throws Exception
    */
    
    public static String parseInputStream(InputStream in) throws Exception{
        return parseInputStream(in, Collections.EMPTY_MAP);
    }
    
    /** Parse the text file at the given path.  Variables are specified as 
        name/value pairs in the contextMap.
        
        @param in The input stream
        @param contextMap The name/value variable pairs
        @return The resulting String
        @throws Exception
    */
    
    public static String parseInputStream(InputStream in, Map contextMap) 
    throws Exception{
        StringWriter writer = new StringWriter();
        InputStreamReader reader = new InputStreamReader(in);
    
        parse(reader, writer, contextMap);
        
        return writer.toString();
    }
    
    /** Pass all data from the given Reader through Velocity and write the 
        resulting data to the given Writer.
        
        @param reader The reader
        @param writer The writer
        @param contextMap The context map
        @throws Exception
    */
    
    public static void parse(Reader reader, Writer writer, Map contextMap) 
    throws Exception{
        VelocityContext customContext = new VelocityContext();
        Iterator keys = contextMap.keySet().iterator();
        while(keys.hasNext()){
            Object key = keys.next();
            customContext.put(key.toString(), contextMap.get(key));
        }
        
        Velocity.evaluate(customContext, writer, VelocityTool.class.getName(), 
            reader);
    }

}
