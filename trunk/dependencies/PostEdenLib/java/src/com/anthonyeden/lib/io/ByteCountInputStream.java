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

package com.anthonyeden.lib.io;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/** A FilterInputStream which will keep track of the number of bytes
    read from the underlying stream.  Callers who want to know how many
    bytes have been read should attach a ChangeListener and call getCount()
    whenever a change event is fired.
    
    <p>If no ChangeListeners are attached then this stream will not fire
    events.  This improves performance by only firing events when at least
    one object is expecting them.
    
    @author Anthony Eden
*/

public class ByteCountInputStream extends FilterInputStream{
    
    private long count = 0;
    private ArrayList changeListeners;
    
    // events won't be fired if there are no listeners
    // this should speed up throughput when there are no
    // listeners
    
    private boolean fireEventRequired = false;
    
    /** Construct a new ByteCountInputStream.
    
        @param in The input stream to count
    */

    public ByteCountInputStream(InputStream in){
        super(in);
    }
    
    /** Add a ChangeListener.
    
        @param l The ChangeListener
    */
    
    public void addChangeListener(ChangeListener l){
        getChangeListeners().add(l);
        fireEventRequired = true;
    }
    
    /** Remove the ChangeListener.
    
        @param l The ChangeListener
    */
    
    public void removeChangeListener(ChangeListener l){
        ArrayList changeListeners = getChangeListeners();
        changeListeners.remove(l);
        if(changeListeners.size() == 0){
            fireEventRequired = false;
        }
    }
    
    /** Fill the buffer with bytes until the buffer is full, the stream
        blocks, or until the end of the stream.
        
        @param buffer The buffer to fill
        @return The number of bytes read
        @throws IOException
    */
    
    public int read(byte[] buffer) throws IOException{
        int bytesRead = in.read(buffer);
        count += bytesRead;
        if(fireEventRequired) fireStateChanged();
        return bytesRead;
    }
    
    /** Fill the buffer with bytes starting at the given offset of the buffer
        and reading for <code>length</code> number of bytes.
        
        @param buffer The buffer
        @param offset The buffer offset
        @param length The number of bytes to read
        @return The number of bytes read
        @throws IOException
    */
    
    public int read(byte[] buffer, int offset, int length) throws IOException{
        int bytesRead = in.read(buffer, offset, length);
        count += bytesRead;
        if(fireEventRequired) fireStateChanged();
        return bytesRead;
    }
    
    /** Read one byte.
    
        @return The byte
        @throws IOException
    */
    
    public int read() throws IOException{
        int b = in.read();
        count++;
        if(fireEventRequired) fireStateChanged();
        return b;
    }
    
    /** Get the number of bytes read so far.
    
        @return The byte count
    */
    
    public long getCount(){
        return count;
    }
    
    /** Get a List of attached ChangeListeners.
    
        @return The List of ChangeListeners
    */
    
    protected synchronized ArrayList getChangeListeners(){
        if(changeListeners == null){
            changeListeners = new ArrayList();
        }
        return changeListeners;
    }
    
    /** Fire a ChangeEvent. */
    
    protected void fireStateChanged(){
        ChangeEvent evt = new ChangeEvent(this);
        ArrayList l;
        
        synchronized(this){
            l = (ArrayList)(getChangeListeners().clone());
        }
        
        Iterator i = l.iterator();
        while(i.hasNext()){
            ((ChangeListener)i.next()).stateChanged(evt);
        }
    }

}
