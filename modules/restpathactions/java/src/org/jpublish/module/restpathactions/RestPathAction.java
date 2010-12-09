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
import com.atlassian.util.profiling.UtilTimerStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;
import org.jpublish.JPublishContext;
import org.jpublish.RepositoryWrapper;
import org.jpublish.action.Action;
import org.jpublish.util.FileCopyUtils;
import org.jpublish.util.PathUtilities;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Oct 5, 2010 6:21:35 PM)
 */
public class RestPathAction implements Action {
    protected static final Log log = LogFactory.getLog(RestPathAction.class);
    public static final String EMPTY_STRING = "";
    public static final String GENERIC_PATH_INFO = "RestPathGenericAction";

    RestPathActionsModule module;
    String modulePath;

    public RestPathAction(String modulePath, RestPathActionsModule module) {
        this.modulePath = modulePath;
        this.module = module;
    }

    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        String method = context.getRequest().getMethod();
        String pathInfo = GENERIC_PATH_INFO;

        try {
            pathInfo = context.getRequest().getPathInfo();
            String path = pathInfo.replaceFirst(modulePath, EMPTY_STRING);

            if (module.isProfiling()) {
                UtilTimerStack.push(pathInfo);
            }

            for (RestPathActionModel rm : module.getRestModels()) {
                final UriTemplateMatcher matcher = rm.matcher();

                if (matcher.matches(path)) {
                    if (module.isDebug()) {
                        log.info(matcher.toString());
                    }

                    MultivaluedMap<String, String> multivaluedMap = matcher.getVariables(true);

                    for (Map.Entry<String, List<String>> entry : multivaluedMap.entrySet()) {
                        if (entry.getValue() != null) {
                            context.put(entry.getKey(),
                                    entry.getValue().size() > 1
                                            ? entry.getValue()
                                            : entry.getValue().get(0));
                        }
                    }

                    module.execute(rm.getAction(), context, rm.getConfiguration());

                    String content = null;

                    final String pagePath = rm.getPage();
                    String viewType = null;

                    if (pagePath != null) {
                        RepositoryWrapper repository = (RepositoryWrapper) context.get(module.getDefaultRepository());
                        viewType = PathUtilities.extractPageType(pagePath);

                        if (repository != null) {
                            content = repository.get(pagePath);
                        }
                    }

                    if (content == null) {
                        content = (String) context.get(RestPathActionsModule.RESPONSE);
                    }


                    HttpServletResponse response = context.getResponse();

                    if (content != null && response != null) {
                        InputStream in;
                        if (content == null) {
                            content = EMPTY_STRING;
                        }

                        String callback = context.getRequest().getParameter(module.getCallbackParameter());
                        if (method.equalsIgnoreCase("get") && callback != null) { //experimental code
                            content = String.format("%s( %s)", callback, content);
                        }

                        in = new ByteArrayInputStream(content.getBytes(
                                module.getSite()
                                        .getCharacterEncodingManager()
                                        .getMap(path).getResponseEncoding()));
                        setResponseContentType(context, path, viewType);

                        long contentLength = FileCopyUtils.copy(in, response.getOutputStream());
                        response.setContentLength((int) contentLength);
                    }

                    break;
                }
            }

        } finally {
            context.setStopProcessing();
            if (module.isProfiling()) {
                UtilTimerStack.pop(pathInfo);
            }
        }
    }

    /**
     * guessing the content type as follows:
     * - if a format request parameter is defined and it is not null, else we look for the page type and
     * we'll try finding a declared MIME type
     *
     * @param context  the JPublish current page context
     * @param viewType the file extension of the view used to fallback for setting the content type
     * @param path     the page path from the request
     */
    private void setResponseContentType(JPublishContext context, String path, String viewType) {
        String format = null;
        String userFormat = null;
        String pageType = PathUtilities.extractPageType(path);

        if (module.getFormatParameter() != null) {
            userFormat = context.getRequest().getParameter(module.getFormatParameter());
            format = userFormat != null ? userFormat : pageType;
        }

        String mimeType = (String) module.getSite().getMimeTypeMap().get(format);

        if (mimeType == null) {
            if (viewType != null) {
                mimeType = (String) module.getSite().getMimeTypeMap().get(viewType.trim().toLowerCase());
            }

            if (mimeType == null) { // fall back on the default mimeType
                mimeType = module.getSite().getMimeTypeMap().getDefaultMimeType();
            }
        }


        String contentType = getMimeTypeWithCharset(mimeType,
                module.getSite().getCharacterEncodingManager().getMap(path).getResponseEncoding());

        context.getResponse().setContentType(contentType);
        //todo: injecting a format.[json|xml|and_so_on] into the context???
    }

    /**
     * Append the charset to the given mime type.
     *
     * @param mimeType The mime type
     * @param charSet  The character set
     * @return The full mimetype + character set
     */

    private String getMimeTypeWithCharset(String mimeType, String charSet) {

        StringBuffer buffer = new StringBuffer();
        buffer.append(mimeType).append("; charset=").append(charSet);
        return buffer.toString();
    }
}
