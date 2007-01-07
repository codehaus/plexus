package org.codehaus.plexus.webdav;

/*
 * Copyright 2001-2007 The Codehaus.
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

import java.io.File;
import java.util.Collection;

/**
 * DavServerManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface DavServerManager
{
    /** The Plexus ROLE name. */
    public static final String ROLE = DavServerManager.class.getName();
    
    /**
     * Create a DavServerComponent and start tracking it. 
     * 
     * @param prefix the prefix for this component.
     * @param rootDirectory the root directory for this component's content.
     * @return the created component, suitable for use.
     * @throws DavServerException 
     */
    public DavServerComponent createServer( String prefix, File rootDirectory ) throws DavServerException;

    /**
     * Get the collection of tracked servers.
     * 
     * @return Collection of {@link DavServerComponent} objects.
     */
    public Collection getServers();
    
    /**
     * Removes a specific server from the tracked list of servers.
     * 
     * NOTE: This does not remove the associated files on disk, merely the reference being tracked.
     * 
     * @param prefix the prefix to remove.
     */
    public void removeServer( String prefix );
    
    /**
     * Get the {@link DavServerComponent} associated with the specified prefix.
     * 
     * @param prefix the prefix for the dav server component to use.
     * @return the DavServerComponent, or null if not found.
     */
    public DavServerComponent getServer( String prefix );
}
