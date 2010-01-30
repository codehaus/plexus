package org.codehaus.plexus.component.factory;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import org.codehaus.classworlds.ClassRealmAdapter;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 *
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public abstract class AbstractComponentFactory
    implements ComponentFactory
{
    // This is for backward compatibility
    private String id;

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        // for backward-compatibility with the old component factories delegate to the old-style method
        return newInstance( componentDescriptor, ClassRealmAdapter.getInstance( classRealm ), container );
    }

    protected Object newInstance( ComponentDescriptor componentDescriptor,
                                  org.codehaus.classworlds.ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        throw new IllegalStateException( getClass().getName() + " does not implement component creation." );
    }

}
