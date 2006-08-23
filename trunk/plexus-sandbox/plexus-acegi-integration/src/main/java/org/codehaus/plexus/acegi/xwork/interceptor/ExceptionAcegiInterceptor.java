package org.codehaus.plexus.acegi.xwork.interceptor;

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

import org.acegisecurity.AcegiSecurityException;
import org.acegisecurity.ui.ExceptionTranslationFilter;
import org.codehaus.plexus.xwork.interceptor.ExceptionLoggingInterceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.ExceptionHolder;

/**
 * Exception interceptor that will rethrow {@link AcegiSecurityException} exceptions
 * so the {@link ExceptionTranslationFilter} can handle them.
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor" role-hint="exceptionAcegi"
 */
public class ExceptionAcegiInterceptor
    extends ExceptionLoggingInterceptor
{

    public static final String THROW = "throw";

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        try
        {
            return super.intercept( invocation );
        }
        catch ( RethrowableException e )
        {
            throw (Exception) e.getCause();
        }
    }

    protected void publishException( ActionInvocation invocation, ExceptionHolder exceptionHolder )
    {
        Throwable e = exceptionHolder.getException();
        if ( e instanceof AcegiSecurityException )
        {
            throw new RethrowableException( (AcegiSecurityException) e );
        }
    }

    private class RethrowableException
        extends RuntimeException
    {
        public RethrowableException( Exception t )
        {
            super( t );
        }
    }
}
