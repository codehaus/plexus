package org.codehaus.plexus.jcs;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.apache.jcs.access.CacheAccess;
import org.apache.jcs.access.exception.CacheException;

import java.util.Properties;

/**
 * Avalon component for JCS.
 *
 * Expects to be configured with a single element named 'properties' which
 * is the name of a standard JCS properties file, and should be in the
 * classpath.
 *
 * Once configured, the {@link #getAccess} methods will provide access to
 * specific CompositeCaches (regions).
 *
 * TODO: Create a JCSEngine class in JCS which provides capabilities similar
 *       to this but does not depend on avalon. This class will them wrap that.
 *
 *       The various static factory methods in CacheAccess and
 *       CompositeCacheManager should be removed, those classes should only
 *       be accessible as instances created by this class or the engine.
 *
 *       To provide for a singleton style access, a singleton wrapper can be
 *       created which holds a single reference to the engine. The JCS
 *       class is the obvious candidate, since it was intended for simple use,
 *       and could be modified without requiring us to change the API.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public class DefaultJCSComponent
    implements JCSComponent, Configurable, Initializable, Disposable
{
    /**
     * Manager for the set of compsite caches (aka regions) for this JCS
     * component instance.
     */
    CompositeCacheManager manager = new CompositeCacheManager();

    /**
     * @see Initializable#initialize
     */
    public void initialize()
        throws Exception
    {
        // No initialization required for now, however there will be in the
        // future when the configuration capabilities are enhanced
    }

    /**
     * @see Configurable#configure
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        // Right now, we build a properties object from the configuration and
        // pass it to JCS. This will change once JCS has a more intelligent
        // configuration mechanism.

        Configuration[] propertyElements =
            configuration.getChild( "properties" ).getChildren( "property" );

        Properties properties = new Properties();

        for ( int i = 0; i < propertyElements.length; i++ )
        {
            properties.put( propertyElements[i].getAttribute( "name" ),
                            propertyElements[i].getAttribute( "value" ) );
        }

        manager.configure( properties );
    }

    /**
     * @see Disposable#dispose
     */
    public void dispose()
    {
        manager.release();
    }

    /**
     * Get access to the region named <code>name</code>, creating it with the
     * provided defaults for cache and element attributes if it does not
     * already exist.
     *
     * @param name Name that will identify the region
     * @param cacheAttributes CompositeCacheAttributes for the region
     * @param elementAttributes Default element attributes for the region
     *
     * @return CacheAccess instance for the new region
     *
     * @exception CacheException
     */
    public CacheAccess getAccess( String name,
                                  CompositeCacheAttributes cacheAttributes,
                                  IElementAttributes elementAttributes )
        throws CacheException
    {
        return new CacheAccess(
            manager.getCache( name, cacheAttributes, elementAttributes ) );
    }

    /**
     * @see #getAccess( String, CompositeCacheAttributes, IElementAttributes )
     */
    public CacheAccess getAccess( String name )
        throws CacheException
    {
        return new CacheAccess(
            manager.getCache( name ) );
    }
}
