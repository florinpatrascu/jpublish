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

package com.anthonyeden.lib.config;

/**
 * This interface provides information about where the configuration object was
 * located in the configuration file.
 *
 * @author Anthony Eden
 * @since 2.0
 */

public class Location {
    
    private String sourceId;
    private int lineNumber;
    private int columnNumber;
    
    /**
     * Construct a new Location object
     *
     * @param sourceId The source ID
     * @param lineNumber The line number
     * @param columnNumber The column number
     */
    
    public Location(String sourceId, int lineNumber, int columnNumber) {
        this.sourceId = sourceId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    /**
     * Get the source ID which is used to identify the source of the 
     * configuration data.  This method should return a file path, URL or some
     * other form of human readable location ID
     *
     * @return The source ID
     */
    
    public String getSourceId() {
        return sourceId;
    }
    
    /**
     * Get the line number
     *
     * @return The line number
     */
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** 
     * Get the column number
     *
     * @return The column number
     */
    
    public int getColumnNumber() {
        return columnNumber;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

}
