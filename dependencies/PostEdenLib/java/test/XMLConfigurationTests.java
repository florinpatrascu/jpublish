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

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * unit tests for the {@link com.anthonyeden.lib.config.XMLConfiguration} class
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jul 4, 2007 7:01:27 PM)
 */
public class XMLConfigurationTests extends TestCase {
    private static final Log log = LogFactory.getLog(XMLConfigurationTests.class);
    String hitsTagString = "<hits>Καλημέρα κόσμε</hits>";
    byte[] hitsTagByteArray = {'<', 'h', 'i', 't', 's', '>', 'a', 't', 'e', 'n',
            (byte) 0xE5, (byte) 0xBF, (byte) 0xAE,'<', '/', 'h', 'i', 't', 's', '>', '\n'};

    public void testParseString() throws ConfigurationException, UnsupportedEncodingException {
        log.info("parsing a String containing an xml structure....");

        Configuration c = new XMLConfiguration("hitsTagString", hitsTagString);
        assertEquals("Καλημέρα κόσμε", c.getValue());

        String s = new String(hitsTagByteArray, 0, hitsTagByteArray.length);
        c = new XMLConfiguration("s", s);
        assertEquals("aten忮", c.getValue());
    }

    public void testParseByteArray() throws ConfigurationException, UnsupportedEncodingException {
        log.info("parsing a byte[] containing an xml structure....");

        Configuration c = new XMLConfiguration("hitsTagByteArray",
                new ByteArrayInputStream(hitsTagByteArray));
        assertTrue("aten忮".equals(c.getValue()));
    }
}
