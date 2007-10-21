package org.codehaus.plexus.component.collections;

import org.codehaus.plexus.PlexusContainer;

import java.util.List;

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

/** @author Jason van Zyl */

// We need to have the collection notified when a new implementation of a given role has
// been added to the container. We probably need some options so that we know when new
// component descriptors have been added to the system, and an option to keep the collection
// up-to-date when new implementations are added.

public class AbstractComponentCollection
{
    /** The reference to the PlexusContainer */
    protected PlexusContainer container;

    /** The role of the components we are holding in this Collection. */
    protected String role;

    /** The role hint of the components we are holding in this Collection. */
    protected List roleHints;

    public AbstractComponentCollection( PlexusContainer container,
                                        String role,
                                        List roleHints )
    {
        this.container = container;

        this.role = role;

        this.roleHints = roleHints;
    }
}
