package org.codehaus.plexus.security.ui.web.filter;

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

import com.opensymphony.xwork.ActionContext;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * PlexusServletFilter 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class PlexusServletFilter
    implements Filter
{
    private PlexusContainer container;

    private Logger logger;

    public void destroy()
    {
        // Do nothing here.
    }

    protected PlexusContainer getContainer()
    {
        return container;
    }

    public Object lookup( String role )
        throws ServletException
    {
        try
        {
            return getContainer().lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup plexus component '" + role + "'.", e );
        }
    }

    public Object lookup( String role, String hint )
        throws ServletException
    {
        try
        {
            return getContainer().lookup( role, hint );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup plexus component '" + role + "'.", e );
        }
    }

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        ActionContext context = ActionContext.getContext();

        container = (PlexusContainer) context.getApplication().get( PlexusLifecycleListener.KEY );

        try
        {
            LoggerManager loggerManager = (LoggerManager) container.lookup( LoggerManager.ROLE );
            logger = loggerManager.getLoggerForComponent( this.getClass().getName() );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "Unable to lookup Logger from plexus.", e );
        }
    }

    public Logger getLogger()
    {
        return logger;
    }
}
