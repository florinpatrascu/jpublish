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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful class management utilities.
 *
 * @author Anthony Eden
 * @author Florin PATRASCU
 */

public class ClassUtilities {

    private static Log log = LogFactory.getLog(ClassUtilities.class);

    /**
     * Load the specified class name.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the ClassUtilities
     * class loader is used.
     *
     * @param className The class name
     * @return The Class
     * @throws ClassNotFoundException
     */

    public static Class loadClass(String className)
            throws ClassNotFoundException {
        return loadClass(className, null);
    }

    /**
     * Load the specified class name.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param className The class name
     * @param requestor The object requesting the class or null
     * @return The Class
     * @throws ClassNotFoundException
     */

    public static Class loadClass(String className, Object requestor)
            throws ClassNotFoundException {
        Class requestorClass = null;
        if (requestor == null) {
            requestorClass = ClassUtilities.class;
        } else {
            requestorClass = requestor.getClass();
        }
        return loadClass(className, requestorClass);
    }

    /**
     * Load the specified class name.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param className The class name
     * @param requestor The class of the object requesting the class or null
     * @return The Class
     * @throws ClassNotFoundException
     */

    public static Class loadClass(String className, Class requestor)
            throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            log.warn(className + "; ClassNotFoundException using thread context class loader");
            cl = requestor.getClass().getClassLoader();
            return cl.loadClass(className);
        } catch (SecurityException e) {
            log.warn(className + "; SecurityException using thread context class loader");
            cl = requestor.getClass().getClassLoader();
            return cl.loadClass(className);
        }
    }

    /**
     * Load the specified resource.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the ClassUtilities
     * class loader is used.
     *
     * @param name The resource name
     * @return The resource URL or null
     */

    public static URL getResource(String name) {
        return getResource(name, null);
    }

    /**
     * Load the specified resource.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The resource name
     * @param requestor The object requesting the resource or null
     * @return The resource URL or null
     */

    public static URL getResource(String name, Object requestor) {
        Class requestorClass = null;
        if (requestor == null) {
            requestorClass = ClassUtilities.class;
        } else {
            requestorClass = requestor.getClass();
        }
        return getResource(name, requestorClass);
    }

    /**
     * Load the specified resource.  This method will first attempt to load
     * the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The resource name
     * @param requestor The class of the object requesting the resource or null
     * @return The resource URL or null
     */

    public static URL getResource(String name, Class requestor) {
        URL resource;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        resource = cl.getResource(name);
        if (resource == null) {
            cl = requestor.getClass().getClassLoader();
            resource = cl.getResource(name);
        }
        return resource;
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then ClassUtilities class
     * loader is used.
     *
     * @param name The resource name
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name) {
        return getResourceAsStream(name, null);
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The class name
     * @param requestor The object requesting the resource or null
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name,
                                                  Object requestor) {
        Class requestorClass = null;
        if (requestor == null) {
            requestorClass = ClassUtilities.class;
        } else {
            requestorClass = requestor.getClass();
        }
        return getResourceAsStream(name, requestorClass);
    }

    /**
     * Load the specified resource stream.  This method will first attempt to
     * load the class using the context class loader.  If that fails due to a
     * ClassNotFoundException or a SecurityException then the requestor's
     * class loader is used.  If the requestor object is null then the
     * ClassUtilities class loader is used.
     *
     * @param name      The class name
     * @param requestor The class of the object requesting the resource or null
     * @return The resource stream or null
     */

    public static InputStream getResourceAsStream(String name,
                                                  Class requestor) {
        InputStream resourceStream = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        resourceStream = cl.getResourceAsStream(name);
        if (resourceStream == null) {
            cl = requestor.getClass().getClassLoader();
            resourceStream = cl.getResourceAsStream(name);
        }
        return resourceStream;
    }

    /**
     * Find all super classes and implemented interfaces for the given
     * class.
     *
     * @param startClass The class
     * @return A List of Class objects
     */

    public static List getAllClassesAndInterfaces(Class startClass) {
        ArrayList classes = new ArrayList();

        addClassesAndInterfaces(startClass, classes);

        return classes;
    }

    /**
     * Add all super classes and interfaces of the given class to the given
     * List.
     *
     * @param c       The Class
     * @param classes An List of Classes
     */

    protected static void addClassesAndInterfaces(Class c, List classes) {
        if (c == null) {
            return;
        }

        Class superClass = c.getSuperclass();
        Class[] interfaces = c.getInterfaces();

        if (superClass != null && !classes.contains(superClass)) {
            //System.out.println("Adding " + superClass.getName() + " superclass of " + c.getName());
            classes.add(superClass);
        }

        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] != null && !classes.contains(interfaces[i])) {
                //System.out.println("Adding " + interfaces[i].getName() + " interface of " + c.getName());
                classes.add(interfaces[i]);
            }
        }

        addClassesAndInterfaces(superClass, classes);
        for (int i = 0; i < interfaces.length; i++) {
            addClassesAndInterfaces(interfaces[i], classes);
        }
    }

}
