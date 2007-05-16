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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This class extends the ArrayList class to support direct placement of the
    list in a JList or JComboBox.
    
    This class can also listen for PropertyChangeEvents on objects in the 
    collection which have an 
    <code>addPropertyChangeListener(PropertyChangeListener l)</code>
    method.  This option is on by default, but can be turned off to invoid 
    introspection overhead required to find and invoke this method.
    
    @author Anthony Eden
*/

public class XArrayList extends ArrayList implements XList{
    
    /** Constant for an empty XArrayList. */
    public static final XArrayList EMPTY_LIST = new XArrayList();
    
    private static final Log log = LogFactory.getLog(XArrayList.class);
    
    public static DataFlavor dataFlavor = new DataFlavor(XArrayList.class, 
        "Collection");
    private static DataFlavor[] dataFlavors = {dataFlavor};
    
    private static Class[] addPCListenerArgTypes = 
        {PropertyChangeListener.class};
    
    private ArrayList listDataListeners;
    private Object selectedItem;
    
    private boolean propertyChangeListenerEnabled = true;

    /** Populate an XArrayList with the data from the given object array.
    
        @param array The object array
        @return The XArrayList
    */

    public static XArrayList arrayToList(Object[] array){
        XArrayList list = new XArrayList();
        for(int i = 0; i < array.length; i++){
            list.add(array[i]);
        }
        return list;
    }
    
    /** Add the given element to the list at the given index.
    
        @param index The index
        @param element The element
    */

    public void add(int index, Object element){
        super.add(index, element);
        fireIntervalAdded(index, index);
        addPropertyChangeListener(element);
    }
    
    /** Add the given object to the list.
    
        @param element The element
        @return boolean If the element was added
    */
    
    public boolean add(Object element){
        boolean added = super.add(element);
        if(added){
            //log.debug("Firing interval added.");
            fireIntervalAdded(size() - 1, size() - 1);
        }
        addPropertyChangeListener(element);
        return added;
    }
    
    /** Add all of the objects in the collection to this collection.
    
        @param collection The collection to add
        @return Return true if this collection was modified
    */
    
    public boolean addAll(Collection collection){
        boolean added = false;
        int startSize = size();
        if(collection.size() > 0){
            added = super.addAll(collection);
            fireIntervalAdded(startSize, size());
        }
        addPropertyChangeListener(collection);
        return added;
    }
    
    /** Add all elements in the collection to this list inserting at the
        given index.
        
        @param index The offset index
        @param collection The collection to add
    */
    
    public boolean addAll(int index, Collection collection){
        boolean added = false;
        int collectionSize = collection.size();
        if(collectionSize > 0){
            added = super.addAll(index, collection);
            fireIntervalAdded(index, index + collectionSize); 
        }
        addPropertyChangeListener(collection);
        return added;
    }
    
    /** Clear the list data. */
    
    public void clear(){
        int listSize = size();
        if(listSize > 0){
            removePropertyChangeListener(this);
            super.clear();
            fireIntervalRemoved(0, listSize);
        }
    }
    
    /** Remove the object at the given index.
    
        @param index The index
        @return The Object removed or null
    */
    
    public Object remove(int index){
        //log.debug("remove(" + index + ")");
        Object removed = super.remove(index);
        if(removed != null){
            removePropertyChangeListener(removed);
            fireIntervalRemoved(index, index);
        }
        return removed;
    }
    
    /** Remove the given object.  Return true if the data was changed (the
        element was removed).
        
        @param object The object
        @return True if the data was changed
    */
    
    public boolean remove(Object object){
        // the ArrayList implementation calls remove(int index)
        // and thus there is no need to fire a removal event
        
        // Note that this may be specific to JDK 1.4 and higher.
        
        return super.remove(object);
    }
    
    /** See the ArrayList retainAll() method.
    
        @param c The Collection of objects to retain
        @return True if this list was modified
    */
    
    public boolean retainAll(Collection c){
        // need to implement property change listener support
        boolean modified = super.retainAll(c);
        if(modified){
            fireContentsChanged(0, size());
        }
        return modified;
    }
    
    /** Remove the objects in the specified collection from this list.
    
        @param c The Collection to remove
        @return True if this collection was modified
    */
    
    public boolean removeAll(Collection c){
        // need to implement property change listener support
        boolean modified = super.removeAll(c);
        if(modified){
            fireContentsChanged(0, size());
        }
        return modified;
    }
    
    // ListModel implementation
    
    /** Get the number of elements in the list.
    
        @return The list size
    */
        
    public int getSize(){
        return size();
    }
    
    /** Get the element at the given index.
    
        @param index The index 
        @return The Object or null
    */
    
    public Object getElementAt(int index){
        return get(index);
    }
    
    /** Add a ListDataListener.
    
        @param l The ListDataListener
    */
    
    public void addListDataListener(ListDataListener l){
        getListDataListeners().add(l);
    }
    
    /** Remove a ListDataListener.
    
        @param l The ListDataListener
    */
    
    public void removeListDataListener(ListDataListener l){
        getListDataListeners().remove(l);
    }
    
    // ComboBoxModel implementation
    
    /** Get the selected item or null if no item is selected.
    
        @return The selected item or null
    */
    
    public Object getSelectedItem(){
        return selectedItem;
    }
    
    /** Set the selected item.  Set this value to null for no selected
        item.
        
        @param selectedItem The new selected item
    */
    
    public void setSelectedItem(Object selectedItem){
        this.selectedItem = selectedItem;
    }
    
    // Transferable implementation
    
