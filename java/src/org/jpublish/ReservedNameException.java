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

package org.jpublish;

/** Exception which is thrown when an action attempts to put a value into
    the context using a reserved name.

    @author Anthony Eden
*/

public class ReservedNameException extends JPublishRuntimeException{

    private String name;
    
    /** Construct a ReservedNameException for the given name.
    
        @param name The reserved name
    */

    public ReservedNameException(String name){
        this("Reserved name: " + name, name);
    }
    
    /** Construct a ReservedNameException for the given name
        with the given message.
        
        @param message The error message
        @param name The reserved name
    */
    
    public ReservedNameException(String message, String name){
        super(message);
        this.name = name;
    }

    /** Get the reserved name.

        @return The reserved name
    */

    public String getName(){
        return name;
    }

}
