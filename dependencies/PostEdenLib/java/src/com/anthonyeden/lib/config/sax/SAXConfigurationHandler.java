package com.anthonyeden.lib.config.sax;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationBase;
import com.anthonyeden.lib.config.Location;
import com.anthonyeden.lib.config.MutableConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;

/**
 * Handler implementation
 *
 * @author Anthony Eden
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since 2.0
 */

class SAXConfigurationHandler extends DefaultHandler {
    protected static final Log log = LogFactory.getLog( SAXConfigurationHandler.class);
    private SAXConfigurationFactory factory;
    private String id;

    private Locator locator;

    private LinkedList configurationStack = new LinkedList();
    private LinkedList valueStack = new LinkedList();

    private Configuration rootConfiguration;

    private MutableConfiguration parentConfiguration;
    private MutableConfiguration currentConfiguration;
    private StringBuffer currentValue;

    /**
     * Construct a new SAXConfigurationHandler with the given root configuration
     * object.
     *
     * @param factory
     * @param id
     */

    SAXConfigurationHandler(SAXConfigurationFactory factory, String id) {
        this.factory = factory;
        this.id = id;
    }

    public Configuration getConfiguration() {
        return rootConfiguration;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        //System.out.println("Start element: " + qName);

        if (currentValue != null) {
            valueStack.add(currentValue);
        }

        if (currentConfiguration != null) {
            configurationStack.add(currentConfiguration);
            parentConfiguration = currentConfiguration;
        }

        Location location = new Location(id, locator.getLineNumber(),
            locator.getColumnNumber());
        currentConfiguration = new ConfigurationBase(qName, null,
            parentConfiguration, location);
        if (parentConfiguration != null) {
            parentConfiguration.addChild(currentConfiguration);
        }

        //System.out.println("Attributes length: " + attributes.getLength());
        for (int i = 0; i < attributes.getLength(); i++) {
            String value = attributes.getValue(i);
            String name = attributes.getQName(i);
            currentConfiguration.addAttribute(name, value);
        }

        currentValue = new StringBuffer();
    }

    public void endElement(String uri, String localName, String qName) {
        //System.out.println("End element: " + qName);
        //System.out.println("Setting the value of " + 
        //    currentConfiguration.getName() + " to " + currentValue);

        currentConfiguration.setValue(currentValue.toString());

        if (configurationStack.size() > 0) {
            currentConfiguration =
                (MutableConfiguration) configurationStack.removeLast();
        } else {
            rootConfiguration = currentConfiguration;
        }

        if (valueStack.size() > 0) {
            currentValue = (StringBuffer) valueStack.removeLast();
        }
    }

    public SAXConfigurationFactory getFactory() {
        return factory;
    }

}
