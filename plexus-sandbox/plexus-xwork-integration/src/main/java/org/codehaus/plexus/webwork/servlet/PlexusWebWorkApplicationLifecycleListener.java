package org.codehaus.plexus.webwork.servlet;

/**
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.opensymphony.xwork.interceptor.component.DefaultComponentManager;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ObjectFactory;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusWebWorkApplicationLifecycleListener
    implements ServletContextListener
{
    public void contextInitialized( ServletContextEvent servletContextEvent )
    {
        // Do init

        ServletContext context = servletContextEvent.getServletContext();

        PlexusContainer container = (PlexusContainer) context.getAttribute( PlexusConstants.PLEXUS_KEY );

        if ( container == null )
        {
            // The container initializer will already have thrown an exception
            // so no need to bitch&whine here

            return;
        }

        ObjectFactory objectFactory;

        try
        {
            objectFactory = (ObjectFactory) container.lookup( ObjectFactory.class.getName() );
        }
        catch ( ComponentLookupException e )
        {
            throw new RuntimeException( "Could not look up the ObjectFactory from the container.", e );
        }

        ObjectFactory.setObjectFactory( objectFactory );
    }

    public void contextDestroyed( ServletContextEvent servletContextEvent )
    {
        // Do shutdown
    }
}
