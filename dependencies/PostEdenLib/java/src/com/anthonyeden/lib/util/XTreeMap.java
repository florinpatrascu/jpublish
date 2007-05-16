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

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ListModel;
import javax.swing.table.TableModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** An implementation of a TreeMap which can be placed in a JTable or
    in a JList.  When used in a JTable the table will have two columns:
    column 1 for the key and column 2 for the value.  Whan used in a
    JList the keys will be used by default for display.
    
    <p>Note that since this class extends from the TreeMap class the keys 
    will be sorted in natural order as described in the TreeMap 
    documentation.

    @author Anthony Eden
*/

public class XTreeMap extends TreeMap implements TableModel, ListModel{
    
    private static final Log log = LogFactory.getLog(XTreeMap.class);
    
    private ArrayList tableModelListeners = new ArrayList();
    private ArrayList listDataListeners = new ArrayList();
    private String[] columnNames = {"Key", "Value"};
    private Class[] columnClasses = {String.class, String.class};
    
    private boolean listUsesKey = false;

    /** Clear the map. */

    public void clear(){
        int mapSize = size();
        if(mapSize > 0){
            super.clear();
            fireTableChanged(new TableModelEvent(this));
            fireIntervalRemoved(0, size() - 1);
        }
    }
    
    /** Put the given object into the map using the given key.  If there
        is already an object in the map with the given key then the object
        being replaced will be returned.  If there is not value for the 
        given key then the return value will be null.
        
        @param key The key
        @param value The value
        @return The previous object
    */
    
    public Object put(Object key, Object value){
        Object oldValue = null;
    
        if(containsKey(key)){
            int index = keyIndex(key);
            oldValue = super.put(key, value);
            fireTableChanged(new TableModelEvent(this, index));
            fireContentsChanged(index, index);
        } else {
            oldValue = super.put(key, value);
            int index = keyIndex(key);
            fireRowsInserted(index, index);
            fireIntervalAdded(index, index);
        }
        
        return oldValue;
    }
    
    /** Put all of the given map into this map.
    
        @param map The map to insert
    */
    
    public void putAll(Map map){
        super.putAll(map);
        fireTableChanged(new TableModelEvent(this));
        fireContentsChanged(0, size() - 1);
    }
    
    /** Remove the object for the given key.  This method will return
        the removed object or null if no object is removed.
        
        @param key The key
        @return The removed object or null
    */
    
    public Object remove(Object key){
        int index = keyIndex(key);
        Object oldValue = super.remove(key);
        fireRowsDeleted(index, index);
        fireIntervalRemoved(index, index);
        return oldValue;
    }
    
    /** Get the index of the given key or -1 if there is no matching key.
    
        @param key The key
        @return The index
    */
    
    public int keyIndex(Object key){
        int index = 0;
        Iterator i = keySet().iterator();
        while(i.hasNext()){
            if(i.next().equals(key)){
                return index;
            }
            index++;
        }
        return -1;
    }
    
    /** Get the key at the given index.
    
        @param index The index
        @return The key at the given index
    */
    
    public Object keyAt(int index){
        //log.debug("keyAt(" + index + ")");
        return elementAt(index, keySet());
    }
    
    /** Get the value at the given index.
    
        @param index The index
        @return The value at the given index
    */
    
    public Object valueAt(int index){
        //log.debug("valueAt(" + index + ")");
        return elementAt(index, values());
    }
    
    /** Get the element at the given index in the given collection.
    
        @param index The index
        @param c The collection
        @return The element at index in the collection
    */
    
    protected Object elementAt(int index, Collection c){
        int x = 0;
        Iterator i = c.iterator();
        while(i.hasNext()){
            Object obj = i.next();
            if(x == index){
                return obj;
            }
            x++;
        }
        return null;
    }
    
    /** Return true if the map keys are returned in the ListModel getElementAt()
        method.  If this method returns false then the getElementAt() method
        returns map values.
        
        @return True if getElementAt() returns keys
    */
    
    public boolean getListUsesKey(){
        return listUsesKey;
    }
    
    /** Set to true if the map keys should be returned in the ListModel 
        getElementAt() method.  Set to false if the getElementAt() method should 
        return map values.
        
        @param listUsesKey True if getElementAt() should return keys
    */
    
    public void setListUsesKey(boolean listUsesKey){
        this.listUsesKey = listUsesKey;
    }
    
    // TableModel implementation
    
    /** Get the total number of rows.
    
        @return The total number of rows
    */
    
    public int getRowCount(){
        return size();
    }
    
