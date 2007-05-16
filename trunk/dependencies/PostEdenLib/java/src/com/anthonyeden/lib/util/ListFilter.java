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
import javax.swing.event.ListDataEvent;

/** Class for filtering a ListModel.

    @author Anthony Eden
*/

public abstract class ListFilter extends ListMap{
    
    protected ArrayList tempIndexes = new ArrayList();
    protected ArrayList values;
    protected int[] indexes;

    /** Construct a new ListFilter. */
    
    public ListFilter(){
        indexes = new int[0];
    }
    
    /** Construct a new ListFilter for the given ListModel.
    
        @param model The ListModel
    */
    
    public ListFilter(ListModel model){
        setListModel(model);
    }
    
    /** Set the list model.  Calling this method will set the filtered ListModel
        and will execute the filter.
    
        @param model The ListModel
    */
    
    public void setListModel(ListModel model){
        super.setModel(model);
        filter();
    }
    
    /** Subclasses should implement this method to execute the filtering. */
    
    public abstract void filter();
    
    /** Filter the list when objects are added.
    
        @param evt The ListDataEvent
    */
    
    public void intervalAdded(ListDataEvent evt){
        filter();
        super.intervalAdded(evt);
    }
    
    /** Filter the list when objects are removed.
    
        @param evt The ListDataEvent
    */
    
    public void intervalRemoved(ListDataEvent evt){
        filter();
        super.intervalRemoved(evt);
    }
    
    /** Filter the list when the contents change.
    
        @param evt The ListDataEvent
    */
    
    public void contentsChanged(ListDataEvent evt){
        filter();
        super.contentsChanged(evt);
    }
    
    /** Get the size of the list.
    
        @return The size of the list
    */
    
    public int getSize(){
        return indexes.length;
    }
    
    /** Get the element at the given index in the list.
    
        @param index The index
        @return The value
    */
    
    public Object getElementAt(int index){
        return model.getElementAt(indexes[index]);
    }
    
    /** Filter a single value.
    
        @param value The value
    */
    
    public void filter(Object value){
        ArrayList values = new ArrayList();
        values.add(value);
        filter(values);
    }
    
    /** Set the value list and filter.
    
        @param values An array of Objects
    */
    
    public void filter(Object[] values){
        ArrayList valuesList = new ArrayList();
        for(int i = 0; i < values.length; i++){
            valuesList.add(values[i]);
        }
        filter(valuesList);
    }
    
    /** Set the value list and filter.
        
        @param values The List of values
    */
    
    public void filter(ArrayList values){
        this.values = values;
        filter();
        super.contentsChanged(new ListDataEvent(this, 
            ListDataEvent.CONTENTS_CHANGED, 0, indexes.length));
    }
    
    /** Get the values.
    
        @return A List of values
    */
    
    public ArrayList getValues(){
        if(values == null){
            values = new ArrayList();
        }
        return values;
    }

}
