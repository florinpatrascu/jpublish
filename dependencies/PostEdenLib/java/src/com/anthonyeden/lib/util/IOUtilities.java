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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;

import java.io.*;

/**
 * Useful IO utilities.
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 */

public class IOUtilities {

    private static final Log log = LogFactory.getLog(IOUtilities.class);
    public static final int BUFFER_SIZE = 4096;

    private IOUtilities() {

    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(InputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing stream: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(Reader s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing reader: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(OutputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing stream: " + e.getMessage());
            }
        }
    }

    /**
     * Close the given stream if the stream is not null.
     *
     * @param s The stream
     */

    public static void close(Writer s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                log.error("Error closing writer: " + e.getMessage());
            }
        }
    }

    /**
     * Read the data from the given file into a byte array and return
     * the array.
     *
     * @param file The file
     * @return The byte array
     * @throws IOException
     */

    public static byte[] readData(File file) throws IOException {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream();

            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            return out.toByteArray();
        } finally {
            IOUtilities.close(in);
            IOUtilities.close(out);
        }
    }

    /**
     * Read the data from the given file into a byte array and return
     * the array.
     *
     * @param file The file
     * @return The byte array
     * @throws IOException
     */

    public static byte[] readData(FileObject file) throws IOException {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            FileContent content = file.getContent();
            in = new BufferedInputStream(content.getInputStream());
            out = new ByteArrayOutputStream();

            int c = -1;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            return out.toByteArray();
        } finally {
            IOUtilities.close(in);
            IOUtilities.close(out);
        }
    }

    /**
     * Write the byte array to the given file.
     *
     * @param file The file to write to
     * @param data The data array
     * @throws IOException
     */

    public static void writeData(File file, byte[] data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
        } finally {
            close(out);
        }
    }

    /**
     * Write the byte array to the given file.
     *
     * @param file The file to write to
     * @param data The data array
     * @throws IOException
     */

    public static void writeData(FileObject file, byte[] data)
            throws IOException {
        OutputStream out = null;
        try {
            FileContent content = file.getContent();
            out = content.getOutputStream();
            out.write(data);
        } finally {
            close(out);
        }
    }

    /**
     * Read the data from the given reader and return it is a single String.
     *
     * @param in The Reader
     * @return The String
     * @throws IOException
     */

    public static String getStringFromReader(Reader in) throws IOException {
        return copyToString(in);
    }

    /**
     * Copy the contents of the given Reader into a String.
     * Closes the reader when done.
     *
     * @param in the reader to copy from
     * @return the String that has been copied to
     * @throws IOException in case of I/O errors
     */
    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param in  the Reader to copy from
     * @param out the Writer to copy to
     * @return the number of characters copied
     * @throws IOException in case of I/O errors
     */
    public static int copy(Reader in, Writer out) throws IOException {
        try {
            int byteCount = 0;
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
                log.warn("Could not close Reader", ex);
            }
            try {
                out.close();
            }
            catch (IOException ex) {
                log.warn("Could not close Writer", ex);
            }
        }
    }
}
