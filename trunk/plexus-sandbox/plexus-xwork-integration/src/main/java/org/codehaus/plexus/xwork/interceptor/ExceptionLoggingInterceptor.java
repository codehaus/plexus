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

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.ExceptionHolder;
import com.opensymphony.xwork.interceptor.ExceptionMappingInterceptor;

/**
 * Exception interceptor that will also log the exception.
 * 
 * @author <a href="mailto:nramirez@exist.com">Napoleon Esmundo C. Ramirez</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor" role-hint="exceptionLogging"
 */
public class ExceptionLoggingInterceptor
    extends ExceptionMappingInterceptor
    implements LogEnabled
{
    private Logger logger;

    private static final long serialVersionUID = -1776743136472264546L;

    protected void publishException( ActionInvocation invocation, ExceptionHolder exceptionHolder )
    {
        Throwable e = exceptionHolder.getException();
        logger.info( "Error ocurred during execution", e );

        super.publishException( invocation, exceptionHolder );
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }
}
