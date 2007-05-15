/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * Created with IntelliJ IDEA.
 * User: Florin T.PATRASCU aka fpatrascu
 * Date: Apr 28, 2004
 * Time: 1:07:30 PM
 */
package org.jpublish.util;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.SiteContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author
 */
public class PathDispatcherManager {
    private static Log log = LogFactory.getLog(PathDispatcherManager.class);
    private static Map mappingInclude;
    private static Map mappingForward;

    private SiteContext siteContext;

    public PathDispatcherManager(SiteContext siteContext) {
        mappingInclude = new LinkedHashMap(); //I just want to keep the insertion order ;)
        mappingForward = new LinkedHashMap(); //I just want to keep the insertion order ;)
        //servletContext = siteContext.getServletContext(); //is not defined yet by JPublish :(
        this.siteContext = siteContext;
    }

    public void loadConfiguration(Configuration configuration)
            throws ConfigurationException {

        // load path-dispatcher definitions
        Iterator defineDispatcherElements = configuration.getChildren("path-dispatcher").iterator();

        while (defineDispatcherElements.hasNext()) {
            Configuration defineDispatcherElement =
                    (Configuration) defineDispatcherElements.next();
            String name = defineDispatcherElement.getAttribute("name");
            String path = defineDispatcherElement.getAttribute("path");

            String action = defineDispatcherElement.getAttribute("action");
            if ("forward".equalsIgnoreCase(action)) {
                mappingForward.put(path, name);
            } else {
                mappingInclude.put(path, name);
            }

            if (log.isDebugEnabled())
                log.debug("Defined `" + action + "` action for path: " +
                        path + ", to be dispatched by: [" + name + "]");
        }
    }

    public boolean canDispatch(String path) {
        return canForwardDispatch(path) != null || canIncludeDispatch(path) != null;
    }

    public String canForwardDispatch(String path) {
        //todo: add regexp and replace countMatches brother ;)
        String dispatcherName = null;

        Iterator paths = mappingForward.keySet().iterator();
        while (paths.hasNext()) {
            String pathMapping = (String) paths.next();
            if (countMatches(path, pathMapping) > 0) {
                return (String) mappingForward.get(pathMapping);
            }
        }
        return dispatcherName;
    }

    public String canIncludeDispatch(String path) {
        String dispatcherName = null;

        Iterator paths = mappingInclude.keySet().iterator();
        while (paths.hasNext()) {
            String pathMapping = (String) paths.next();
            if (countMatches(path, pathMapping) > 0) {
                return (String) mappingInclude.get(pathMapping);
            }
        }
        return dispatcherName;
    }

    public void forward(HttpServletRequest request, HttpServletResponse response, String dispatcherName)
            throws IOException, ServletException {

        if (dispatcherName != null) {
            RequestDispatcher rd = siteContext.getServletContext().getNamedDispatcher(dispatcherName);
            rd.forward(request, response);
        }
    }

    /**
     * This method is damn tricky.
     * Ask Anthony how we can re-process the input supplied by our
     * new "slave": the Servlet which just proccessed the request ;)
     * <p/>
     * We can therefore to "influence" the output by using Velocity or Actions...
     * todo... check this f@#$ gizmo
     * <p/>
     * BufferedReader br = new BufferedReader( new InputStreamReader(request.getInputStream() ) );
     * String data = br.readLine();
     * ...todo: eat this data
     * if(br !=null) br.close();
     * apparently is working but is weird :)) I hope is not a mistake
     *
     * @param request
     * @param response
     * @param dispatcherName
     * @throws IOException
     * @throws ServletException
     */
    public void include(HttpServletRequest request, HttpServletResponse response, String dispatcherName)
            throws IOException, ServletException {

        if (dispatcherName != null) {
            RequestDispatcher rd = siteContext.getServletContext().getNamedDispatcher(dispatcherName);
            rd.include(request, response);
            // unfinished....
        }
    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException, ServletException {
        //todo: add a decisional logic to decide if is forward or include...
    }

    //HELPER methods from Apache's StringUtils library

    // Count matches
    //-----------------------------------------------------------------------
    /**
     * <p>Counts how many times the substring appears in the larger String.</p>
     * <p/>
     * <p>A <code>null</code> or empty ("") String input returns <code>0</code>.</p>
     * <p/>
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str the String to check, may be null
     * @param sub the substring to count, may be null
     * @return the number of occurances, 0 if either String is <code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (str == null || str.length() == 0 || sub == null || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
