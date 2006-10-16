/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package org.codehaus.plexus.xwork.interceptor;

/*
 * Copyright 2006 The Codehaus.
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
 */

import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork.interceptor.ExceptionHolder;

/**
 * <p>
 * Exception interceptor that allows ignoring exceptions. Set the result to <code>ignore</code>
 * in your <code>xwork.xml</code> file to ignore an exception when you are catching a more general
 * one.
 * </p>
 * 
 * <p>
 * <code>
 *   &lt;exception-mapping exception="MyException" result="ignore"/>
 * </code>
 * </p>
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor" role-hint="exceptionInterceptor"
 */
public class ExceptionMappingInterceptor
    extends com.opensymphony.xwork.interceptor.ExceptionMappingInterceptor
{
    public static final String IGNORE = "ignore";

    private static final long serialVersionUID = 235443833909815141L;

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        String result;

        try
        {
            result = invocation.invoke();
        }
        catch ( Exception e )
        {
            List exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();
            String mappedResult = this.findResultFromExceptions( exceptionMappings, e );
            if ( mappedResult != null )
            {
                result = mappedResult;
                if ( result.equals( IGNORE ) )
                {
                    throw e;
                }
                publishException( invocation, new ExceptionHolder( e ) );
            }
            else
            {
                throw e;
            }
        }

        return result;
    }

    private String findResultFromExceptions( List exceptionMappings, Throwable t )
    {
        String result = null;

        // Check for specific exception mappings.
        if ( exceptionMappings != null )
        {
            int deepest = Integer.MAX_VALUE;
            for ( Iterator iter = exceptionMappings.iterator(); iter.hasNext(); )
            {
                ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) iter.next();
                int depth = getDepth( exceptionMappingConfig.getExceptionClassName(), t );
                if ( depth >= 0 && depth < deepest )
                {
                    deepest = depth;
                    result = exceptionMappingConfig.getResult();
                }
            }
        }

        return result;
    }

}
