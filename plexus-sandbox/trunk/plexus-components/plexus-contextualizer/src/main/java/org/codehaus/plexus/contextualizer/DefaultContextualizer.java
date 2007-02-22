package org.codehaus.plexus.contextualizer;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

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
 * @since 22 feb. 07
 * @version $Id$
 */
public class DefaultContextualizer
    extends AbstractLogEnabled
    implements Contextualizer, Contextualizable, Initializable
{
    //---------------------------------------------
    // Configuration attributes
    //--------------------------------------------- 
    /**
     * values to put in Plexus Context
     * 
     * @plexus.configuration 
     */
    private Map contextValues;

    /**
     * add all Systemp properties in Plexus Context
     * 
     * @plexus.configuration default-value="false"
     */
    private boolean addAllSystemProperties;

    /**
     * defined system properties to add in Plexus Context
     * 
     * @plexus.configuration 
     */
    private List definedSystemProperties;

    private PlexusContainer plexusContainer;

    //---------------------------------------------
    // Plexus Lifecycle
    //--------------------------------------------- 
    public void contextualize( Context context )
        throws ContextException
    {
        this.plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

    }

    public void initialize()
        throws InitializationException
    {
        if ( this.addAllSystemProperties )
        {
            if ( this.getLogger().isDebugEnabled() )
            {
                this.getLogger().debug( "adding all system properties in context" );
            }
            Properties systemProperties = System.getProperties();
            Enumeration keys = systemProperties.keys();
            while ( keys.hasMoreElements() )
            {
                String key = (String) keys.nextElement();
                String value = systemProperties.getProperty( key );
                if ( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "add sysprops " + key + " : " + value );
                }
                this.plexusContainer.getContext().put( key, value );
            }
        }

        if ( this.contextValues != null )
        {
            for ( Iterator keys = this.contextValues.keySet().iterator(); keys.hasNext(); )
            {
                String key = (String) keys.next();
                String value = (String) this.contextValues.get( key );
                if ( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "add user value " + key + " : " + value );
                }
                this.plexusContainer.getContext().put( key, value );
            }
        }
        if ( this.definedSystemProperties != null )
        {
            for ( Iterator iterator = this.definedSystemProperties.iterator(); iterator.hasNext(); )
            {
                String key = (String) iterator.next();
                String value = System.getProperty( key );
                if ( this.getLogger().isDebugEnabled() )
                {
                    this.getLogger().debug( "add sysprops " + key + " : " + value );
                }
                this.plexusContainer.getContext().put( key, value );
            }
        }

    }

    public Map getContextValues()
    {
        return contextValues;
    }

    public void setContextValues( Map contextValues )
    {
        this.contextValues = contextValues;
    }

    public boolean isAddAllSystemProperties()
    {
        return addAllSystemProperties;
    }

    public void setAddAllSystemProperties( boolean addAllSystemProperties )
    {
        this.addAllSystemProperties = addAllSystemProperties;
    }

    public List getDefinedSystemProperties()
    {
        return definedSystemProperties;
    }

    public void setDefinedSystemProperties( List definedSystemProperties )
    {
        this.definedSystemProperties = definedSystemProperties;
    }

}
