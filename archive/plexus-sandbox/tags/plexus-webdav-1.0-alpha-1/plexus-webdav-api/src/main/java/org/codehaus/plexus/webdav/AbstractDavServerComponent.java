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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AbstractDavServerComponent 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractDavServerComponent
    implements DavServerComponent
{
    private List listeners;

    public AbstractDavServerComponent()
    {
        listeners = new ArrayList();
    }

    public void addListener( DavServerListener listener )
    {
        listeners.add( listener );
    }

    public void removeListener( DavServerListener listener )
    {
        listeners.remove( listener );
    }

    protected void triggerCollectionCreated( String resource )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            DavServerListener listener = (DavServerListener) it.next();
            try
            {
                listener.serverCollectionCreated( this, resource );
            }
            catch ( Exception e )
            {
                /* ignore error */
            }
        }
    }

    protected void triggerCollectionRemoved( String resource )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            DavServerListener listener = (DavServerListener) it.next();
            try
            {
                listener.serverCollectionRemoved( this, resource );
            }
            catch ( Exception e )
            {
                /* ignore error */
            }
        }
    }

    protected void triggerResourceCreated( String resource )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            DavServerListener listener = (DavServerListener) it.next();
            try
            {
                listener.serverResourceCreated( this, resource );
            }
            catch ( Exception e )
            {
                /* ignore error */
            }
        }
    }

    protected void triggerResourceRemoved( String resource )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            DavServerListener listener = (DavServerListener) it.next();
            try
            {
                listener.serverResourceRemoved( this, resource );
            }
            catch ( Exception e )
            {
                /* ignore error */
            }
        }
    }

    protected void triggerResourceModified( String resource )
    {
        Iterator it = listeners.iterator();
        while ( it.hasNext() )
        {
            DavServerListener listener = (DavServerListener) it.next();
            try
            {
                listener.serverResourceModified( this, resource );
            }
            catch ( Exception e )
            {
                /* ignore error */
            }
        }
    }

    public boolean hasResource( String resource )
    {
        File rootDir = getRootDirectory();
        if ( rootDir == null )
        {
            return false;
        }
        File resourceFile = new File( rootDir, resource );
        return resourceFile.exists();
    }
}
