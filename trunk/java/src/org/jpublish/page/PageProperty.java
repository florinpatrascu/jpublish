/*
 * Copyright 2004-2007 the original author or authors.
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
 *
 */

package org.jpublish.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** A name/value property which is accessible to a specific page.

    @author Anthony Eden
*/

public class PageProperty{
    
    private static final Log log = LogFactory.getLog(PageProperty.class);
    
    private String name;
    private Map values;
    
    /** Construct a page property with the given name.  The name must
        be a non-null value.
    
        @param name The name of the page property
    */
    
    public PageProperty(String name){
        if(name == null){
            throw new IllegalArgumentException("Property name cannot be null");
        }
        
        this.name = name;
        values = new HashMap();
    }
    
    /** Get the property name.
    
        @return The property name
    */
    
    public String getName(){
        return name;
    }
    
    /** Get the property value using the default Locale.
    
        @return The property value using the default Locale
    */
    
    public String getValue(){
        log.debug("getValue()");
        return getValue(Locale.getDefault());
    }
    
    /** Get the value for the given locale.  This method will try to find
        the most suitable locale by searching the property values in the
        following manner:
        
        <p>
        result of locale.toString()
        language + "_" + country + "_" + variant<br>
        language + "_" + country<br>
        langauge<br>
        ""
        </p>
        
        This method may return null if no suitable value is found.
        
        @param locale The locale
        @return The value or null
    */
    
    public String getValue(Locale locale){
        if(log.isDebugEnabled())
            log.debug("getValue(" + locale + ")");
        
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        
        String value = null;
        
        value = (String)values.get(locale.toString());
        if(value != null){
            return value;
        }
        
        if(variant != null){
            value = (String)values.get(
                language + "_" + country + "_" + variant);
            if(value != null){
                return value;
            }
        }
        
        if(country != null){
            value = (String)values.get(language + "_" + country);
            if(value != null){
                return value;
            }
        }
        
        if(language != null){
            value = (String)values.get(language);
            if(value != null){
                return value;
            }
        }
        
        return (String)values.get("");
    }
    
    /** Set the property value for the given Locale.
    
        @param value The value
        @param locale The Locale
    */
    
    public void setValue(String value, Locale locale){
        String localeString = null;
        if(locale != null){
            localeString = locale.toString();
        }
        setValue(value, localeString);
    }
    
    /** Set the property value for the given locale String.  The locale
        String should be in the form language_country_variant as described
        in the Locale.toString() method.
        
        @param value The value
        @param locale The locale String
    */
    
    public void setValue(String value, String locale){
        if(locale == null){
            locale = "";
        }
        values.put(locale, value);
    }
    
    /** Get the Map of all locale values.
    
        @return A map of locale String/value pairs
    */
    
    public Map getValues(){
        return values;
    }

}
