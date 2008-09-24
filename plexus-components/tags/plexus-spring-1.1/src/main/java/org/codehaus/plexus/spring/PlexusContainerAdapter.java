package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * An adapter to access Spring ApplicationContext from a plexus component
 *
 * @author <a href="mailto:nicolas@apache.org">Nicolas De Loof</a>
 * @version $Id$
 */
public class PlexusContainerAdapter
    implements PlexusContainer, ApplicationContextAware, ServiceLocator
{
    
    private org.slf4j.Logger logger = LoggerFactory.getLogger( getClass() );

    private Context context = new SimpleContext();

    private ApplicationContext applicationContext;

    private String name = "plexus-spring adapter";

    private Date creationDate = new Date();

    /** key : component key , value : PlexusConfiguration */
    private Map plexusConfigurationPerComponent = new HashMap();

    public PlexusContainerAdapter()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#addComponentDescriptor(org.codehaus.plexus.component.repository.ComponentDescriptor)
     */
    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        throw new UnsupportedOperationException( "addComponentDescriptor( ComponentDescriptor componentDescriptor )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#addContextValue(java.lang.Object, java.lang.Object)
     */
    public void addContextValue( Object key, Object value )
    {
        context.put( key, value );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#addJarRepository(java.io.File)
     */
    public void addJarRepository( File repository )
    {
        throw new UnsupportedOperationException( "addJarRepository( File repository )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#addJarResource(java.io.File)
     */
    public void addJarResource( File resource )
        throws PlexusContainerException
    {
        throw new UnsupportedOperationException( "addJarResource( File resource )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#autowire(java.lang.Object)
     */
    public Object autowire( Object component )
        throws CompositionException
    {
        throw new UnsupportedOperationException( "autowire( Object component )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#createAndAutowire(java.lang.String)
     */
    public Object createAndAutowire( String clazz )
        throws CompositionException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        throw new UnsupportedOperationException( "createAndAutowire( String clazz )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#createChildContainer(java.lang.String, java.util.List, java.util.Map)
     */
    public PlexusContainer createChildContainer( String name, List classpathJars, Map context )
        throws PlexusContainerException
    {
        throw new UnsupportedOperationException( "createChildContainer( String name, List classpathJars, Map context )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#createChildContainer(java.lang.String, java.util.List, java.util.Map, java.util.List)
     */
    public PlexusContainer createChildContainer( String name, List classpathJars, Map context, List discoveryListeners )
        throws PlexusContainerException
    {
        throw new UnsupportedOperationException(
                                                 "createChildContainer( String name, List classpathJars, Map context, List discoveryListeners )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#createComponentRealm(java.lang.String, java.util.List)
     */
    public ClassRealm createComponentRealm( String id, List jars )
        throws PlexusContainerException
    {
        throw new UnsupportedOperationException( "createComponentRealm( String id, List jars )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#dispose()
     */
    public void dispose()
    {
        throw new UnsupportedOperationException( "dispose()" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getChildContainer(java.lang.String)
     */
    public PlexusContainer getChildContainer( String name )
    {
        throw new UnsupportedOperationException( "getChildContainer( String name )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptor(java.lang.String)
     */
    public ComponentDescriptor getComponentDescriptor( String role )
    {
        throw new UnsupportedOperationException( "getComponentDescriptor( String role )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptor(java.lang.String, java.lang.String)
     */
    public ComponentDescriptor getComponentDescriptor( String role, String roleHint )
    {
        throw new UnsupportedOperationException( "getComponentDescriptor( String role, String roleHint )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptor(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public ComponentDescriptor getComponentDescriptor( String role, ClassRealm realm )
    {
        throw new UnsupportedOperationException( "getComponentDescriptor( String role, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptor(java.lang.String, java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public ComponentDescriptor getComponentDescriptor( String role, String roleHint, ClassRealm realm )
    {
        throw new UnsupportedOperationException(
                                                 "getComponentDescriptor( String role, String roleHint, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptorList(java.lang.String)
     */
    public List getComponentDescriptorList( String role )
    {
        throw new UnsupportedOperationException( "getComponentDescriptorList( String role )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptorList(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public List getComponentDescriptorList( String role, ClassRealm componentRealm )
    {
        throw new UnsupportedOperationException( "getComponentDescriptorList( String role, ClassRealm componentRealm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptorMap(java.lang.String)
     */
    public Map getComponentDescriptorMap( String role )
    {
        throw new UnsupportedOperationException( "getComponentDescriptorMap( String role )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentDescriptorMap(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Map getComponentDescriptorMap( String role, ClassRealm componentRealm )
    {
        throw new UnsupportedOperationException( "getComponentDescriptorMap( String role, ClassRealm componentRealm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getComponentRealm(java.lang.String)
     */
    public ClassRealm getComponentRealm( String realmId )
    {
        throw new UnsupportedOperationException( "getComponentRealm( String realmId )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getContainerRealm()
     */
    public ClassRealm getContainerRealm()
    {
        throw new UnsupportedOperationException( "getContainerRealm()" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getContext()
     */
    public Context getContext()
    {
        return context;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getCreationDate()
     */
    public Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getLogger()
     */
    public Logger getLogger()
    {
        return getLoggerManager().getLoggerForComponent( getClass().getName() );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getLoggerManager()
     */
    public LoggerManager getLoggerManager()
    {
        if ( this.applicationContext.containsBean( "loggerManager" ) )
        {
            return (LoggerManager) this.applicationContext.getBean( "loggerManager" );
        }
        else
        {
            logger.warn( "No loggerManager set in context. Falling back to ConsoleLoggerManager" );
            ConsoleLoggerManager defaultLoggerManager = new ConsoleLoggerManager();
            defaultLoggerManager.initialize();
            return defaultLoggerManager;
        }
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getLookupRealm()
     */
    public ClassRealm getLookupRealm()
    {
        throw new UnsupportedOperationException( "getLookupRealm()" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getLookupRealm(java.lang.Object)
     */
    public ClassRealm getLookupRealm( Object component )
    {
        throw new UnsupportedOperationException( "getLookupRealm( Object component )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#hasChildContainer(java.lang.String)
     */
    public boolean hasChildContainer( String name )
    {
        throw new UnsupportedOperationException( "hasChildContainer( String name )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#hasComponent(java.lang.String)
     */
    public boolean hasComponent( String role )
    {
        return applicationContext.containsBean( PlexusToSpringUtils.buildSpringId( role ) );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#hasComponent(java.lang.String, java.lang.String)
     */
    public boolean hasComponent( String role, String roleHint )
    {
        return applicationContext.containsBean( PlexusToSpringUtils.buildSpringId( role, roleHint ) );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#isReloadingEnabled()
     */
    public boolean isReloadingEnabled()
    {
        throw new UnsupportedOperationException( "isReloadingEnabled()" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.String)
     */
    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return lookup( componentKey, (String) null );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.Class)
     */
    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return lookup( componentClass.getName(), (String) null );

    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Object lookup( String componentKey, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookup( String componentKey, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.String, java.lang.String)
     */
    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return applicationContext.getBean( PlexusToSpringUtils.buildSpringId( role, roleHint ) );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.Class, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookup( Class componentClass, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.Class, java.lang.String)
     */
    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role.getName(), roleHint );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.String, java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookup( String role, String roleHint, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookup(java.lang.Class, java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookup( Class role, String roleHint, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupList(java.lang.String)
     */
    public List lookupList( String role )
        throws ComponentLookupException
    {
        return PlexusToSpringUtils.lookupList( PlexusToSpringUtils.buildSpringId( role ), applicationContext );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupList(java.lang.Class)
     */
    public List lookupList( Class role )
        throws ComponentLookupException
    {
        return lookupList( role.getName() );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupList(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookupList( String role, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupList(java.lang.Class, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookupList( Class role, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupMap(java.lang.String)
     */
    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return PlexusToSpringUtils.lookupMap( PlexusToSpringUtils.buildSpringId( role ), applicationContext );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupMap(java.lang.Class)
     */
    public Map lookupMap( Class role )
        throws ComponentLookupException
    {
        return lookupMap( role.getName() );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupMap(java.lang.String, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookupMap( String role, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#lookupMap(java.lang.Class, org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException
    {
        throw new UnsupportedOperationException( "lookupMap( Class role, ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#registerComponentDiscoveryListener(org.codehaus.plexus.component.discovery.ComponentDiscoveryListener)
     */
    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        throw new UnsupportedOperationException(
                                                 "registerComponentDiscoveryListener( ComponentDiscoveryListener listener )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#release(java.lang.Object)
     */
    public void release( Object component )
        throws ComponentLifecycleException
    {
        // nothing here
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#releaseAll(java.util.Map)
     */
    public void releaseAll( Map components )
        throws ComponentLifecycleException
    {
        // nothing here
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#releaseAll(java.util.List)
     */
    public void releaseAll( List components )
        throws ComponentLifecycleException
    {
        // nothing here
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#removeChildContainer(java.lang.String)
     */
    public void removeChildContainer( String name )
    {
        throw new UnsupportedOperationException( "removeChildContainer( String name )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#removeComponentDiscoveryListener(org.codehaus.plexus.component.discovery.ComponentDiscoveryListener)
     */
    public void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        throw new UnsupportedOperationException(
                                                 "removeComponentDiscoveryListener( ComponentDiscoveryListener listener )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#resume(java.lang.Object)
     */
    public void resume( Object component )
        throws ComponentLifecycleException
    {
        throw new UnsupportedOperationException( "resume( Object component )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#setLoggerManager(org.codehaus.plexus.logging.LoggerManager)
     */
    public void setLoggerManager( LoggerManager loggerManager )
    {
        // ignored
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#setLookupRealm(org.codehaus.plexus.classworlds.realm.ClassRealm)
     */
    public ClassRealm setLookupRealm( ClassRealm realm )
    {
        throw new UnsupportedOperationException( "setLookupRealm( ClassRealm realm )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#setParentPlexusContainer(org.codehaus.plexus.PlexusContainer)
     */
    public void setParentPlexusContainer( PlexusContainer container )
    {
        throw new UnsupportedOperationException( "setParentPlexusContainer( PlexusContainer container )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#setReloadingEnabled(boolean)
     */
    public void setReloadingEnabled( boolean reloadingEnabled )
    {
        throw new UnsupportedOperationException( "setReloadingEnabled( boolean reloadingEnabled )" );
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.plexus.PlexusContainer#suspend(java.lang.Object)
     */
    public void suspend( Object component )
        throws ComponentLifecycleException
    {
        throw new UnsupportedOperationException( "suspend( Object component )" );
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext( ApplicationContext applicationContext )
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    private class SimpleContext
        implements Context
    {
        private Map map = new HashMap();

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#contains(java.lang.Object)
         */
        public boolean contains( Object key )
        {
            return PlexusConstants.PLEXUS_KEY.equals( key ) || map.containsKey( key );
        }

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#get(java.lang.Object)
         */
        public Object get( Object key )
            throws ContextException
        {
            return PlexusConstants.PLEXUS_KEY.equals( key ) ? PlexusContainerAdapter.this : map.get( key );
        }

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#getContextData()
         */
        public Map getContextData()
        {
            return map;
        }

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#hide(java.lang.Object)
         */
        public void hide( Object key )
            throws IllegalStateException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#makeReadOnly()
         */
        public void makeReadOnly()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         * @see org.codehaus.plexus.context.Context#put(java.lang.Object, java.lang.Object)
         */
        public void put( Object key, Object value )
            throws IllegalStateException
        {
            map.put( key, value );
        }

    }

    public Map getPlexusConfigurationPerComponent()
    {
        return plexusConfigurationPerComponent;
    }

    public void setPlexusConfigurationPerComponent( Map plexusConfigurationPerComponent )
    {
        this.plexusConfigurationPerComponent = plexusConfigurationPerComponent;
    }
}
