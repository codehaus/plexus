package org.codehaus.plexus.xwork.interceptor;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

import com.opensymphony.webwork.plexus.PlexusLifecycleListener;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * release PlexusActionSupport which are all per-lookup
 * Note the followig test before release PlexusActionSupport.class.isAssignableFrom( action.getClass() )
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 7 feb. 07
 * @version $Id$
 * @plexus.component 
 *   role="com.opensymphony.xwork.interceptor.Interceptor"
 *   role-hint="plexusReleaseComponentInterceptor"
 * 
 */
public class PlexusReleaseComponentInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{

    /** 
     * @see com.opensymphony.xwork.interceptor.Interceptor#destroy()
     */
    public void destroy()
    {
        // nothing

    }

    /** 
     * @see com.opensymphony.xwork.interceptor.Interceptor#init()
     */
    public void init()
    {
        // nothing

    }

    /** 
     * @see com.opensymphony.xwork.interceptor.Interceptor#intercept(com.opensymphony.xwork.ActionInvocation)
     */
    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {
        try
        {
            return actionInvocation.invoke();
        }
        catch ( Exception e )
        {
            // I think Exception is enough ? Throwable ?
            throw e;
        }
        finally
        {
            // release the action
            this.releaseActionQuietly( actionInvocation );
        }
    }

    /**
     * @param actionInvocation
     */
    private void releaseActionQuietly( ActionInvocation actionInvocation )
    {
        try
        {
            Object action = actionInvocation.getAction();
            // we do it only for PlexusActionSupport ?
            // olamy : I think yes because if not filtering release will failed with a warning

            if ( PlexusActionSupport.class.isAssignableFrom( action.getClass() ) )
            {
                PlexusContainer plexusContainer = (PlexusContainer) actionInvocation.getInvocationContext()
                    .getApplication().get( PlexusLifecycleListener.KEY );
                plexusContainer.release( action );
                if ( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "release " + action.getClass().getName() );
                }
            }
            else
            {
                if ( this.getLogger().isDebugEnabled() )
                {
                    StringBuffer debugMessage = new StringBuffer( "action not released not a PlexusActionSupport : " );
                    debugMessage.append( action.getClass().getName() );
                    this.getLogger().debug( debugMessage.toString() );
                }
            }
        }
        catch ( ComponentLifecycleException e )
        {
            // TODO sure of this quietly exception ?
            this.getLogger().warn( e.getMessage(), e );
        }
    }

}
