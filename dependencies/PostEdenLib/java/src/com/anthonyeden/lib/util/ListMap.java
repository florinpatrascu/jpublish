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

import javax.swing.ListModel;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/** A wrapper for a list model.

    @author Anthony Eden
*/

public class ListMap extends AbstractListModel implements ListDataListener{
    
    /** The wrapped ListMode. */

    protected ListModel model;

    /** Get the model.
    
        @return The ListModel
    */
    
    public ListModel getModel(){
        return model;
    }
    
    /** Set the model.
    
        @param model The list model
    */

    public void setModel(ListModel model){
        this.model = model;
        model.addListDataListener(this);
    }
    
    /** Get the element at the given index.
    
        @param index The index
        @return The element
    */
    
    public Object getElementAt(int index){
        return model.getElementAt(index);
    }
    
    /** Get the list model size.
    
        @return The size
    */
    
    public int getSize(){
        return model.getSize();
    }
    
    /** This method can be called to signal that an interval specified
        in the given event object has been added.
        
        @param evt The ListDataEvent object
    */
    
    public void intervalAdded(ListDataEvent evt){
        fireIntervalAdded(evt.getSource(), evt.getIndex0(), evt.getIndex1());
    }
    
    /** This method can be called to signal that an interval specified
        in the given event object has been removed.
        
        @param evt The ListDataEvent object
    */
    
    public void intervalRemoved(ListDataEvent evt){
        fireIntervalRemoved(evt.getSource(), evt.getIndex0(), evt.getIndex1());
    }
    
    /** This method can be called to signal that an interval specified
        in the given event object has been changed.
        
        @param evt The ListDataEvent object
    */
    
    public void contentsChanged(ListDataEvent evt){
        fireIntervalRemoved(evt.getSource(), evt.getIndex0(), evt.getIndex1());
    }

}
