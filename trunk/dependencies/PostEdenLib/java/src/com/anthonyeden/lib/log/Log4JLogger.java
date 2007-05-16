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

package com.anthonyeden.lib.log;

import java.io.File;

import org.apache.log4j.Category;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**	An implementation of the Logger interface which uses Log4J as its
	logging implementation.
    
    <b>This class is deprecated.</b>  All EdenLib classes now use the
    Apache Jakarta Commons logging library.
	
	@author Anthony Eden
    @deprecated
*/

public class Log4JLogger implements LoggerInternal{

	/**	Configure the logger using the Log4J BasicConfigurator. */
	
	public static void configure(){
		BasicConfigurator.configure();
	}
	
	/** Configure the logger using the given property file.
	
		@param file The file
	*/
	
	public static void configure(File file){
		PropertyConfigurator.configure(file.getAbsolutePath());
	}
	
	/**	Initialize the logger with the given identifier.  The identifier
		can be any string and may or may not be included in the final
		log stream (depending on the implementation.)
		
		@param identifier The identifier
	*/
	
	public void init(String identifier){
		log = Category.getInstance(identifier);
	}
	
	/**	Send a debug message.
	
		@param message The message
	*/

	public void debug(String message){
		checkInit();
		log.debug(message);
	}
	
	/**	Send an info message.
	
		@param message The message
	*/
	
	public void info(String message){
		checkInit();
		log.info(message);
	}
	
	/**	Send a warn message.
	
		@param message The message
	*/
	
	public void warn(String message){
		checkInit();
		log.warn(message);
	}
	
	/**	Send an error message.
	
		@param message The message
	*/
	
	public void error(String message){
		checkInit();
		log.error(message);
	}
	
	/**	Send a fatal message.
	
		@param message The message
	*/
	
	public void fatal(String message){
		checkInit();
		log.fatal(message);
	}
	
	/**	Check if the class is already inited. */
	
	public void checkInit(){
		if(log == null){
			throw new IllegalStateException("Logger must be initialized first");
		}
	}
	
	private Category log;
	
}
