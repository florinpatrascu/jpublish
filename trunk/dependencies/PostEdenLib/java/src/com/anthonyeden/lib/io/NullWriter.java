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

package com.anthonyeden.lib.io;

import java.io.Writer;
import java.io.IOException;

/** A null writer will not write any data.  This class includes a shared
    NullWriter instance which can be used throughout your application
    wherever a null writer is required.

    @author Anthony Eden
*/

public class NullWriter extends Writer{
    
    /** Shared NullWriter instance.  You should be able to use
        this shared instance throughout your system wherever a null
        writer is required since the data is discarded.
    */
    
    public static final NullWriter NULL_WRITER = new NullWriter();
    
    private boolean closed = false;

    /** Constructor. */

    public NullWriter(){
        
    }
    
    /** Close the stream. */

    public void close(){
        closed = true;
    }
    
    /** Flush the data that is currently in the buffer.
    
        @throws IOException
    */
    
    public void flush() throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
    }
    
    /** Write the given character array to the output stream.
    
        @param charArray The character array
        @throws IOException
    */
    
    public void write(char[] charArray) throws IOException{
        write(charArray, 0, charArray.length);
    }
    
    /** Write the given character array to the output stream beginning from
        the given offset and proceeding to until the given length is reached.
    
        @param charArray The character array
        @param offset The start offset
        @param length The length to write
        @throws IOException
    */
    
    public void write(char[] charArray, int offset, int length) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
    }
    
    /** Write the given character to the output stream.
    
        @param c The character
        @throws IOException
    */
    
    public void write(int c) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
    }
    
    /** Write the given String to the output stream.
    
        @param string The String
        @throws IOException
    */
    
    public void write(String string) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
    }
    
    /** Write the given String to the output stream beginning from the given offset 
        and proceeding to until the given length is reached.
    
        @param string The String
        @param offset The start offset
        @param length The length to write
        @throws IOException
    */
    
    public void write(String string, int offset, int length) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
    }

}
