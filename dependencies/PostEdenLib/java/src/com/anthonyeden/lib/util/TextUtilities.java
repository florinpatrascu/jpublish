/*-- 

 Copyright (C) 2000-2003 Anthony Eden.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "EdenLib" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact me@anthonyeden.com.
 
 4. Products derived from this software may not be called "EdenLib", nor
    may "EdenLib" appear in their name, without prior written permission
    from Anthony Eden (me@anthonyeden.com).
 
 In addition, I request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, 
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.

 For more information on EdenLib, please see <http://edenlib.sf.net/>.
 
 */

package com.anthonyeden.lib.util;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

/** Useful text manipulation methods.

    @author Anthony Eden
*/

public final class TextUtilities{
    
    public static final String DEFAULT_DELIMITER = ",";

    /** Constructor. */

    private TextUtilities(){
        // no op
    }
    
    /** Replace all instances of a String within another String.
    
        @param str The complete String
        @param searchString The search String
        @param replacementString The replacement String
    */
    
    public static String replace(String str, String searchString, 
    String replacementString){
        int offset = 0;
        int searchLength = searchString.length();
        int replaceLength = replacementString.length();
        int index = str.indexOf(searchString);
        while(index >= 0){
            StringBuffer buffer = new StringBuffer();
            offset = index + replaceLength;
            buffer.append(str.substring(0, index));
            buffer.append(replacementString);
            buffer.append(str.substring(index + searchLength));
            str = buffer.toString();
            index = str.indexOf(searchString);
        }
        return str;
    }
    
    /** Remove all instances of the given characters from the given String.
    
        @param src The source String
        @param removeChars The characters to remove
        @return The result String
    */
    
    public static String remove(String src, char[] removeChars){
        StringBuffer buffer = new StringBuffer();
        char[] array = new char[src.length()];
        src.getChars(0, src.length(), array, 0);
        LOOP: for(int i = 0; i < array.length; i++){
            for(int j = 0; j < removeChars.length; j++){
                if(array[i] == removeChars[j]){
                    continue LOOP;
                }
            }
            buffer.append(array[i]);
        }
        return buffer.toString();
    }
    
    /** Remove all whitespace from the given String.
    
        @param src The source String
        @return The resulting String
    */
    
    public static String removeWhitespace(String src){
        StringBuffer buffer = new StringBuffer();
        char[] array = new char[src.length()];
        src.getChars(0, src.length(), array, 0);
        for(int i = 0; i < array.length; i++){
            if(!Character.isWhitespace(array[i])){
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    /** Returns true if the given String contains at least one non alpha-
        numeric character.
        
        @param str The String
        @return True if one or more alpha-numeric characters are found
    */  

    public static boolean hasNonAlphaNumericCharacters(String str){
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(!Character.isLetterOrDigit(chars[i])){
                return true;
            }
        }
        return false;
    }
    
    /** Returns true if the given String contains at least one non alpha-
        numeric character.  Characters in the ignore array are not checked.
        
        @param str The String
        @param ignore An array of characters to ignore
        @return True if one or more alpha-numeric characters are found
    */  
    
    public static boolean hasNonAlphaNumericCharacters(String str, char[] ignore){
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(isIgnore(chars[i], ignore)){
                continue;
            }
        
            if(!Character.isLetterOrDigit(chars[i])){
                return true;
            }
        }
        return false;
    }
    
    /** String all non alpha-numeric characters from the given String.
    
        @param str The String
        @return The new String
    */

    public static String stripNonAlphaNumericCharacters(String str){
        StringBuffer buffer = new StringBuffer();
        
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(Character.isLetterOrDigit(chars[i])){
                buffer.append(chars[i]);
            }
        }
        
        return buffer.toString();
    }
    
    /** Strip all non alpha-numeric characters from the given String.  Characters
        contained in the ignore array will be left within the String.
        
        @param str The String
        @param ignore An array of characters to ignore
        @return The new String
    */
    
