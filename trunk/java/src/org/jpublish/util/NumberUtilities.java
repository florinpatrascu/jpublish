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

package org.jpublish.util;

import java.text.NumberFormat;

/** Utility class for formatting numbers.

    @author Anthony Eden
    @since 1.4
*/

public class NumberUtilities{
    
     private static final NumberUtilities numberUtilities = new NumberUtilities();
    
    /** Get a shared NumberUtilities instance.
    
        @return A NumberUtilities instance
    */
    
    public static NumberUtilities getInstance(){
        return numberUtilities;
    }
    
    /** Format the specified double to the number of places.
    
        @param d The double
        @param places The number of decimal places
    */
    
    public String format(double d, int places){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(places);
        nf.setMaximumFractionDigits(places);
        return nf.format(d);
    }
    
    /** Format the specified double to the number of places.
    
        @param d The double
        @param places The number of decimal places
    */
    
    public String format(Double d, int places){
        return format(d.doubleValue(), places);
    }
    
    /** Format the specified double to the number of places.
    
        @param d The double
        @param places The number of decimal places
    */
    
    public String format(String d, int places){
        return format(Double.parseDouble(d), places);
    }
    
}
