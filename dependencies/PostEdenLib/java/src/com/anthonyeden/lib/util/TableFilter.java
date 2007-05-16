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
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/** A filter for TableModels.  The filter has a model (conforming to TableModel)
    and itself implements TableModel.
    
    @author Anthony Eden
*/

public abstract class TableFilter extends TableMap{
    
    protected int[] indexes;
    protected int column = -1;
    protected Object[] values;
    protected ArrayList tempIndexes = new ArrayList();
    
    /** Construct a new TableFilter with no backing model. */
    
    public TableFilter(){
        indexes = new int[0];
    }
    
    /** Construct a new TableFilter with the given backing model.
    
        @param model The TableModel
    */
    
    public TableFilter(TableModel model){
        setTableModel(model);
    }
    
    /** Set the table model to filter.
    
        @param model The model to filter
    */

    public void setTableModel(TableModel model){
        super.setModel(model);
        filter();
    }
    
    /** Filter the model.  Subclasses should implement this method
        to provide the filter logic.
    */
    
    public abstract void filter();
    
    /** Signal that the table has changed in some way.
    
        @param evt The TableModelEvent
    */
    
    public void tableChanged(TableModelEvent evt){
        filter();
        super.tableChanged(evt);
    }
    
    /** Get the number of rows in the table.
    
        @return The row count
    */
    
    public int getRowCount(){
        return indexes.length;
    }
    
    /** Get the value at the given row and column.
    
        @param row The row
        @param column The column
        @return The value
    */
    
    public Object getValueAt(int row, int column){
        return model.getValueAt(indexes[row], column);
    }
    
    /** Set the value at the given row and column.
    
        @param newValue The new value
        @param row The row
        @param column The column
    */
    
    public void setValueAt(Object newValue, int row, int column){
        model.setValueAt(newValue, indexes[row], column);
    }
    
    /** Filter the given column using the specified value.
    
        @param column The column
        @param value The value
    */
    
    public void filter(int column, Object value){
        Object[] values = new Object[1];
        values[0] = value;
        filter(column, values);
    }
    
    /** Filter the given column using the values in the given array.
    
        @param column The column
        @param values An array of value objects
    */
    
    public void filter(int column, Object[] values){
        this.column = column;
        this.values = values;
        filter();
        super.tableChanged(new TableModelEvent(this));
    }
    
    /** Get the current filtered column.
    
        @return The column
    */
    
    public int getColumn(){
        return column;
    }
    
    /** Get the current filter value array.
    
        @return An array of filter values
    */
    
    public Object[] getValues(){
        return values;
    }

}