    public static String stripNonAlphaNumericCharacters(String str, char[] ignore){
        StringBuffer buffer = new StringBuffer();
        
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(isIgnore(chars[i], ignore)){
                continue;
            }
            
            if(Character.isLetterOrDigit(chars[i])){
                buffer.append(chars[i]);
            }
        }
        
        return buffer.toString();
    }
    
    /** Capitalize the first letter of the given text.
    
        @param text The original text
        @return The capitalized text
    */
    
    public static String capitalize(String text){
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    /** Split the given text using the given separator.
    
        @param text The original text
        @param separator The separator
        @return The result List of String objects
    */
    
    public static List splitString(String text, String separator){
        return splitString(text, separator, true);
    }
    
    /** Split the given text using the given separator.
    
        @param text The original text
        @param separator The separator
        @return The result List of String objects
    */
    
    public static List splitString(String text, String separator, boolean removeWhitespace){
        ArrayList elements = new ArrayList();
        StringTokenizer tk = new StringTokenizer(text, separator);
        while(tk.hasMoreTokens()){
            if(removeWhitespace){
                elements.add(removeWhitespace(tk.nextToken()));
            } else {
                elements.add(tk.nextToken());
            }
        }
        return elements;
    }
    
    /** Split the given String on whitespace characters.
    
        @param text The source String
        @return A List of String objects
    */
    
    public static List splitStringOnWhitespace(String text){
        List elements = new ArrayList();
        StringBuffer buffer = new StringBuffer();
        char[] chars = text.toCharArray();
        for(int i = 0; i < chars.length; i++){
            if(Character.isWhitespace(chars[i])){
                if(buffer.length() > 0){
                    elements.add(buffer.toString());
                    buffer = new StringBuffer();
                }
            } else {
                buffer.append(chars[i]);
            }
        }
        
        if(buffer.length() > 0){
            elements.add(buffer.toString());
        }
        
        return elements;
    }
    
    /** Split the String on newlines.
    
        @param text The source text
        @return A List of String objects
    */
    
    public static List chompString(String text){
        return splitString(text, "\n");
    }
    
    /** Join the given Object array using the DEFAULT_DELIMITER.
    
        @param data The data array
        @return The resulting String
    */
    
    public static String join(Object[] data){
        return join(data, DEFAULT_DELIMITER);
    }
    
    /** Join the given Object array using the given delimiter.  The toString()
        method on each Object will be invoked to retrieve a String representation
        of the Object.
        
        @param data The data array
        @param delimiter The delimiter String
        @return The resulting String
    */
    
    public static String join(Object[] data, String delimiter){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < data.length; i++){
            buffer.append(data.toString());
            if(i < data.length - 1){
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
    /** Join the given Collection using the DEFAULT_DELIMITER.
    
        @param data The collection
        @return The resulting String
    */
    
    public static String join(Collection data){
        return join(data, DEFAULT_DELIMITER);
    }
    
    /** Join the given Collection using the given delimiter.  The toString()
        method on each Object will be invoked to retrieve a String representation
        of the Object.
        
        @param data The collection
        @param delimiter The delimiter String
        @return The resulting String
    */
    
    public static String join(Collection data, String delimiter){
        StringBuffer buffer = new StringBuffer();
        Iterator iter = data.iterator();
        while(iter.hasNext()){
            buffer.append(iter.next().toString());
            if(iter.hasNext()){
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
    /** Return true if the character is an ASCII character.
    
        @param c The character
        @return True if character is ASCII
    */
    
    public static boolean isAscii( char c ){
        return (c & 0xff80) == 0;
    }
    
    /** Return true if the character is an extended ASCII character.
    
        @param c The character
        @return True if character is extended ASCII
    */
    
    public static boolean isExtendedAscii( char c ){
        return (c & 0xff00) == 0;
    }
    
    /** Return true if the given character is a member of the ignore array.
    
        @param c The character
        @param ignore An array of characters to ignore
        @return True if the character is in the ignore array
    */
    
    private static boolean isIgnore(char c, char[] ignore){
        for(int i = 0; i < ignore.length; i++){
            if(c == ignore[i]){
                return true;
            }
        }
        return false;
    }

}
