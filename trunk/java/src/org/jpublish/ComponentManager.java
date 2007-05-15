/*
 * Copyright (C) 2001-2003 Aetrion LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows
 *    these conditions in the documentation and/or other materials
 *    provided with the distribution.
 *
 * 3. The name "JPublish" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact info@aetrion.com.
 *
 * 4. Products derived from this software may not be called "JPublish", nor
 *    may "JPublish" appear in their name, without prior written permission
 *    from Aetrion LLC (info@aetrion.com).
 *
 * In addition, the authors of this software request (but do not require)
 * that you include in the end-user documentation provided with the
 * redistribution and/or in the software itself an acknowledgement equivalent
 * to the following:
 *     "This product includes software developed by
 *      Aetrion LLC (http://www.aetrion.com/)."
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.jpublish;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import java.util.Iterator;

/**
 * Standard interface implemented by component managers.
 *
 * @author Anthony Eden
 * @since 2.0
 */

public interface ComponentManager {

    /**
     * Set the SiteContext.
     *
     * @param siteContext The site context
     */

    public void setSiteContext(SiteContext siteContext);

    /**
     * Get a component.
     *
     * @return The ComponentInstance
     * @throws Exception
     */

    public JPublishComponent getComponent(String id) throws Exception;

    /**
     * Return an Iterator of all components.
     *
     * @return An Iterator of Components
     */

    public Iterator getComponents();

    /**
     * Load the ComponentManager's configuration.
     *
     * @param configuration The Configuration object
     * @throws ConfigurationException
     */

    public void loadConfiguration(Configuration configuration) throws
            ConfigurationException;

}
