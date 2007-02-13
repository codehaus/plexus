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
package org.codehaus.plexus.discovery;

import java.util.Iterator;

/**
 * A ServiceDiscoverer discover resources
 *
 * @author Aldrin Leal
 */
public interface ResourceDiscoverer
{
    /** Role for this Component */
    public static final String ROLE = ResourceDiscoverer.class.getName();

    /**
     * ID
     *
     * @return ID of the Service
     */
    String getId();

    /**
     * Gets the name of the Service
     *
     * @return Name of the Service
     */
    String getName();

    /**
     * Finds Services
     *
     * @return ArrayList of DiscoverableServices
     */
    Iterator findResources();
}
