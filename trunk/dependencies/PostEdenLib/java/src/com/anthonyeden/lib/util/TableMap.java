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

import javax.swing.table.*; 
import javax.swing.event.TableModelListener; 
import javax.swing.event.TableModelEvent; 

/** Based on work by Philip Milne. 

    @author Anthony Eden
*/

public class TableMap extends AbstractTableModel implements TableModelListener {
    
    /** The wrapped TableModel. */
    
    protected TableModel model;

    /** Get the TableModel which is wrapped by this map.
    
        @return The TableModel
    */
    
    public TableModel getModel() {
        return model;
    }
    
    /** Set the TableModel which is wrapped by this map.
    
        @param model The TableModel
    */

    public void setModel(TableModel model) {
        this.model = model; 
        model.addTableModelListener(this); 
    }
    
    /** Get the value at the given row and column.
    
        @param aRow The row
        @param aColumn The column
        @return The value
    */

    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn); 
    }
    
    /** Set the value at the given row and column.
    
        @param aValue The value
        @param aRow The row
        @param aColumn The column
    */
        
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn); 
    }
    
    /** Get the row count.
    
        @return The row count
    */

    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount(); 
    }
    
    /** Get the column count.
    
        @return The column count
    */

    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount(); 
    }
    
    /** Get the column name for the given index.
    
        @param aColumn The column
        @return The column name
    */
        
    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn); 
    }
    
    /** Get the column class for the given column index.
    
        @param aColumn The column
        @return The column class
    */

    public Class getColumnClass(int aColumn) {
        return model.getColumnClass(aColumn); 
    }
    
    /** Return true if the cell at the given row and column is editable.
    
        @param row The row
        @param column The column
        @return True if the cell is editable
    */
        
    public boolean isCellEditable(int row, int column) { 
        return model.isCellEditable(row, column); 
    }
    
    /** Fire an event signaling that the table has changed.
    
        @param e The TableModelEvent
    */

    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }

}
