package org.codehaus.plexus.security.rbac;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
 * Operation
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface Operation
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = Operation.class.getName();
    
    /**
     * Get name
     */
    public abstract String getName();

    /**
     * Get resource
     */
    public Resource getResource();

    /**
     * true if the resource is required for authorization to be granted
     */
    public boolean isResourceRequired();

    /**
     * Set name
     * 
     * @param name
     */
    public void setName( String name );

    /**
     * Set resource name
     * 
     * @param resource
     */
    public void setResource( Resource resource );

    /**
     * Set true if the resource is required for authorization to be granted
     * 
     * @param resourceRequired
     */
    public void setResourceRequired( boolean resourceRequired );

}