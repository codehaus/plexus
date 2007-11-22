package org.codehaus.plexus.webdav.jackrabbit;

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

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;

/**
 * JackrabbitFullAccessManager - a no-op access manager for jackrabbit, everyone is allowed in.
 * 
 * Access management for the jackrabbit webdav repository is handle elsewhere.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JackrabbitFullAccessManager
    implements AccessManager
{

    public boolean canAccess( String workspaceName )
        throws NoSuchWorkspaceException, RepositoryException
    {
        /* EVERYONE can access */
        return true;
    }

    public void checkPermission( ItemId id, int permissions )
        throws AccessDeniedException, ItemNotFoundException, RepositoryException
    {
        /* do nothing */
    }

    public void close()
        throws Exception
    {
        /* do nothing */
    }

    public void init( AMContext context )
        throws AccessDeniedException, Exception
    {
        /* do nothing */
    }

    public boolean isGranted( ItemId id, int permissions )
        throws ItemNotFoundException, RepositoryException
    {
        /* Everyone is granted */
        return true;
    }
}
