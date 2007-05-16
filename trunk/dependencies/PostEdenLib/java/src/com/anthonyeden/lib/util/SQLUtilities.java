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

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLWarning;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Useful SQL utilities.

    @author Anthony Eden
    @author kalman
*/

public class SQLUtilities{
    
    private static Log log = LogFactory.getLog(SQLUtilities.class);
    
    /** Close the given JDBC ResultSet if it is not null.
    
        @param rs The JDBC ResultSet
    */
    
     public static void close(ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
            } catch (Exception e) {
                log.error("Error closing result set: " + e.getMessage());
            }
        }
    }
    
    /** Close the given JDBC statement if it is not null.
    
        @param stmt The JDBC Statement
    */

    public static void close(Statement stmt) {
        if(stmt != null){
            try {
                stmt.close();
            } catch (Exception e) {
                log.error("Error closing statement: " + e.getMessage());
            }
        }
    }

   /**  Close the given JDBC connection if it is not null.
    
        @param c The JDBC connection
    */

    public static void close(Connection c){
        if(c != null){
            try{
                c.close();
            } catch(Exception e){
                log.error("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /** Convert the given SQLWarning into a String.
    
        @param sqlw The SQLWarning
    */

    public static String toString(SQLWarning sqlw) {
        StringBuffer buffer = new StringBuffer();
        String newLine = System.getProperty("line.separator");
        do {
            buffer.append("error code = ").append(
                sqlw.getErrorCode()).append(newLine);
            buffer.append("localized message = ").append(
                sqlw.getLocalizedMessage()).append(newLine);
            buffer.append("message = ").append(
                sqlw.getMessage()).append(newLine);
            buffer.append("sqlstate = ").append(
                sqlw.getSQLState()).append(newLine);
            sqlw = sqlw.getNextWarning();
        } while( sqlw != null );
        
        return buffer.toString();
    }
    
    /** Convert the given SQLException into a String.
    
        @param sqlx The SQLException
        @return A String
    */

    public static String toString(SQLException sqlx) {
        StringBuffer buffer = new StringBuffer();
        String newLine = System.getProperty("line.separator");
        do {
            buffer.append("error code = ").append(
                sqlx.getErrorCode()).append(newLine);
            buffer.append("localized message = ").append(
                sqlx.getLocalizedMessage()).append(newLine);
            buffer.append("message = ").append(
                sqlx.getMessage()).append(newLine);
            buffer.append("sqlstate = ").append(
                sqlx.getSQLState()).append(newLine);
            sqlx = sqlx.getNextException();
        } while( sqlx != null );
        
        return buffer.toString();
    }

}
