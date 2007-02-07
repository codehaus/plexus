/**
 * Copyright 2006 Aldrin Leal, aldrin at leal dot eng dot bee ar
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codehaus.plexus.discovery.mdns;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.codehaus.plexus.discovery.DiscoverableResource;
import org.codehaus.plexus.discovery.ResourceDiscoverer;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.util.CollectionUtils;

/**
 * A MulticastDNS Service Discoverer
 *
 * @author Aldrin Leal
 * @plexus.component role="org.codehaus.plexus.discovery.ResourceDiscoverer"
 * lifecycle-handler="basic"
 */
public class MDNSResourceDiscoverer
    implements ResourceDiscoverer, ServiceListener, LogEnabled, Startable
{
    private static final int TIMEOUT_DEFAULT = 5000;

    /** Resource Types List */
    private Set resourceTypes;

    /** Logger */
    private Logger logger;

    /**
     * Instância jMDNS
     *
     * @plexus.requirement role-hint="org.codehaus.plexus.discovery.mdns.JmDNSWrapper"
     */
    private JmDNSWrapper jmDNSWrapper;

    /** Component Status */
    private boolean started;

    /** Found Services */
    private Map foundServices;

    /** Timeout */
    private int timeout;

    /** Constructor */
    public MDNSResourceDiscoverer()
    {
        reset();
    }

    /** Component Reset to Defaults */
    private void reset()
    {
        if ( null != this.resourceTypes )
        {
            for ( Iterator iterServices = new ArrayList( resourceTypes )
                .iterator(); iterServices.hasNext(); )
            {
                String curServiceType = (String) iterServices.next();

                jmDNSWrapper.getJmdns().removeServiceListener( curServiceType, this );
            }
        }

        this.resourceTypes = new TreeSet();
        this.started = false;
        this.foundServices = new TreeMap();
        this.timeout = TIMEOUT_DEFAULT;
    }

    /**
     * jmDNSWrapper Getter
     *
     * @return the jmDNSWrapper
     */
    public JmDNSWrapper getJmDNSWrapper()
    {
        return jmDNSWrapper;
    }

    /**
     * jmDNSWrapper Setter
     *
     * @param jmDNSWrapper the jmDNSWrapper to set
     */
    public void setJmDNSWrapper( JmDNSWrapper jmDNSWrapper )
    {
        this.jmDNSWrapper = jmDNSWrapper;
    }

    /**
     * Returns the ID of this Service
     *
     * @see org.codehaus.plexus.discovery.ServiceDiscoverer#getId()
     */
    public String getId()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.discovery.ServiceDiscoverer#getName()
     */
    public String getName()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.plexus.discovery.ServiceDiscoverer#getUrl()
     */
    public URL getURL()
    {
        return null;
    }

    /** {@inheritDoc} */
    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    /**
     * Gets the Resource Types
     *
     * @return the resourceTypes
     */
    public Set getResourceTypes()
    {
        return resourceTypes;
    }

    /**
     * Timeout (ms) Getter
     *
     * @return the timeout (in ms)
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Timeout (ms) Setter
     *
     * @param timeout the timeout to set (in ms)
     */
    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }

    /**
     * Sets the Resource Types
     *
     * @param resourceTypes the resourceTypes to set
     */
    public void setResourceTypes( Set resourceTypes )
    {
        if ( !started )
        {
            this.resourceTypes.clear();
            this.resourceTypes.addAll( resourceTypes );
        }
        else
        {
            removeServices( CollectionUtils.subtract( this.resourceTypes, resourceTypes ) );

            addServices( CollectionUtils.subtract( resourceTypes, this.resourceTypes ) );
        }
    }

    /**
     * Adds a bunch of Resource Service Types
     *
     * @param resourceTypesToInclude Collection of Resource Types to Include
     */
    private void addServices( Collection resourceTypesToInclude )
    {
        for ( Iterator iterInclude = resourceTypesToInclude.iterator(); iterInclude
            .hasNext(); )
        {
            String resourceTypeToInclude = (String) iterInclude.next();

            this.jmDNSWrapper.getJmdns().registerServiceType( resourceTypeToInclude );
            this.jmDNSWrapper.getJmdns().addServiceListener( resourceTypeToInclude, this );
        }
    }

    /**
     * Removes a bunch of Resource Service Types
     *
     * @param resourceTypesToExclude Collection of Resource Types to Exclude
     */
    private void removeServices( Collection resourceTypesToExclude )
    {
        for ( Iterator iterExclude = resourceTypesToExclude.iterator(); iterExclude
            .hasNext(); )
        {
            String resourceTypeToExclude = (String) iterExclude.next();

            this.jmDNSWrapper.getJmdns().removeServiceListener( resourceTypeToExclude, this );
        }
    }

    /**
     * Boolean Predicate for Execution Status
     *
     * @return true if resource discoverer is alive and kicking, false otherwise
     */
    public boolean isStarted()
    {
        return started;
    }

    /**
     * Starts the Service
     *
     * @throws StartingException Someone has set us up the bomb
     */
    public void start()
        throws StartingException
    {
        try
        {
            /**
             * Silently ignore shmuck calls...
             */
            if ( this.started )
            {
                return;
            }

            if ( this.logger.isInfoEnabled() )
            {
                this.logger.info( "Firing up." );
            }

            for ( Iterator it = this.resourceTypes.iterator(); it.hasNext(); )
            {
                String resourceType = (String) it.next();

                if ( this.logger.isInfoEnabled() )
                {
                    this.logger.info( "Adding Resource Discoverer for: " + resourceType );
                }

                this.jmDNSWrapper.getJmdns().registerServiceType( resourceType );
                this.jmDNSWrapper.getJmdns().addServiceListener( resourceType, this );
            }

            if ( this.logger.isInfoEnabled() )
            {
                this.logger.info( "Ready." );
            }

            this.started = true;
        }
        catch ( Exception e )
        {
            throw new StartingException( "start()", e );
        }
    }

    /**
     * Deactivates the service
     *
     * @throws StoppingException Someone has set us up the bomb
     */
    public void stop()
        throws StoppingException
    {
        try
        {
            if ( this.logger.isInfoEnabled() )
            {
                this.logger.info( "Shutting down." );
            }
            if ( started )
            {
                reset();
            }
            if ( this.logger.isInfoEnabled() )
            {
                this.logger.info( "Stop! Hammertime..." );
            }
        }
        catch ( RuntimeException e )
        {
            throw new StoppingException( "stop()", e );
        }
    }

    /** Iterates on the found resources */
    public Iterator findResources()
    {
        return new ArrayList( this.foundServices.values() ).iterator();
    }

    /** Fired whenever a service is added (i.e., ask for resolution) */
    public void serviceAdded( ServiceEvent serviceEvent )
    {
        if ( !this.foundServices.containsKey( key( serviceEvent ) ) )
        {
            synchronized ( this )
            {
                serviceEvent.getDNS().requestServiceInfo( serviceEvent.getType(), serviceEvent.getName(),
                                                          this.timeout );
            }
        }
    }

    /**
     * Fired whenever a service is removed (i.e., removes from the found
     * services)
     */
    public void serviceRemoved( ServiceEvent serviceEvent )
    {
        synchronized ( this )
        {
            foundServices.remove( key( serviceEvent ) );
        }
    }

    /** Called whenever a service is resolved (i.e., adds to the found services) */
    public void serviceResolved( ServiceEvent serviceEvent )
    {
        synchronized ( this )
        {
            if ( !foundServices.containsKey( key( serviceEvent ) ) )
            {
                try
                {
                    DiscoverableResource mddr = new DiscoverableResource();

                    mddr.setType( serviceEvent.getType() );
                    mddr.setName( serviceEvent.getName() );
                    mddr.setResourceDiscoverer( this );
                    mddr.setUrl( new URL( serviceEvent.getInfo().getURL() ) );

                    foundServices.put( key( serviceEvent ), mddr );
                }
                catch ( MalformedURLException e )
                {
                    if ( logger.isWarnEnabled() )
                    {
                        logger.warn( "serviceResolved(serviceEvent=" + serviceEvent + ")", e );
                    }
                }
            }
        }
    }

    /**
     * Gets a key for a given serviceEvent
     *
     * @param serviceEvent
     * @return name + type
     */
    private String key( ServiceEvent serviceEvent )
    {
        return serviceEvent.getName() + serviceEvent.getType();
	}
}
