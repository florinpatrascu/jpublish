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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Utility class for parsing and formatting dates.  An instance of this 
    class can be dropped in the context to allow parsing and formatting
    dates from within view templates.

    @author Anthony Eden
    @since 1.4
*/

public class DateUtilities{
    
    private static final DateUtilities dateUtilities = new DateUtilities();

    /** Get a shared instance of the DateUtilities class. 
    
        @return A DateUtilities instance
    */
    
    public static DateUtilities getInstance(){
        return dateUtilities;
    }
    
    /** Format the specified date using the specified format String.  The
        format String follows the rules specified in the 
        <code>java.text.SimpleDateFormat</code> class.
    
        @param date The date
        @param format The format String
        @return A formatted Date String
    */
    
    public String format(Date date, String format){
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(date);
    }
    
    /** Parse the specified date String using the specified format String.
        The format String follows the rules specified in the 
        <code>java.text.SimpleDateFormat</code> class.
    
        @param date The date String
        @param format The format String
        @return A Date object
        @throws ParseException
    */
    
    public Date parse(String date, String format) throws ParseException{
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.parse(date);
    }
    
}