    /** Get the transferable data for the given data flavor.
    
        @param dataFlavor The DataFlavor
        @return The data
        @throws UnsupportedFlavorException
    */
    
    public Object getTransferData(DataFlavor dataFlavor) throws 
    UnsupportedFlavorException{
        if(isDataFlavorSupported(dataFlavor)){
            return this;
        } else {
            throw new UnsupportedFlavorException(dataFlavor);
        }
    }
    
    /** Get an array of supported DataFlavors.
    
        @return An array of DataFlavor objects
    */
    
    public DataFlavor[] getTransferDataFlavors(){
        return dataFlavors;
    }
    
    /** Return true if the given DataFlavor is supported.
    
        @param dataFlavor The DataFlavor
        @return True if the DataFlavor is supported
    */
    
    public boolean isDataFlavorSupported(DataFlavor dataFlavor){
        return dataFlavor.equals(XArrayList.dataFlavor);
    }
    
    // PropertyChangeListener implementation
    
    public void propertyChange(PropertyChangeEvent evt){
        //log.debug("Property changed [old=" + evt.getOldValue() + ",new=" + 
        //    evt.getNewValue() + "]");
        int index = indexOf(evt.getSource());
        if(index >= 0){
            //log.debug("Firing contents changed [" + index + "]");
            fireContentsChanged(index, index);
        }
    }
    
    /** Set to false to disable property change listener support.  The default
        value is true.
        
        <b>Warning:</b> do not change this flag while there are objects in the
        collection.  Doing so may cause this collection to remain attached to
        objects which are removed from the collection.
        
        @param propertyChangeListenerEnabled Set to false to disable
    */
    
    public void setPropertyChangeListenerEnabled(
    boolean propertyChangeListenerEnabled){
        this.propertyChangeListenerEnabled = propertyChangeListenerEnabled;
    }
    
    // protected methods
    
    /** Get all registered ListDataListeners
    
        @return An ArrayList of ListDataListeners
    */
    
    protected ArrayList getListDataListeners(){
        if(listDataListeners == null){
            listDataListeners = new ArrayList();
        }
        return listDataListeners;
    }
    
    /** Remove the given range of objects.
    
        @param startIndex The start index
        @param endIndex The end index 
    */
    
    protected void removeRange(int startIndex, int endIndex){
        //log.debug("removeRange(" + startIndex + "," + endIndex + ")");
        super.removeRange(startIndex, endIndex);
        fireIntervalRemoved(startIndex, endIndex);
    }
    
    /** Fire a ListDataEvent for the added interval.
    
        @param index0 The start index
        @param index1 The end index 
    */
    
    protected void fireIntervalAdded(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.INTERVAL_ADDED, index0, index1);
        List l = null;
        
        synchronized(this){
            l = (List)(getListDataListeners().clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).intervalAdded(evt);
        }
    }
    
    /** Fire a ListDataEvent for the removed interval.
    
        @param index0 The start index
        @param index1 The end index 
    */
    
    protected void fireIntervalRemoved(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.INTERVAL_REMOVED, index0, index1);
        List l = null;
        
        synchronized(this){
            l = (List)(getListDataListeners().clone());
        }
        
        //log.debug("Listener count: " + l.size());
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            //log.debug("Fire interval removed: " + index0 + "," + index1);
            ((ListDataListener)i.next()).intervalRemoved(evt);
        }
    }
    
    /** Fire a ListDataEvent for the changed interval.
    
        @param index0 The start index
        @param index1 The end index 
    */
    
    protected void fireContentsChanged(int index0, int index1){
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.CONTENTS_CHANGED, index0, index1);
        List l = null;
        
        synchronized(this){
            l = (List)(getListDataListeners().clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).contentsChanged(evt);
        }
    }
    
    /** Add this XArrayList as a PropertyChangeListener to the given
        target object.  If the <code>propertyChangeListenerEnabled</code>
        is false then this method returns immediately.  Otherwise 
        reflection will be used on the target object to locate and invoke
        the addPropertyChangeListener() method.  If no method is found
        then this method returns.
        
        @param target The target object
    */
    
    private void addPropertyChangeListener(Object target){
        if(!propertyChangeListenerEnabled){
            return;
        }
        
        try{
            Method m = target.getClass().getMethod("addPropertyChangeListener", 
                addPCListenerArgTypes);
            Object[] args = {this};
            m.invoke(target, args);
        } catch(NoSuchMethodException e){
            // do nothing
        } catch(Exception e){
            // do nothing (report errors?)
        }
    }
    
    /** Add PropertyChangeListeners to all objects in the collection.
    
        @param collection The collection of objects
    */
    
    private void addPropertyChangeListener(Collection collection){
        Iterator i = collection.iterator();
        while(i.hasNext()){
            addPropertyChangeListener(i.next());
        }
    }
    
    /** Remove this XArrayList as a PropertyChangeListener for the target
        object.
        
        @param target The target object
    */
    
    private void removePropertyChangeListener(Object target){
        if(!propertyChangeListenerEnabled){
            return;
        }
        
        try{
            Method m = target.getClass().getMethod(
                "removePropertyChangeListener", addPCListenerArgTypes);
            Object[] args = {this};
            m.invoke(target, args);
        } catch(NoSuchMethodException e){
            // do nothing
        } catch(Exception e){
            // do nothing (report errors?)
        }
    }
    
    /** Remove PropertyChangeListeners to all objects in the collection.
    
        @param collection The collection of objects
    */
    
    private void removePropertyChangeListener(Collection collection){
        Iterator i = collection.iterator();
        while(i.hasNext()){
            removePropertyChangeListener(i.next());
        }
    }

}
