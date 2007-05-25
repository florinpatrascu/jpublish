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

package com.anthonyeden.lib.config.sax;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationBase;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.ConfigurationFactory;
import com.anthonyeden.lib.util.ClassUtilities;
import com.anthonyeden.lib.util.IOUtilities;
import com.anthonyeden.lib.util.MessageUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.*;
import java.util.Properties;

/**
 * Implementation of the ConfigurationFactory interface which generates a
 * a configuration tree using a SAX parser.
 * <p/>
 * -fixed the unicode parsing [Florin]
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 2.0
 */

public class SAXConfigurationFactory implements ConfigurationFactory {
    public static final String CONFIG_FILE="edenlib-config.properties";
    public static final String DEFAULT_CONFIG_FILE="/edenlib-default-config.properties";

    private static final Log log = LogFactory.getLog(SAXConfigurationFactory.class);
    private static final SAXConfigurationFactory INSTANCE = new SAXConfigurationFactory();
    public static final String PKG = "com.anthonyeden.lib";
    public static final int BUFFER_SIZE = 4096;
    private static String parserClassName;
    private static boolean ns = false;

    private SAXConfigurationFactory() {
        Properties properties = new Properties();
        InputStream in;
        try {
            in = ClassUtilities.getResourceAsStream(CONFIG_FILE);

        } catch (Exception e) {
            log.warn("Could not load " + CONFIG_FILE + " file. Falling back to defaults.");
            in = getClass().getResourceAsStream(DEFAULT_CONFIG_FILE);//"com.bluecast.xml.Piccolo";
        }

        try {
            properties.load(in);
            parserClassName = properties.getProperty("org.xml.sax.parser");
            ns = properties.getProperty("namespaces").equalsIgnoreCase("on");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static SAXConfigurationFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get the root Configuration object from the specified InputStream.
     *
     * @param id The id of the configuration (file path, URL, etc)
     * @param in The InputStream
     * @return The Configuration object
     * @throws ConfigurationException
     */

    public Configuration getConfigurationX(String id, InputStream in)
            throws ConfigurationException {
        try {
            return getConfiguration(id, new InputStreamReader(in, ConfigurationBase.ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Old method used just to maintain the backward compatibility
     *
     * @param id
     * @param in
     * @return
     * @throws ConfigurationException
     * @deprecated
     */
    public Configuration getConfiguration(String id, Reader in) throws ConfigurationException {

        try {
            return getConfiguration(id,
                    new ByteArrayInputStream(
                            IOUtilities.copyToString(in).getBytes("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConfigurationException(e);
        }
    }

    /**
     * Get the root Configuration object from the specified InputStream.
     *
     * @param id The id of the configuration (file path, URL, etc)
     * @param in The InputStream
     * @return The Configuration object
     * @throws ConfigurationException
     */

    public Configuration getConfiguration(String id, InputStream in) throws ConfigurationException {
        SAXConfigurationHandler handler = new SAXConfigurationHandler(this, id);

        try {
            XMLReader xr = (XMLReader) ClassUtilities.loadClass(parserClassName).newInstance(); //XMLReaderFactory.createXMLReader();

            xr.setErrorHandler(handler);
            xr.setContentHandler(handler);

            try {
                xr.setFeature("http://xml.org/sax/features/namespaces", ns);
                xr.setFeature("http://xml.org/sax/features/namespace-prefixes", !ns);
            }
            catch (SAXException e) {
                log.info("Warning: could not set namespace feature to " + ns);
                try {
                    boolean currentValue;
                    log.info("Current value is " + xr.getFeature("http://xml.org/sax/features/namespaces"));
                }
                catch (SAXException x) {
                    log.info("Current value is unknown but probably false.");
                }
            }

            xr.parse(new InputSource(in));
            return handler.getConfiguration();
        } catch (SAXException e) {
            Object[] args = {id, e.getMessage()};
            throw new ConfigurationException(MessageUtilities.getMessage(
                    getClass(), PKG, "parseSAXError", args));
        } catch (IOException e) {
            Object[] args = {id, e.getMessage()};
            throw new ConfigurationException(MessageUtilities.getMessage(
                    getClass(), PKG, "parseIOError", args));
        } catch (IllegalAccessException e) {
            throw new ConfigurationException("illegal access to: " + parserClassName, e);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("class: " + parserClassName + ", not found", e);
        } catch (InstantiationException e) {
            throw new ConfigurationException("cannot instantiate: " + parserClassName, e);
        }
    }

}
