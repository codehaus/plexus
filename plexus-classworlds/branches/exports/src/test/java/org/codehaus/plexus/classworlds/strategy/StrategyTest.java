package org.codehaus.plexus.classworlds.strategy;

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

import junit.framework.TestCase;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.TestUtil;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

// jars within jars
// hierarchy vs graph

public class StrategyTest
        extends TestCase
{
    private ClassWorld world;
    
    private ClassRealm realm;

    private Strategy strategy;

    public void setUp()
        throws Exception
    {
        this.world = new ClassWorld();
        this.realm = this.world.newRealm( "realm" );
        this.strategy = this.realm.getStrategy();
        
        realm.addURL( TestUtil.getTestComponent( "component0-1.0.jar" ) );
    }

    public void testLoadingOfApplicationClass()
        throws Exception
    {
        Class c = strategy.loadClass( "org.codehaus.plexus.Component0" );

        assertNotNull( c );
    }

    public void testLoadingOfApplicationClassThenDoingItAgain()
        throws Exception
    {
        Class c;

        c = strategy.loadClass( "org.codehaus.plexus.Component0" );

        assertNotNull( c );

        c = strategy.loadClass( "org.codehaus.plexus.Component0" );

        assertNotNull( c );
    }


    public void testLoadingOfSystemClass()
        throws Exception
    {
        Class c = strategy.loadClass( "java.lang.Object" );

        assertNotNull( c );
    }

    public void testLoadingOfNonExistentClass()
        throws Exception
    {
        try
        {
            strategy.loadClass( "org.codehaus.plexus.NonExistentComponent" );

            fail( "Should have thrown a ClassNotFoundException!" );
        }
        catch ( ClassNotFoundException e )
        {
            // do nothing
        }
    }

    public void testGetApplicationResource()
        throws Exception
    {
        URL resource = strategy.getResource( "META-INF/plexus/components.xml" );

        assertNotNull( resource );

        String content = getContent( resource.openStream() );

        assertTrue( content.startsWith( "<component-set>" ) );
    }

    public void testGetSystemResource()
        throws Exception
    {
        URL resource = strategy.getResource( "java/lang/Object.class" );

        assertNotNull( resource );
    }


    public void testFindResources()
        throws Exception
    {
        realm.addURL( TestUtil.getTestComponent( "component1-1.0.jar" ) );

        Enumeration e = strategy.findResources( "META-INF/plexus/components.xml" );

        assertNotNull( e );

        int resourceCount = 0;

        for ( Enumeration resources = e; resources.hasMoreElements(); )
        {
            resources.nextElement();

            resourceCount++;
        }

        assertEquals( 2, resourceCount );
    }


    public void testGetResourceAsStream()
        throws Exception
    {
        InputStream is = strategy.getResourceAsStream( "META-INF/plexus/components.xml" );

        assertNotNull( is );

        String content = getContent( is );

        assertTrue( content.startsWith( "<component-set>" ) );
    }

    protected String getContent( InputStream in )
        throws Exception
    {
        byte[] buffer = new byte[1024];

        int read = 0;

        StringBuffer content = new StringBuffer();

        while ( ( read = in.read( buffer, 0, 1024 ) ) >= 0 )
        {
            content.append( new String( buffer, 0, read ) );
        }

        return content.toString();
    }
}
