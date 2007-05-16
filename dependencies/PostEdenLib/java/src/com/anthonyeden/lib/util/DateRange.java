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

import java.util.Date;

/** This class represents a continuous date range.

    @author Anthony Eden
*/

public class DateRange{
    
    private Date startDate;
    private Date endDate;
    
    /** Construct a new DateRange with null values for the start and
        end date.
    */

    public DateRange(){
    
    }
    
    /** Construct a new DateRanger with the given start and end date.
    
        @param startDate The start date
        @param endDate The end date
    */

    public DateRange(Date startDate, Date endDate){
        setStartDate(startDate);
        setEndDate(endDate);
    }
    
    /** Get the start date.  This value may be null.
    
        @return The start date
    */
    
    public Date getStartDate(){
        return startDate;
    }
    
    /** Set the start date.  This value may be null.
    
        @param startDate The new start date
    */

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    
    /** Get the end date.  This value may be null.
    
        @return The end date
    */
    
    public Date getEndDate(){
        return endDate;
    }
    
    /** Set the end date.  This value may be null.
    
        @param endDate The new end date
    */
    
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
    
    /** Return true if the given Date is after the start date and before the end
        date.  If either date is null then that part of the test will 
        automatically be true.
        
        @param date The Date to test
        @return true if the Date is between the start and end date
    */
    
    public boolean isWithinRange(Date date){
        if(startDate == null || date.after(startDate)){
            if(endDate == null || date.before(endDate)){
                return true;
            }
        }
        return false;
    }
    
    
    /** Return the number of segments in the range given that
        a segment is <code>segmentLength</code> milliseconds.
        
        @param segmentLength Segment length in milliseconds
        @return The number of segments
        @throws IllegalStateException If either the start or end date are not 
            set
    */

    public double getSegmentCount(int segmentLength) throws 
    IllegalStateException{
        if(startDate == null || endDate == null){
            throw new IllegalStateException("Operation cannot be performed");
        }
        
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        
        return (endTime - startTime) / segmentLength;
    }
    
    /** Return a String representation of the DateRange.
    
        @return A String representing the date range
    */
    
    public String toString(){
        return "[" + startDate + " to " + endDate + "]";
    }

}
