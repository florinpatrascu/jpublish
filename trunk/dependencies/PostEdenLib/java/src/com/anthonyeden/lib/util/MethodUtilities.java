/*-- 

 Copyright (C) 2001 Anthony Eden.
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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/** Utilities for invoking methods at runtime using reflection.

    @author Anthony Eden
*/

public class MethodUtilities{
    
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    private MethodUtilities(){
    
    }
    
    /** Invoke the given method.  This method is a convenience method which 
        creates an array of arguments with the single arg value.  This is 
        necessary because the Method.invoke() method only accepts an array of 
        argument objects.
    
        @param method The method
        @param target The target Object
        @param arg A single argument
        @return The return object or null
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
    */

    public static Object invoke(Method method, Object target, Object arg) throws
    IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        if(arg == null){
            return invoke(method, target, EMPTY_OBJECT_ARRAY);
        } else {
            Object[] args = new Object[1];
            args[0] = arg;
            return invoke(method, target, args);
        }
    }
    
    /** Invoke the given method.  This method actually invokes the target
        method with the given arguments.
    
        @param method The Method
        @param target The target Object
        @param args An array of argument Objects
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
    */
    
    public static Object invoke(Method method, Object target, Object[] args)
    throws IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException{
        return method.invoke(target, args);
    }
    
    /** Invoke the given method.  The target object is used to determine
        the target class and which is then used to get a Method object.
        This method is a convenience method which creates an array of arguments 
        with the single arg value.  This is necessary because the 
        Method.invoke() method only accepts an array of argument objects.
    
        @param methodName The method name
        @param target The target Object
        @param arg A single argument
        @return The return object or null
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invoke(String methodName, Object target, Object arg) 
    throws IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException, NoSuchMethodException, SecurityException{
        if(arg == null){
            return invoke(methodName, target, EMPTY_OBJECT_ARRAY);
        } else {
            Object[] args = new Object[1];
            args[0] = arg;
            return invoke(methodName, target, args);
        }
    }
    
    /** Invoke the given method.  The target object is used to determine
        the target class and which is then used to get a Method object.
    
        @param methodName The Method name
        @param target The target Object
        @param args An array of argument Objects
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invoke(String methodName, Object target, Object[] args) 
    throws IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException, NoSuchMethodException, SecurityException{
        return invoke(methodName, target, target.getClass(), args);
    }
    
    /** Invoke the given method.  The target object may be null in which
        case the target class is used and the method must be a static
        method.
    
        @param methodName The method name
        @param target The target Object
        @param targetClass The Class of the target object
        @param args An array of argument Objects
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invoke(String methodName, Object target, 
    Class targetClass, Object[] args) throws IllegalAccessException, 
    IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
    SecurityException
    {
        Class[] paramClasses = new Class[args.length];
        for(int i = 0; i < args.length; i++){
            paramClasses[i] = args[i].getClass();
        }
        Method method = getMethod(targetClass, methodName, paramClasses);
        return invoke(method, target, args);
    }
    
    /** Invoke the given method.  This method is a convenience method for
        invoking single argument methods.
    
        @param methodName The method name
        @param target The target Object
        @param targetClass The Class of the target object
        @param arg A single argument
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invoke(String methodName, Object target, 
    Class targetClass, Object arg) throws IllegalAccessException, 
    IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
    SecurityException
    {
        if(arg == null){
            return invoke(methodName, target, targetClass, EMPTY_OBJECT_ARRAY);
        } else {
            Object[] args = new Object[1];
            args[0] = arg;
            return invoke(methodName, target, targetClass, args);
        }
    }
    
    /** Invoke the given method.  This method is a convenience method for
        invoking single argument methods.  The argument's class is obtained
        from the argument object.
    
        @param methodName The method name
        @param target The target Object
        @param arg A single argument
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invokeDeclared(String methodName, Object target, 
    Object arg) throws IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException, NoSuchMethodException, SecurityException{
        if(arg == null){
            return invokeDeclared(methodName, target, EMPTY_OBJECT_ARRAY);
        } else {
            Object[] args = new Object[1];
            args[0] = arg;
            return invokeDeclared(methodName, target, args);
        }
    }
    
    /** Invoke the given method.  This method is a convenience method for
        invoking single argument methods.  The argument's class is obtained
        from the argument object.
    
        @param methodName The method name
        @param target The target Object
        @param args An array of arguments
        @return The return value
        @throws IllegalAccessException
        @throws IllegalArgumentException
        @throws InvocationTargetException
        @throws NoSuchMethodException
        @throws SecurityException
    */
    
    public static Object invokeDeclared(String methodName, Object target, 
    Object[] args) throws IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException, NoSuchMethodException, SecurityException{
        Class[] paramClasses = new Class[args.length];
        for(int i = 0; i < args.length; i++){
            paramClasses[i] = args[i].getClass();
        }
        Method method = getDeclaredMethod(target.getClass(), methodName, 
            paramClasses);
        return invoke(method, target, args);
    }
    
    /** Get the method with the given name and with a single argument of the
        type specified by paramClass.  The paramClass may be null in which case
        the method should have no arguments.
        
        @param targetClass The target class
        @param name The method name
        @param paramClass The single parameter class (may be null)
        @return The Method
        @throws SecurityException
        @throws NoSuchMethodException
    */
    
    public static Method getMethod(Class targetClass, String name, 
    Class paramClass) throws NoSuchMethodException, SecurityException{
        if(paramClass == null){
            return getMethod(targetClass, name, EMPTY_CLASS_ARRAY);
        } else {
            Class[] paramClasses = new Class[1];
            paramClasses[0] = paramClass;
            return getMethod(targetClass, name, paramClasses);
        }
    }
    
    /** Get the method with the given name and with arguments of the
        types specified by paramClasses.  If the method is not found
        then a method which accepts superclasses of the current arguments
        will be returned, if possible.
        
        @param targetClass The target class
        @param name The method name
        @param paramClasses An array of parameter classes
        @return The Method
        @throws SecurityException
        @throws NoSuchMethodException
    */
    
    public static Method getMethod(Class targetClass, String name, 
    Class[] paramClasses) throws NoSuchMethodException, SecurityException{
        Method method = null;
        try{
            method = targetClass.getMethod(name, paramClasses);
        } catch(NoSuchMethodException nsme){
            Method[] methods = targetClass.getMethods();
            
            OUTER: for(int i = 0; i < methods.length; i++){
                if (methods[i].getName().equalsIgnoreCase(name) && 
                    methods[i].getParameterTypes().length == paramClasses.length)
                {
                    Class[] params = methods[i].getParameterTypes();
                    for(int j = 0; j < params.length; j++) {
                        if (!params[j].isAssignableFrom(paramClasses[j])){
                            continue OUTER;
                        }
                    }
                    method = methods[i];
                    break;
                }
            }
            if (method == null) {
                throw nsme;
            }
        }
        return method;
    }
    
    /** Get the declared method with the given name and with a single argument 
        of the type specified by paramClass.  The paramClass may be null in 
        which case the method should have no arguments.
        
        @param targetClass The target class
        @param name The method name
        @param paramClass The single parameter class (may be null)
        @return The declared Method
        @throws SecurityException
        @throws NoSuchMethodException
    */
    
    public static Method getDeclaredMethod(Class targetClass, String name, 
    Class paramClass) throws NoSuchMethodException, SecurityException{
        if(paramClass == null){
            return getDeclaredMethod(targetClass, name, EMPTY_CLASS_ARRAY);
        } else {
            Class[] paramClasses = new Class[1];
            paramClasses[0] = paramClass;
            return getDeclaredMethod(targetClass, name, paramClasses);
        }
    }
    
    /** Get the declared method with the given name and with arguments of 
        the types specified by paramClasses.
        
        @param targetClass The target class
        @param name The method name
        @param paramClasses An array of parameter classes
        @return The declared Method
        @throws SecurityException
        @throws NoSuchMethodException
    */
    
    public static Method getDeclaredMethod(Class targetClass, String name, 
    Class[] paramClasses) throws NoSuchMethodException, SecurityException{
        return targetClass.getDeclaredMethod(name, paramClasses);
    }
    
}
