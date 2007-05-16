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

import java.util.ArrayList;

import javax.swing.ListModel;

/** Implementation of the ListFilter abstract class which filters out exact
    matches.
    
    @author Anthony Eden
*/

public class ListMatchFilter extends ListFilter{

    /** Construct a ListMatchFilter with no ListModel. */
    
    public ListMatchFilter(){
        super();
    }
    
    /** Construct a ListMatchFilter for the given model.
    
        @param model The ListModel
    */
    
    public ListMatchFilter(ListModel model){
        super(model);
    }
    
    /** Filter the list. */

    public synchronized void filter(){
        tempIndexes.clear();
        int size = model.getSize();
        ArrayList values = getValues();
        TOP_LOOP: for(int i = 0; i < size; i++){
            Object value = model.getElementAt(i);
            //System.out.println("Checking element " + value);
            for(int j = 0, valuesSize = values.size(); j < valuesSize; j++){
                if(values.get(j).equals(value)){
                    continue TOP_LOOP;
                }
            }
            tempIndexes.add(new Integer(i));
        }
        
        indexes = new int[tempIndexes.size()];
        for(int i = 0; i < indexes.length; i++){
            indexes[i] = ((Integer)tempIndexes.get(i)).intValue();
        }
    }

}
