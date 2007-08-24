/*
 *
 * Copyright 2007 Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ca.flop.jpublish.dwr.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;
import org.jpublish.util.FileCopyUtils;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Collection;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Aug 19, 2007 1:54:24 PM)
 */
public class FilePing implements Runnable {
    public static final String EMPTY_STRING = "";
    public static final Log log = LogFactory.getLog(FilePing.class);
    private String requestUrl;
    private String fileName;

    transient long lastRead;
    transient long fileTimeStamp;

    /**
     * Our key to get hold of ServerContexts
     */
    private ServerContext sctx;

    /**
     * Are we refreshing the file contents on all the pages?
     */
    private transient boolean active = false;

    /**
     *
     */
    public FilePing() {
        ServletContext servletContext = WebContextFactory.get().getServletContext();
        sctx = ServerContextFactory.get(servletContext);
    }

    /**
     *
     */
    public synchronized void toggle() {
        active = !active;
        if (active) {
            new Thread(this).start();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            log.debug("FilePing: Starting server-side thread");
            if (fileName != null && fileName.trim().length() > 0)
                while (active) {
                    Collection sessions = sctx.getScriptSessionsByPage(requestUrl);
                    Util pages = new Util(sessions);
                    File file = new File(fileName);

                    if (file.exists()) {
                        fileTimeStamp = file.lastModified();
                        if (lastRead < fileTimeStamp) {
                            pages.setValue("fileDisplay", read(fileName));
                            lastRead = fileTimeStamp;
                            log.info("reading from: " + fileName + "...");
                        }
                    } else {
                        pages.setValue("fileDisplay", " File: " + fileName + ", not found!");
                    }

                    log.debug("Sent message");
                    Thread.sleep(1000);
                }

            Collection sessions = sctx.getScriptSessionsByPage(requestUrl);
            Util pages = new Util(sessions);
            pages.setValue("fileDisplay", EMPTY_STRING);
            lastRead = 0L;
            log.debug("FilePing: Stopping server-side thread");
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * return the contents of a file
     *
     * @param fileName the file name
     * @return the file contents as a String
     */
    public String read(String fileName) {
        Reader in;
        try {
            in = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Error! File no found: " + fileName;
        }
        try {
            return FileCopyUtils.copyToString(in);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error! Can't read the file: " + fileName;
        }
    }

    // a bit of inversion of control here ;)
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
