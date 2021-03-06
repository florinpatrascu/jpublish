/*
 *
 * Copyright 2005 Joe Walker
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

package uk.ltd.getahead.dwrdemo.filter;

import java.lang.reflect.Method;

import javax.servlet.http.HttpSession;

import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;
import org.directwebremoting.WebContextFactory;

/**
 * Manages the current Authentication state.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class AuthenticationAjaxFilter implements AjaxFilter
{
    /* (non-Javadoc)
     * @see org.directwebremoting.AjaxFilter#doFilter(java.lang.Object, java.lang.reflect.Method, java.lang.Object[], org.directwebremoting.AjaxFilterChain)
     */
    public Object doFilter(Object object, Method method, Object[] params, AjaxFilterChain chain) throws Exception
    {
        // We allow anyone to authenticate
        if (authenticateName.equals(method.getName()))
        {
            return chain.doFilter(object, method, params);
        }

        Object user = getUser();
        if (user != null)
        {
            return chain.doFilter(object, method, params);
        }

        throw new SecurityException("Not authenticated");
    }

    /**
     * @return Returns the authenticateName.
     */
    public String getAuthenticateName()
    {
        return authenticateName;
    }

    /**
     * @param authenticateName The authenticateName to set.
     */
    public void setAuthenticateName(String authenticateName)
    {
        this.authenticateName = authenticateName;
    }

    /**
     * What is the name of the authenticate method?
     */
    private String authenticateName = "authenticate";

    /**
     * Accessor for the current user
     * @param user The current user
     */
    public static void setUser(Object user)
    {
        HttpSession session = WebContextFactory.get().getSession(true);
        session.setAttribute(USER, user);
    }

    /**
     * Accessor for the current user
     * @return The current user
     */
    public static Object getUser()
    {
        HttpSession session = WebContextFactory.get().getSession(true);
        return session.getAttribute(USER);
    }

    /**
     * The session key
     */
    private static final String USER = "org.directwebremoting.filter.AuthenticationAjaxFilter.USER";
}
