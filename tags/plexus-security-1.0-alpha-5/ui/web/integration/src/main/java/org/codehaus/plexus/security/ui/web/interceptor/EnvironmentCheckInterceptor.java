package org.codehaus.plexus.security.ui.web.interceptor;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.security.system.check.EnvironmentCheck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EnvironmentCheckInterceptor 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 *                   role-hint="pssEnvironmentCheckInterceptor"
 */
public class EnvironmentCheckInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{
    private static boolean checked = false;
    
    /**
     * We track our own logger, because we test for Plexus too.
     */
    private Logger logger;
    
    /**
     * @plexus.requirement role="org.codehaus.plexus.security.system.check.EnvironmentCheck"
     */
    private List checkers;

    public void destroy()
    {
        // no-op
    }

    public void init()
    {
        if( EnvironmentCheckInterceptor.checked )
        {
            // No need to check twice.
            return;
        }
        
        if( checkers != null )
        {
            List violations = new ArrayList();
            
            Iterator it = checkers.iterator();
            while(it.hasNext())
            {
                EnvironmentCheck check = (EnvironmentCheck) it.next();
                
                check.validateEnvironment( violations );
            }
            
            if (!violations.isEmpty())
            {
                StringBuffer msg = new StringBuffer();
                msg.append( "EnvironmentCheck Failure.\n" );
                msg.append( "======================================================================\n" );
                msg.append( " ENVIRONMENT FAILURE !! \n" );
                msg.append( "\n" );

                Iterator vit = violations.iterator();
                while(vit.hasNext())
                {
                    msg.append( vit.next() ).append("\n");
                }
                
                msg.append( "\n" );
                msg.append( "======================================================================" );
                getLogger().fatalError( msg.toString() );
            }
        }
        
        EnvironmentCheckInterceptor.checked = true;
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            // Try to use parent logger first.
            logger = super.getLogger();
            if ( logger == null )
            {
                // whoops no parent logger.
                logger = new ConsoleLogger( Logger.LEVEL_DEBUG, EnvironmentCheck.class.getName() );
            }
        }
        return logger;
    }

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        // A no-op here. Work for this intereceptor is done in init().
        return invocation.invoke();
    }
}
