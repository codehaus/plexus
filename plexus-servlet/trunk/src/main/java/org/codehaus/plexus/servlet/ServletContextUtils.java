package org.codehaus.plexus.servlet;

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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.util.PropertyUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * <code>ServletContextUtils</code> provides methods to embed a Plexus
 * container within a Servlet context.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Id$
 */
public final class ServletContextUtils
{
    private ServletContext context;

    private PlexusContainer container;

    private boolean removeFromContext;

    private List components = new ArrayList();

    /**
     * Create a Plexus container using the {@link org.codehaus.plexus.DefaultPlexusContainer}. This method
     * should be called from an environment where a
     * <code>ServletContext</code> is available. It will create and initialize
     * the Plexus container and place references to the container into the context.
     *
     * @param context The servlet context to place the container in.
     * @return a Plexus container that has been initialized and started.
     * @throws PlexusContainerException If the Plexus container could not be started.
     */
    public void start( ServletContext context )
        throws PlexusContainerException
    {
        this.context = context;

        Object o = context.getAttribute( PlexusConstants.PLEXUS_KEY );

        if ( o == null )
        {
            context.log( "Initializing Plexus." );

            container = new DefaultPlexusContainer( null,
                                                    resolveContextProperties( context ),
                                                    resolveConfig( context ),
                                                    null );

            context.setAttribute( PlexusConstants.PLEXUS_KEY, container );

            removeFromContext = true;
        }
        else
        {
            if ( !( o instanceof PlexusContainer ) )
            {
                throw new PlexusContainerException( "An attribute with key '" + PlexusConstants.PLEXUS_KEY +
                    "' was in the context but it was not an instance of " + PlexusContainer.class.getName() + "." );
            }

            context.log( "Plexus container already in context." );

            container = (PlexusContainer) o;

            // TODO: make a child container. The child container has to load the WEB-INF/plexus.xml in the WAR.
        }

        lookupAddToContextComponents( context );

        context.log( "Plexus initialized." );
    }

    public void stop()
    {
        context.log( "Stopping Plexus." );

        if ( removeFromContext )
        {
            context.removeAttribute( PlexusConstants.PLEXUS_KEY );
        }

        releaseAddToContextComponents();

        container.dispose();

        context.log( "Plexus stopped." );
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private Properties resolveContextProperties( ServletContext context )
    {
        Properties properties = new Properties();

        String filename = context.getInitParameter( PlexusServletUtils.PLEXUS_PROPERTIES_PARAM );

        if ( filename == null )
        {
            filename = PlexusServletUtils.DEFAULT_PLEXUS_PROPERTIES;
        }

        context.log( "Loading plexus context properties from: '" + filename + "'" );

        try
        {
            URL url = context.getResource( filename );
            // bwalding: I think we'd be better off not using this exception swallower!
            properties = PropertyUtils.loadProperties( url );
        }
        catch ( Exception e )
        {
            // michal: I don't think it is that good idea to ignore this error.
            // bwalding: it's actually pretty difficult to get here as the PropertyUtils.loadProperties absorbs all Exceptions
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
        }

        if ( properties == null )
        {
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
            properties = new Properties();
        }

        if ( !properties.containsKey( PlexusServletUtils.PLEXUS_HOME ) )
        {
            setPlexusHome( context, properties );
        }

        return properties;
    }

    private URL resolveConfig( ServletContext context )
        throws PlexusContainerException
    {
        String plexusConf = context.getInitParameter( PlexusServletUtils.PLEXUS_CONFIG_PARAM );

        if ( plexusConf == null )
        {
            plexusConf = PlexusServletUtils.DEFAULT_PLEXUS_CONFIG;
        }

        try
        {
            URL resource = context.getResource( plexusConf );

            System.out.println( "resource = " + resource );

            return resource;
        }
        catch ( MalformedURLException e )
        {
            throw new PlexusContainerException( "Error while getting URL to '" + plexusConf + "'", e );
        }
    }

    /**
     * Set plexus.home context variable
     */
    private void setPlexusHome( ServletContext context, Properties contexProperties )
    {
        String realPath = context.getRealPath( "/WEB-INF" );

        if ( realPath != null )
        {
            File f = new File( realPath );

            contexProperties.setProperty( PlexusServletUtils.PLEXUS_HOME, f.getAbsolutePath() );
        }
        else
        {
            context.log( "Not setting 'plexus.home' as plexus is running inside webapp with no 'real path'." );
        }
    }

    private void lookupAddToContextComponents( ServletContext context )
        throws PlexusContainerException
    {
        String string = context.getInitParameter( PlexusServletUtils.PLEXUS_CONFIG_ADD_TO_CONTEXT );

        if ( string == null || string.length() == 0 )
        {
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer( string, "," );

        while( tokenizer.hasMoreTokens() )
        {
            String token = tokenizer.nextToken();

            int index = token.indexOf( ":" );

            Object component;

            if ( index > 0 )
            {
                String role = token.substring( 0, index );
                String roleHint = role.substring( index );

                try
                {
                    component = container.lookup( role, roleHint );
                }
                catch ( ComponentLookupException e )
                {
                    throw new PlexusContainerException( "Error while looking up component '" + role + ":" + roleHint + "'", e );
                }
            }
            else
            {
                try
                {
                    component = container.lookup( token );
                }
                catch ( ComponentLookupException e )
                {
                    throw new PlexusContainerException( "Error while looking up component '" + token + "'", e );
                }
            }

            context.setAttribute( token, component );
            components.add( component );
        }
    }

    private void releaseAddToContextComponents()
    {
        for ( Iterator it = components.iterator(); it.hasNext(); )
        {
            Object component = it.next();

            try
            {
                container.release( component );
            }
            catch ( ComponentLifecycleException e )
            {
                context.log( "Error while releasing " + component, e );
            }
        }
    }
}
