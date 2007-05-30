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

package ca.flop.jpublish.i18n;

import ca.flop.jpublish.SpringActionSupport;
import com.anthonyeden.lib.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: May 29, 2007 12:15:15 AM)
 */
public class SpringI18nSupport extends SpringActionSupport {
    public static final String I18N = "i18n";
    protected static final Log log = LogFactory.getLog(SpringI18nSupport.class);

    /**
     * Execute the action using the given context.
     *
     * @param context       The current context
     * @param configuration The configuration
     * @throws Exception Any error
     */
    public void execute(JPublishContext context, Configuration configuration) throws Exception {
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        HttpServletResponse response = (HttpServletResponse) context.get("response");
        Page page = ((Page) context.get("page"));

        if (page != null) {
            Locale locale = page.getLocale();

            ApplicationContext applicationContext = getApplicationContext(context);
            context.put(SPRING, applicationContext);
            HandlerInterceptor localeChangeInterceptor = (HandlerInterceptor)
                    applicationContext.getBean("localeChangeInterceptor");

            if (localeChangeInterceptor != null) {
                try {
                    LocaleResolver localeResolver = (LocaleResolver) applicationContext.getBean("localeResolver");
                    if (localeResolver != null) {
                        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, localeResolver);
                        localeChangeInterceptor.preHandle(request, response, null);
                        locale = localeResolver.resolveLocale(request);
                        MessageSource messageSource = (MessageSource) applicationContext.getBean("messageSource");
                        if (messageSource != null) {
                            context.put(I18N, new MessageSourceAccessor(messageSource, locale));
                        } else {
                            log.error("There is no 'messageSource' defined in your application context." +
                                    " Please define one.");
                        }
                    } else {
                        log.error("please define a 'localeResolver' bean in your Application context");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Cannot handle the locale change event");
                }
            }
            page.setLocale(locale);
        }
    }
}
