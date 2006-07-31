package org.codehaus.plexus.acegi.intercept.method.aspectj;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;

/**
 * <p>Acegi interceptor for any AspectJ pointcut.</p>
 * 
 * <p>When securityInterceptor is set method calls are processed before proceeding,
 * checking the authorization of the user to invoke the method. When not set authorization
 * is not checked.</p>
 *
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id: ContinuumUserDetailsService.java 421005 2006-07-11 22:00:29Z carlos $
 */
public abstract aspect SecurityAspect
{

    private AspectJSecurityInterceptor securityInterceptor;

    /**
     * <p>Pointcut that will be secured with Acegi</p> 
     * 
     * <p>For example to secure all public methods of the Continuum class:
     * <pre>
     *     pointcut securedExecution():
     *       target(Continuum) &&
     *       execution(public * *(..)) &&
     *       !within(MethodSecurityAspect);
     * </pre>
     * </p>
     */
    protected abstract pointcut securedExecution();

    Object around(): securedExecution() {
        
        if ( getSecurityInterceptor() != null )
        {
            AspectJCallback callback = new AspectJCallback()
            {
                public Object proceedWithObject()
                {
                    return proceed();
                }
            };
            return getSecurityInterceptor().invoke( thisJoinPoint, callback );
        }
        else
        {
            return proceed();
        }
    }

    /**
     * The {@link AspectJSecurityInterceptor} that will process the method calls
     */
    public AspectJSecurityInterceptor getSecurityInterceptor()
    {
        return securityInterceptor;
    }

    public void setSecurityInterceptor( AspectJSecurityInterceptor securityInterceptor )
    {
        this.securityInterceptor = securityInterceptor;
    }
}
