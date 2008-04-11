package org.codehaus.plexus.appserver.manager.web.action;

import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.ApplicationServerConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/*
 * Copyright 2007 The Codehaus Foundation.
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
/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 5 mars 07
 * @version $Id$
 */
public abstract class AbstractManagerAction
    extends PlexusActionSupport
    implements Initializable
{
    private ApplicationServer applicationServer;

    public void contextualize( Context context )
        throws ContextException
    {

        super.contextualize( context );
        try
        {
            this.applicationServer = (ApplicationServer) context
                .get( ApplicationServerConstants.APP_SERVER_CONTEXT_KEY );
            getLogger().debug( "ApplicationServer found in context" );
        }
        catch ( Exception e )
        {
            getLogger().error( e.getMessage(), e );
            this.logContextKeys( context );
            throw new ContextException( e.getMessage(), e );
        }
    }

    private void logContextKeys( Context context )
    {
        for ( Iterator keys = context.getContextData().keySet().iterator(); keys.hasNext(); )
        {
            String key = (String) keys.next();
            getLogger().debug(
                               "key " + key + ", object class"
                                   + context.getContextData().get( key ).getClass().getName() );
        }
    }

    public void initialize()
        throws InitializationException
    {
        /*
         try
         {
         MBeanServer server = MBeanServerFactory.createMBeanServer();

         this.applicationServerMBeanMBean = (ApplicationServerMBeanMBean) server
         .instantiate( MBean.class.getName(), new ObjectName( "PlexusAppServer:name=ApplicationServer" ) );
         }
         catch ( InstanceNotFoundException e )
         {
         getLogger().error( e.getMessage(), e );
         throw new InitializationException( e.getMessage(), e );
         }
         catch ( MBeanException e )
         {
         getLogger().error( e.getMessage(), e );
         throw new InitializationException( e.getMessage(), e );
         }
         catch ( ReflectionException e )
         {
         getLogger().error( e.getMessage(), e );
         throw new InitializationException( e.getMessage(), e );
         }
         catch ( MalformedObjectNameException e )
         {
         getLogger().error( e.getMessage(), e );
         throw new InitializationException( e.getMessage(), e );
         }
         */
    }

    public List getAppRuntimeProfiles()
    {
        return this.applicationServer.getAppRuntimeProfiles();
    }

    public ApplicationServer getApplicationServer()
    {
        return this.applicationServer;
    }
    /*
     public ApplicationServerMBeanMBean getApplicationServerMBeanMBean()
     throws Exception
     {
     return this.applicationServerMBeanMBean;
     }
     */
}