    /** Get the total number of columns.
    
        @return The column count
    */
    
    public int getColumnCount(){
        return columnNames.length;
    }
    
    /** Get the column name for the given column.
    
        @param column The column index
        @return The column name
    */
    
    public String getColumnName(int column){
        return columnNames[column];
    }
    
    /** Set the column name for the given column index.
    
        @param column The column index
        @param name The new name
    */
    
    public synchronized void setColumnName(int column, String name){
        columnNames[column] = name;
    }
    
    /** Get the given column's class.
    
        @param column The column 
        @return The column class
    */
    
    public Class getColumnClass(int column){
        return columnClasses[column];
    }
    
    public synchronized void setColumnClass(int column, Class columnClass){
        columnClasses[column] = columnClass;
    }
    
    /** Return true if the cell is editable.  The default behavior allows
        the UI user to change the key value.
        
        @param row The row
        @param column The column 
        @return True if the column is editable
    */
    
    public boolean isCellEditable(int row, int column){
        return (column == 1);
    }
    
    /** Get the value at the given row and column.
    
        @param row The row
        @param column The column
        @return The value 
    */
    
    public Object getValueAt(int row, int column){
        switch(column){
            case 0:
                Object key = keyAt(row);
                //log.debug("Key at " + row + "=" + key);
                return key;
            case 1:
                Object value = valueAt(row);
                //log.debug("Value at " + row + "=" + value);
                return value;
            default:
                throw new IllegalArgumentException("Column must be 0 or 1");
        }
    }
    
    /** Set the value at the given row and column.
    
        @param value The new value
        @param row The row
        @param column The column 
    */
    
    public synchronized void setValueAt(Object value, int row, int column){
        if(isCellEditable(row, column)){
            Object mapKey = keyAt(row);
            Object mapValue = get(mapKey);
        
            switch(column){
                case 0:
                    remove(mapKey);
                    put(value, mapValue);
                    break;
                case 1:
                    put(mapKey, value);
                    break;
                default:
                    throw new IllegalArgumentException("Column must be 0 or 1");
            }
        }
    }
    
    /** Add a TableModelListener.
    
        @param l The TableModelListener to add 
    */
    
    public void addTableModelListener(TableModelListener l){
        tableModelListeners.add(l);
    }
    
    /** Remove a TableModelListener.
    
        @param l The TableModelListener to remove
    */
    
    public void removeTableModelListener(TableModelListener l){
        tableModelListeners.remove(l);
    }
    
    // ListModel implementation
    
    /** Get the element at the given index.
    
        @param index The index
        @return The element
    */
    
    public Object getElementAt(int index){
        if(listUsesKey){
            return keyAt(index);
        } else {
            return valueAt(index);  
        }
    }
    
    /** Get the list size.
    
        @return The list size 
    */
    
    public int getSize(){
        return size();
    }
    
    /** Add a ListDataListener.
    
        @param l The ListDataListener
    */
    
    public void addListDataListener(ListDataListener l){
        listDataListeners.add(l);
    }
    
    /** Remove a ListDataListener.
    
        @param l The ListDataListener
    */
    
    public void removeListDataListener(ListDataListener l){
        listDataListeners.remove(l);
    }
    
    // Event firing methods
    
    protected void fireTableChanged(TableModelEvent evt){
        ArrayList l = null;
        
        synchronized(this){
            l = (ArrayList)(tableModelListeners.clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((TableModelListener)i.next()).tableChanged(evt);
        }
    }
    
    protected void fireRowsInserted(int index0, int index1){
        fireTableChanged(new TableModelEvent(this, index0, index1, 
            TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }
    
    protected void fireRowsDeleted(int index0, int index1){
        fireTableChanged(new TableModelEvent(this, index0, index1, 
            TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }
    
    protected void fireIntervalAdded(int index0, int index1){
        ArrayList l = null;
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.INTERVAL_ADDED, index0, index1);
        
        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).intervalAdded(evt);
        }
    }
    
    protected void fireIntervalRemoved(int index0, int index1){
        ArrayList l = null;
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.INTERVAL_REMOVED, index0, index1);
        
        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).intervalRemoved(evt);
        }
    }
    
    protected void fireContentsChanged(int index0, int index1){
        ArrayList l = null;
        ListDataEvent evt = new ListDataEvent(this, 
            ListDataEvent.CONTENTS_CHANGED, index0, index1);
        
        synchronized(this){
            l = (ArrayList)(listDataListeners.clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ListDataListener)i.next()).contentsChanged(evt);
        }
    }

}
