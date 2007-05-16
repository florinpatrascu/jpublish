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

package com.anthonyeden.lib;

import java.io.PrintStream;
import java.io.PrintWriter;

/** A chained runtime exception allows a parent exception to be specified
    and accessible through the <code>getNestedError()</code> method
    and will appear in stack traces.
    
    @author Anthony Eden
*/

public class ChainedRuntimeException extends RuntimeException{
    
    private Throwable nestedError;
    
    /** Create a new ChainedRuntimeException using the message of the nested
        error as the exception message.
        
        @param nestedError The nested error
    */
    
    public ChainedRuntimeException(Throwable nestedError){
        this(nestedError.getMessage(), nestedError);
    }

    /** Create a new ChainedRuntimeException.
    
        @param message The message
        @param nestedError The parent exception
    */

    public ChainedRuntimeException(String message, Throwable nestedError){
        super(message);
        this.nestedError = nestedError;
    }
    
    /** Return the parent exception.
    
        @return The parent exception
    */
    
    public Throwable getNestedError(){
        return nestedError;
    }
    
    /** Print the stack trace to System.err */
    
    public void printStackTrace(){
        printStackTrace(System.err);
    }
    
    /** Print the stack trace.
    
        @param out The output stream
    */
    
    public void printStackTrace(PrintStream out){
        super.printStackTrace(out);
        if(nestedError != null){
            out.println("Nested Exception:");
            nestedError.printStackTrace(out);
        }
        out.flush();
    }
    
    /** Print the stack trace.
    
        @param out The output writer
    */
    
    public void printStackTrace(PrintWriter out){
        super.printStackTrace(out);
        if(nestedError != null){
            out.println("Nested Exception:");
            nestedError.printStackTrace(out);
        }
        out.flush();
    }

}
