package org.codehaus.plexus.summit.activity;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.summit.exception.SummitRuntimeException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * <p>
 * This service allows you to execute methods on action classes.  Action classes
 * are just classes who follow the method naming conventions described below.
 * They do not need to inherit from any base class.</p>
 * <p>
 * Actions are looked for based on the "action" parameter in the
 * RequestParameters.  The packages specified in the configuration are then
 * searched for this action.  For example, if the action is "Order.Submit" and
 * we are to look in package "org.codehaus.plexus.summit.modules," the service
 * attempts to load "org.codehaus.plexus.summit.modules.Order.Submit" and execute
 * a method on.</p>
 * <p>
 * The method is specified with the submit button name.  The name must be
 * prefixed with "eventSubmit_".  For example, if the submit button is named
 * "eventSubmit_doMyaction" then this service tries to execute the method
 * "doMyaction" on the specified action.</p> 
 * 
 * <p>Limitations:</p>
 *
 * <p>
 * Because ParameterParser makes all the key values lowercase, we have
 * to do some work to format the string into a method name. For
 * example, a button name eventSubmit_doDelete gets converted into
 * eventsubmit_dodelete. Thus, we need to form some sort of naming
 * convention so that dodelete can be turned into doDelete.</p>
 * <p>
 *
 * Thus, the convention is this:
 *
 * <ul>
 * <li>The variable name MUST have the prefix "eventSubmit_".</li>
 * <li>The variable name after the prefix MUST begin with the letters
 * "do".</li>
 * <li>The first letter after the "do" will be capitalized and the
 * rest will be lowercase</li>
 * </ul>
 *
 * If you follow these conventions, then you should be ok with your
 * method naming in your Action class.</p>
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @since Mar 1, 2003
 */
public class DefaultActionEventService
    extends AbstractLogEnabled
    implements ActionEventService, Configurable, ThreadSafe
{
    /** Whether or not the method name's case is folded. */
    private boolean foldCase;

    /** The configuration key determining the case folding. */
    protected static final String FOLD_CASE_KEY = "fold-method-case";
    
    /** The default case folding value. */
    protected static final boolean FOLD_CASE_DEFAULT = true;
    
    /** The default method to execute */
    protected static final String DEFAULT_METHOD = "doPerform";

    /** The name of the button to look for. */
    protected static final String BUTTON = "eventSubmit_";
    
    /** The length of the button to look for. */
    protected static final int BUTTON_LENGTH = BUTTON.length();
    
    /** The prefix of the method name. */
    protected static final String METHOD_NAME_PREFIX = "do";
    
    /** The length of the method name. */
    protected static final int METHOD_NAME_LENGTH = METHOD_NAME_PREFIX.length();
        
    List actionPackages;
    
    public DefaultActionEventService()
    {
        actionPackages = new ArrayList();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        foldCase = config.getAttributeAsBoolean( FOLD_CASE_KEY,
                                                 FOLD_CASE_DEFAULT );
                                                 
        Configuration[] packages = config.getChild( "actionPackages" )
                                         .getChildren( "actionPackage" );
        
        // If the user does not define any modules, use the default package.
        if ( packages == null || packages.length == 0 )
        {
            actionPackages.add( "" );
        }
        else
        {
            for ( int i = 0; i < packages.length; i++ )
            {
                getLogger().info( "Adding package " + packages[i].getValue() );
                actionPackages.add( packages[i].getValue() );
            }
        }
    }
        
    /**
     * @see org.codehaus.plexus.summit.activity.ActionEventService#perform(org.codehaus.plexus.summit.rundata.RunData)
     */
    public void perform(RunData data) throws Exception
    {
        String action = data.getParameters().getString("action");
        
        if ( action != null )
        {
            String methodName = null;
            try
            {
                methodName = getMethodName( data, DEFAULT_METHOD );

                Class actionClass = getClass( action );

                Method method = getMethod( actionClass, data.getClass(), methodName, DEFAULT_METHOD );
                
                // The arguments to pass to the method to execute.
                Object[] args = new Object[1];
                args[0] = data;
                
                method.invoke( actionClass.newInstance(), args );
            }
            catch ( ClassNotFoundException e )
            {
                getLogger().debug( "Could not find the action.", e );
            } 
            catch (InvocationTargetException ite)
            {
                // I have not seen this exception, in stacktraces generated
                // while doing my own testing on jdk1.3.1 and earlier.  But
                // see it increasingly from stacktraces reported by others.
                // Its printStackTrace method should do The Right Thing, but
                // I suspect some implementation is not.
                // Unwrap it here, so that the original cause does not get lost.
                Throwable t = ite.getTargetException();
                if (t instanceof Exception) 
                {
                    throw (Exception)t;
                }
                else if (t instanceof java.lang.Error) 
                {
                    throw (java.lang.Error)t;
                }
                else 
                {
                    // this should not happen, but something could throw
                    // an instance of Throwable
                    throw new SummitRuntimeException("",t);
                }
            }
        }
    }
    
    /**
     * Find the method to execute for the action.
     * 
     * @param methodName The name of the method.
     * @param actionClass The class which the method is on.
     * @return Method
     * @throws NoSuchMethodException
     */
    protected Method getMethod( Class actionClass,
                                Class rundataClass,
                                String methodName, 
                                String defaultName )
        throws NoSuchMethodException, ClassNotFoundException
    {
        // The arguments to the method to find.
        Class[] classes = new Class[1];
        classes[0] = rundataClass;

        try
        {
            return actionClass.getMethod(methodName, classes);
        }
        catch ( NoSuchMethodException e )
        {
            return actionClass.getMethod(defaultName, classes);
        }
    }
    
    /**
     * Find the appropriate method name to execute.
     * 
     * @param data
     * @return String
     */
    protected String getMethodName( RunData data, String defaultName )
    {
        for ( Iterator iter = data.getParameters().keys(); iter.hasNext(); )
        {
            String key = (String) iter.next();
            if ( key.startsWith( BUTTON ) )
            {
                return formatString(key);
            }
        }
        
        return defaultName;
    }

    /**
     * This method does the conversion of the lowercase method name
     * into the proper case.
     *
     * @param input The unconverted method name.
     * @return A string with the method name in the proper case.
     */
    protected final String formatString(String input)
    {
        String methodName = input;

        if ( foldCase )
        {
            methodName = 
                methodName.substring(BUTTON_LENGTH + METHOD_NAME_LENGTH);

            return (METHOD_NAME_PREFIX + firstLetterCaps(methodName));
        }
        else
        {
            return methodName.substring(BUTTON_LENGTH);
        }
    }

    /**
     * Finds the class correspond to the action.
     * 
     * @param action
     * @return Class
     */
    protected Class getClass(String action) throws ClassNotFoundException
    {
        for ( Iterator iter = actionPackages.iterator(); iter.hasNext(); )
        {
            // Try to load the action from the possible action packages.
            String actionPackage = (String) iter.next();
            try
            {
                Class actionClass = 
                    getClass().getClassLoader().loadClass(actionPackage + 
                                                          "." + action );
                return actionClass;
            }
            catch (ClassNotFoundException e)
            {
                // Continue on, it could be in another package.
            }
        }
        throw new ClassNotFoundException( "Could not find a class for action " +
                                        action + "." );
    }
    

    /**
     * Makes the first letter caps and the rest lowercase.
     *
     * @param data The input string.
     * @return A string with the described case.
     */
    protected final String firstLetterCaps( String data )
    {
        String firstLetter = data.substring(0, 1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }
}
