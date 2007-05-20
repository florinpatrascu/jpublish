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

package ca.flop.jpublish.dwr;

import org.jpublish.SiteContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Properties;

/**
 * I am using this implementation to fool the DWRProcessor (a DwrServlet clone).
 * Interim solution
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 1, 2006 4:58:33 PM)
 */
public class FakeServletConfig implements ServletConfig {
    private Properties initParameters = new Properties();
    private ServletContext servletContext;

    public FakeServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getServletName() {
        return "JPublish " + SiteContext.JPUBLISH_VERSION;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getInitParameter(String name) {
        return initParameters.getProperty(name);
    }

    public void setInitParameter(String name, String value) {
        if (name != null & value != null)
            initParameters.setProperty(name, value);
    }

    public Enumeration getInitParameterNames() {
        return initParameters.keys();
    }
}
