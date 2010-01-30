package org.codehaus.plexus.component.manager;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * This ensures a component is only used as a singleton, and is only shutdown when the container
 * shuts down.
 * 
 * @author Jason van Zyl
 */
public class SingletonComponentManager<T>
    extends AbstractComponentManager<T>
{
    private T singleton;

    public SingletonComponentManager( MutablePlexusContainer container,
                                      LifecycleHandler lifecycleHandler,
                                      ComponentDescriptor<T> componentDescriptor,
                                      String role,
                                      String roleHint )
    {
        super( container, lifecycleHandler, componentDescriptor, role, roleHint );
    }

    public synchronized void release( Object component )
        throws ComponentLifecycleException
    {
        if ( singleton == component )
        {
            dispose();
        }
    }

    public synchronized void dispose()
        throws ComponentLifecycleException
    {
        if ( singleton != null )
        {
            endComponentLifecycle( singleton );
            singleton = null;
        }
    }

    public synchronized T getComponent( )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        if ( singleton == null )
        {
            singleton = createComponentInstance();
        }

        incrementConnectionCount();

        return singleton;
    }
}
