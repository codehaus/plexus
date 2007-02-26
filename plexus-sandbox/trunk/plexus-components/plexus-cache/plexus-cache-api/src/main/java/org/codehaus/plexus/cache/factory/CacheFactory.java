package org.codehaus.plexus.cache.factory;

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

import org.codehaus.plexus.cache.Cache;
import org.codehaus.plexus.cache.CacheHints;
import org.codehaus.plexus.cache.impl.NoCacheCache;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * CacheFactory - dynamic cache creation (and tracking) facility for non-plexus objects to use.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CacheFactory
{
    static class CacheFactoryHolder
    {
        static CacheFactory instance = new CacheFactory();
    }

    private static Map caches;

    private static CacheCreator creator;

    private CacheFactory()
    {
        caches = new HashMap();

        try
        {
            ClassLoader classLoader = this.getClass().getClassLoader();

            if ( classLoader == null )
            {
                System.err.println( "Using System ClassLoader." );
                classLoader = ClassLoader.getSystemClassLoader();
            }
            else
            {
                System.err.println( "Using Object Classloader." );
            }

            Enumeration cachePropResources = classLoader.getResources( "META-INF/plexus-cache.properties" );

            if ( cachePropResources.hasMoreElements() )
            {
                URL propURL = (URL) cachePropResources.nextElement();
                Properties props = new Properties();

                props.load( propURL.openStream() );
                String creatorImpl = props.getProperty( "cache.creator" );

                Class creatorClass = classLoader.loadClass( creatorImpl );
                creator = (CacheCreator) creatorClass.newInstance();
                System.out.println( "Using creator: " + creator.getClass().getName() );
            }
            else
            {
                System.err.println( "No resource found." );
            }

            if ( cachePropResources.hasMoreElements() )
            {
                System.err.println( "More than 1 CacheCreator provider exists in classpath. "
                    + "Using first one found [" + creator.getClass().getName() + "]." );
            }
        }
        catch ( IOException e )
        {
            throw new ExceptionInInitializerError( e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ExceptionInInitializerError( e );
        }
        catch ( InstantiationException e )
        {
            throw new ExceptionInInitializerError( e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ExceptionInInitializerError( e );
        }
    }

    public static CacheFactory getInstance()
    {
        return CacheFactoryHolder.instance;
    }

    public void setCacheCreatorFactory( CacheCreator creator )
    {
        CacheFactory.creator = creator;
    }

    public Cache getCache( String id, CacheHints hints )
    {
        if ( creator == null )
        {
            return new NoCacheCache();
        }

        if ( caches.containsKey( id ) )
        {
            return (Cache) caches.get( id );
        }

        if ( hints == null )
        {
            // Setup some defaults.
            hints = new CacheHints();
            hints.setName( id );
        }

        Cache cache = CacheFactory.creator.createCache( hints );

        caches.put( id, cache );
        return (Cache) cache;
    }
}
