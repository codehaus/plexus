package org.codehaus.plexus.summit.pull;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * This is a default implementation of the pull service.  It takes avalon
 * components and sticks them in the context.  It supports global and request
 * tools fully.  Session tools are untested and may need further work.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @todo create a dynamic proxy around session tools like in the Ivory so
 * they are released properly
 * @since Feb 12, 2003
 */
public class DefaultPullService
    extends AbstractLogEnabled
    implements PullService, Contextualizable, Initializable, Disposable
{
    public static final String GLOBAL_SCOPE = "global";

    public static final String REQUEST_SCOPE = "request";

    public static final String SESSION_SCOPE = "session";

    private Map globalTools;

    private Map sessionTools;

    private Map requestTools;

    private PlexusContainer container;

    private List tools;

    public DefaultPullService()
    {
        globalTools = new HashMap();
        sessionTools = new HashMap();
        requestTools = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws InitializationException
    {
        for ( Iterator i = tools.iterator(); i.hasNext(); )
        {
            Tool tool = (Tool) i.next();

            if ( tool.getScope().equals( GLOBAL_SCOPE ) )
            {
                Object o;

                try
                {

                if ( tool.getRoleHint() == null )
                {
                    o = container.lookup( tool.getRole() );
                }
                else
                {
                    o = container.lookup( tool.getRole(), tool.getRoleHint() );
                }

                }
                catch ( ComponentLookupException e)
                {
                    throw new InitializationException( "Error looking up global tool: ", e );
                }

                globalTools.put( tool.getName(), o );
            }
            else if ( tool.getScope().equals( REQUEST_SCOPE ) )
            {
                requestTools.put( tool.getName(), tool.getRole() );

            }
            else if ( tool.getScope().equals( SESSION_SCOPE ) )
            {
                sessionTools.put( tool.getName(), tool.getRole() );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void populateContext( ViewContext context, RunData data )
    {
        populateWithGlobalTools( context );

        populateWithRequestTools( context, data );

        populateWithSessionTools( context, data );
    }

    protected void populateWithGlobalTools( ViewContext context )
    {
        context.putAll( globalTools );
    }

    protected void populateWithRequestTools( ViewContext context, RunData data )
    {
        Iterator itr = requestTools.keySet().iterator();

        while ( itr.hasNext() )
        {
            String key = (String) itr.next();

            try
            {
                Object component = container.lookup( (String) requestTools.get( key ) );

                setRequestRunData( component, data );

                context.put( key, component );
            }
            catch ( Exception e )
            {
                getLogger().error( "Couldn't find request tool " + key + " with role " + requestTools.get( key ) + ".", e );
            }
        }
    }

    /**
     * An attempt to populate the session with tools.  This is largely untested
     * and it is not clear yet how we are to know when to release() the
     * components after the session expires.
     *
     * @param context
     * @param data
     */
    protected void populateWithSessionTools( ViewContext context, RunData data )
    {
        Iterator itr = sessionTools.keySet().iterator();

        while ( itr.hasNext() )
        {
            String key = (String) itr.next();
            // ensure that tool is created only once for a user
            // by synchronizing against the session object
            HttpSession session = data.getSession();
            synchronized ( session )
            {
                // first try and fetch the tool from the session
                Object tool = session.getAttribute( key );
                if ( tool == null )
                {
                    try
                    {
                        tool = container.lookup( (String) sessionTools.get( key ) );
                        // Session tools should have their own interface.
                        //setRequestRunData( tool, data );

                        // Store the tool in the session
                        session.setAttribute( key, tool );
                    }
                    catch ( Exception e )
                    {
                        getLogger().error( "Could find request tool " + key + " with role " + sessionTools.get( key ) + ".", e );

                        break;
                    }
                }

                // put the tool in the context
                context.put( key, tool );

//                getLogger().debug( "Addded tool $" + key + " to the context." );
            }
        }
    }

    /**
     * Set the appropriate runtime data for components that extend
     * RequestTool.
     *
     * @param component
     */
    protected void setRequestRunData( Object component, RunData data )
    {
        if ( component instanceof RequestTool )
        {
            ( (RequestTool) component ).setRunData( data );
        }
    }

    public void releaseTools( ViewContext context )
    {
        try
        {
            releaseTools( context, requestTools );
        }
        catch ( Exception e )
        {
            getLogger().error( "Problem releasing tools: ", e );
        }
    }

    protected void releaseTools( ViewContext context, Map tools )
        throws Exception
    {
        Iterator itr = tools.keySet().iterator();

        while ( itr.hasNext() )
        {
            String key = (String) itr.next();

            Object tool = context.remove( key );

            container.release( tool );
        }
    }

    public void dispose()
    {
        try
        {
            disposeGlobalTools();
        }
        catch ( Exception e )
        {
            getLogger().error( "Problem disposing global tools: ", e );
        }

        this.container = null;
    }

    /**
     * Method releaseGlobalTools.
     */
    private void disposeGlobalTools()
        throws Exception
    {
        Iterator itr = globalTools.keySet().iterator();

        List listToRemove = new ArrayList();

        while ( itr.hasNext() )
        {
            String key = (String) itr.next();

            listToRemove.add( key );
        }

        itr = listToRemove.iterator();

        while ( itr.hasNext() )
        {
            String key = (String) itr.next();
            // release AND remove the tool
            container.release( globalTools.remove( key ) );
        }
    }
}
