package com.anthonyeden.lib.util;

import java.awt.datatransfer.Transferable;
import java.util.List;
import java.beans.PropertyChangeListener;

import javax.swing.ListModel;
import javax.swing.ComboBoxModel;

public interface XList extends List, ListModel, ComboBoxModel, Transferable,
PropertyChangeListener{
    
    
    
}
