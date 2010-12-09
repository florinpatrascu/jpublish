/*
 * Copyright (c) 2009. Florin T.PATRASCU
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
 */

package org.jpublish.module.restpathactions;

import com.anthonyeden.lib.config.Configuration;
import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.apache.wink.common.internal.uritemplate.UriTemplateProcessor;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 5, 2010 7:20:49 PM)
 */
public class RestPathActionModel {
    private String path;
    private String action;
    private String page;
    private String methods;
    private Configuration configuration;
    private UriTemplateProcessor templateProcessor;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        templateProcessor = new JaxRsUriTemplateProcessor(path);
    }

    public UriTemplateMatcher matcher() {
        return templateProcessor.matcher();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return "path='" + path + '\'' +
                ", action='" + action + '\'' +
                ", page='" + page + '\'' +
                ", methods='" + methods + '\'';
    }
}
