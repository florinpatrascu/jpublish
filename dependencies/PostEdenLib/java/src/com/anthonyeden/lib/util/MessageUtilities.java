package com.anthonyeden.lib.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utilities for retrieving localized messages.
 *
 * @author Anthony Eden
 */
 
public class MessageUtilities {
    
    /** 
     * Private constructor so that instances of MessageUtilities are not
     * constructed.
     */
    
    private MessageUtilities() {
        // no op
    }
    
    /**
     * Locate the given message and merge it with the given arguments using
     * the MessageFormat class.  This method uses ResourceBundle to locate 
     * a bundle called org.jpublish.messages and then pulls the appropriate
     * message.
     *
     * @param name The message name
     * @param args The arguments
     * @return The message
     */
    
    public static String getMessage(String pkg, String name, Object[] args) {
        return getMessage(pkg, name, args, Locale.getDefault());
    }
    
    /**
     * Locate the given message and merge it with the given arguments using
     * the MessageFormat class.  This method uses ResourceBundle to locate 
     * a bundle called org.jpublish.messages and then pulls the appropriate
     * message. 
     * 
     * <p>The pkg argument is used to prepend a package name to the 
     * bundle name so that different JARs can have different bundles. For
     * example, if pkg is com.foo then the Bundle should be located by the
     * classloader at com/foo/messages.properties.  pkg can be null in which
     * case /messages.properties will be used.
     *
     * @param pkg The package
     * @param name The message name
     * @param args The arguments
     * @param locale The Locale
     * @return The message
     */
    
    public static String getMessage(String pkg, String name, Object[] args, 
    Locale locale) {
        String propertiesPath = null;
        if (pkg == null) {
            propertiesPath = "messages";
        } else {
            propertiesPath = pkg + ".messages";
        }
        
        ResourceBundle res = ResourceBundle.getBundle(propertiesPath, locale);
        
        return MessageFormat.format(res.getString(name), args);
    }
    
    /**
     * Locate the given message and merge it with the given arguments using
     * the MessageFormat class.  This method uses ResourceBundle to locate 
     * a bundle called org.jpublish.messages and then pulls the appropriate
     * message.  The actual message key is determined by concatenating the
     * class name and the name parameter.
     *
     * @param c The requesting class
     * @param pkg The package name
     * @param name The message name
     * @param args The arguments
     * @return The message
     */
    
    public static String getMessage(Class c, String pkg, String name, 
    Object[] args) {
        return getMessage(pkg, c.getName() + "." + name, args, 
            Locale.getDefault());
    }
    
    /**
     * Locate the given message and merge it with the given arguments using
     * the MessageFormat class.  This method uses ResourceBundle to locate 
     * a bundle called org.jpublish.messages and then pulls the appropriate
     * message.  The actual message key is determined by concatenating the
     * class name and the name parameter.
     *
     * @param c The requesting Class
     * @param name The message name
     * @param args The arguments
     * @param locale The locale
     * @return The message
     */
    
    public static String getMessage(Class c, String pkg, String name, 
    Object[] args, Locale locale) {
        return getMessage(pkg, c.getName() + "." + name, args, locale);
    }
    
}
