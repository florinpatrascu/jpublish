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

package ca.flop.jpublish.components;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.util.ClassUtilities;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpublish.JPublishContext;
import org.jpublish.component.AbstractComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StringTemplate component.
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2007.09.01)
 */
public class JPSTComponent extends AbstractComponent {
    private static final Log log = LogFactory.getLog(JPSTComponent.class);
    public static final String BLANK = " ";
    public static final String EMPTY_STRING = "";
    public static final String GROUP_NAME_TERMINATOR = ":";


    private static final String NAME = "JPStringTemplateComponent";
    private static final String DESCRIPTION =
            "This component offers StringTemplate rendering support to any JPublish applications.";

    private Map stringTemplateGroups;
    private int stringTemplateCacheRefreshInterval = 0;
    private static final String DEFAULT_GROUP_NAME = "default";
    private static final String DEFAULT_TEMPLATE_LEXER = "org.antlr.stringtemplate.language.DefaultTemplateLexer";

    /**
     * Load the component's configuration data.  Implementations should
     * override this method if they require configuration.  If an
     * implementation does override this method the implementation
     * should call <code>super.loadConfiguration(configuration)</code>
     * first.
     *
     * @param configuration The configuration data
     * @throws com.anthonyeden.lib.config.ConfigurationException
     *
     */
    public void loadConfiguration(Configuration configuration) throws ConfigurationException {
        String lexerClassName;
        Class lexer;
        StringTemplateGroup stg;
        int refreshInterval;

        super.loadConfiguration(configuration);

        log.info("loading the StringTemplate rendering support...");

        Configuration stGroupsConfiguration = configuration.getChild("stringtemplate-groups");

        if (stGroupsConfiguration == null) {

            throw new ConfigurationException("'stringtemplate-groups' definition is required");

        } else {

            List stGroups = stGroupsConfiguration.getChildren();
            stringTemplateGroups = new HashMap();

            for (int i = 0; i < stGroups.size(); i++) {
                Configuration stGroupConfiguration = (Configuration) stGroups.get(i);
                String stGroupName = stGroupConfiguration.getAttribute("name", DEFAULT_GROUP_NAME);
                String stGroupDir = stGroupConfiguration.getAttribute("absolute-root-dir", EMPTY_STRING);
                String stRelativeGroupDir = stGroupConfiguration.getAttribute("relative-root-dir");

                if (stRelativeGroupDir != null) {
                    stGroupDir = super.getSiteContext().getRoot().getAbsolutePath() +
                            stRelativeGroupDir;
                }

                if (stGroupConfiguration.getAttribute("lexer") == null) {
                    lexerClassName = DEFAULT_TEMPLATE_LEXER;
                } else {
                    lexerClassName = stGroupConfiguration.getAttribute("lexer", DEFAULT_TEMPLATE_LEXER);
                }

                try {
                    lexer = ClassUtilities.loadClass(lexerClassName);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Stringtemplate Lexer class not found: " + lexerClassName);
                }

                if (stGroupDir != null) {
                    stg = new StringTemplateGroup(stGroupName, stGroupDir, lexer);
                } else {
                    stg = new StringTemplateGroup(stGroupName, lexer);
                }

                refreshInterval = Integer.parseInt(
                        stGroupConfiguration.getAttribute("refresh-interval", "0"));
                stg.setRefreshInterval(refreshInterval);

                stringTemplateGroups.put(stGroupName, stg);
                log.info("... group: " + stGroupName + ", dir: " + stGroupDir + ", cache: " + refreshInterval +
                        "sec.,  lexer: " + lexerClassName + ",defined.");
            }
        }
        log.info("StringTemplate component support available.");

    }

    /**
     * Render the component for the specified path and return the result.
     *
     * @param uriString the stgroup uri locator
     * @param context   The context
     * @return The rendered component
     * @throws Exception
     */
    public String render(String uriString, JPublishContext context) throws Exception {
        STGroup stGroup = new STGroup(uriString);
        StringTemplateGroup group = (StringTemplateGroup) stringTemplateGroups.get(stGroup.getGroupName());
        StringTemplate query = group.getInstanceOf(stGroup.getTemplatePath(), copyContext(context));
        return query.toString();
    }

    /**
     * textilize a given text containing Textile syntax
     *
     * @param text a string containing the Textile syntax
     * @return a textilized text
     * @throws Exception if any
     */
    public String renderText(String text, JPublishContext context) throws Exception {
        StringTemplate query = new StringTemplate(text);
        query.setAttributes(copyContext(context));
        return query.toString();
    }

    /**
     * convenient method to render a StringTemplate in the 'default'
     * StringTemplateGroup
     *
     * @param stringTemplatePath the path to a StringTemplate in the 'default' group name.
     * @return a StringTemplate rendered text
     * @throws Exception if any
     */
    public String renderPath(String stringTemplatePath, JPublishContext context) throws Exception {
        STGroup stGroup = new STGroup(stringTemplatePath);
        StringTemplateGroup group = (StringTemplateGroup) stringTemplateGroups.get(stGroup.getGroupName());
        StringTemplate query = group.getInstanceOf(stGroup.getTemplatePath(), copyContext(context));
        return query.toString();
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public boolean isCacheEnabled() {
        return stringTemplateCacheRefreshInterval > 0;
    }


    /**
     * get a copy of the context attributes map
     * todo: improve the JPublishContext and prepare it for putAll() [florin]
     *
     * @param context the JPublish context
     * @return a copy of the JPublish context attributes map
     */
    private Map copyContext(JPublishContext context) {
        Map map = new HashMap();
        Object[] ctxKeys = context.getKeys();
        for (int i = 0; i < ctxKeys.length; i++) {
            String key = (String) ctxKeys[i];
            map.put(key, context.get(key));
        }
        return map;
    }

    /**
     * parse the given URI and extract the stgroup name and path.
     * The accepted format is: group_name:template_path
     * If the ':' is missing we assume the 'default' string template name group
     * will be used.
     * <p/>
     * [2007.08.19] florin
     */
    protected class STGroup {
        private String groupName = EMPTY_STRING;
        private String templatePath = EMPTY_STRING;

        public STGroup(String uriString) {
            if (uriString != null && uriString.trim().length() > 0) {
                int groupNameTerminatorIndex = uriString.indexOf(GROUP_NAME_TERMINATOR);
                if (groupNameTerminatorIndex <= 0) {
                    groupName = DEFAULT_GROUP_NAME;
                    templatePath = uriString;
                } else {
                    groupName = uriString.substring(0, groupNameTerminatorIndex);
                    templatePath = uriString.substring(groupNameTerminatorIndex + 1);
                    if (log.isDebugEnabled()) {
                        log.debug("accessing StringTemplateGroup: " + groupName + ", template: " + templatePath + "[.st]");
                    }
                }
            } else {
                log.warn("invalid StringTemplate group_name:group_path definiton.");
            }
        }

        public String getGroupName() {
            return groupName;
        }

        public String getTemplatePath() {
            return templatePath;
        }

    }
}
