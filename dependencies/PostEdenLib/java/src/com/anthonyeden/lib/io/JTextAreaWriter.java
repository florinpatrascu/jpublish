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

import javax.swing.JTextArea;

/** A implementation of the java.io.Writer class which provides writing to a
    JTextArea via a stream.
    
    <p><b>Note:</b> There appears to be bug in the Macintosh implementation of 
    the JDK 1.1 where a PrintWriter writing to this class will not include the 
    correct line feeds for display in a JTextArea.  There is a simple test of
    the "java.version" system property which, if it starts with the String "1.1"
    will cause newlines to be written each time the buffer is flushed.</p>
    
    @author Anthony Eden
*/

public class JTextAreaWriter extends Writer{
    
    private boolean closed = false;
    private JTextArea textArea;
    private StringBuffer buffer;

    /** Constructor.
    
        @param textArea The JTextArea to write to.
    */

    public JTextAreaWriter(JTextArea textArea){
        setTextArea(textArea);
    }
    
    /** Set the JTextArea to write to.
    
        @param textArea The JTextArea
    */
    
    public void setTextArea(JTextArea textArea){
        if(textArea == null){
            throw new IllegalArgumentException("The text area must not be null.");
        }
        this.textArea = textArea;
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
        // the newline character should not be necessary.  The PrintWriter
        // should autmatically put the newline, but it doesn't seem to work
        textArea.append(getBuffer().toString());
        if(System.getProperty("java.version").startsWith("1.1")){
            textArea.append("\n");
        }
        textArea.setCaretPosition(textArea.getDocument().getLength());
        buffer = null;
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
        getBuffer().append(charArray, offset, length);
    }
    
    /** Write the given character to the output stream.
    
        @param c The character
        @throws IOException
    */
    
    public void write(int c) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
        getBuffer().append((char)c);
    }
    
    /** Write the given String to the output stream.
    
        @param string The String
        @throws IOException
    */
    
    public void write(String string) throws IOException{
        if(closed){
            throw new IOException("The stream is not open.");
        }
        getBuffer().append(string);
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
        getBuffer().append(string.substring(offset, length));
    }
    
    // protected methods
    
    /** Get the StringBuffer which holds the data prior to writing via
        a call to the <code>flush()</code> method.  This method should
        never return null.
        
        @return A StringBuffer
    */
    
    protected StringBuffer getBuffer(){
        if(buffer == null){
            buffer = new StringBuffer();
        }
        return buffer;
    }

}
