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

import junit.framework.TestCase;
import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateMatcher;

import java.util.Map;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Dec 5, 2009 5:56:59 PM)
 */
public class RestTest extends TestCase {

    public void testURIBuilder() {
        final String routePath = "/search/{query}/page/{pageNo}";
        JaxRsUriTemplateProcessor templateProcessor = new JaxRsUriTemplateProcessor(routePath);
        UriTemplateMatcher matcher = templateProcessor.matcher();

        assertTrue(matcher.matches("/search/toys and boooks/page/1"));
        Map map = matcher.getVariables(true);

        System.out.println(map);
    }
}
