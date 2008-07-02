package org.codehaus.plexus.classworlds.realm;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.net.MalformedURLException;

import java.net.URL;

import junit.framework.TestCase;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.TestUtil;

public class ClassRealmTest
    extends TestCase
{
    private ClassWorld world;

    public ClassRealmTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
        this.world = new ClassWorld();
    }

    public void tearDown()
    {
        this.world = null;
    }

    public void testNewRealm()
        throws Exception
    {
        ClassRealm realm = this.world.newRealm( "foo" );
        assertNotNull( realm );
        assertSame( this.world, realm.getWorld() );
        assertEquals( "foo", realm.getId() );
    }

    public void testLocateSourceRealm_NoImports()
        throws Exception
    {
        ClassRealm realm = new ClassRealm( this.world, "foo" );
        assertSame( realm, realm.locateSourceRealm( "com.werken.Stuff" ) );
    }

    public void testLocateSourceRealm_SimpleImport()
        throws Exception
    {
        ClassRealm mainRealm = (ClassRealm) this.world.newRealm( "main" );
        ClassRealm werkflowRealm = this.world.newRealm( "werkflow" );
        mainRealm.importFrom( "werkflow", "com.werken.werkflow" );

        assertSame( werkflowRealm, mainRealm.locateSourceRealm( "com.werken.werkflow.WerkflowEngine" ) );
        assertSame( werkflowRealm, mainRealm.locateSourceRealm( "com.werken.werkflow.process.ProcessManager" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "com.werken.blissed.Process" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "java.lang.Object" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "NoviceProgrammerClass" ) );
    }

    public void testLocateSourceRealm_MultipleImport()
        throws Exception
    {
        ClassRealm mainRealm = (ClassRealm) this.world.newRealm( "main" );
        ClassRealm werkflowRealm = this.world.newRealm( "werkflow" );
        ClassRealm blissedRealm = this.world.newRealm( "blissed" );

        mainRealm.importFrom( "werkflow", "com.werken.werkflow" );
        mainRealm.importFrom( "blissed", "com.werken.blissed" );

        assertSame( werkflowRealm, mainRealm.locateSourceRealm( "com.werken.werkflow.WerkflowEngine" ) );
        assertSame( werkflowRealm, mainRealm.locateSourceRealm( "com.werken.werkflow.process.ProcessManager" ) );
        assertSame( blissedRealm, mainRealm.locateSourceRealm( "com.werken.blissed.Process" ) );
        assertSame( blissedRealm, mainRealm.locateSourceRealm( "com.werken.blissed.guard.BooleanGuard" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "java.lang.Object" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "NoviceProgrammerClass" ) );
    }

    public void testLocateSourceRealm_Hierachy()
        throws Exception
    {
        ClassRealm mainRealm = (ClassRealm) this.world.newRealm( "main" );
        ClassRealm fooRealm = this.world.newRealm( "foo" );
        ClassRealm fooBarRealm = this.world.newRealm( "fooBar" );
        ClassRealm fooBarBazRealm = this.world.newRealm( "fooBarBaz" );

        mainRealm.importFrom( "foo", "foo" );
        mainRealm.importFrom( "fooBar", "foo.bar" );
        mainRealm.importFrom( "fooBarBaz", "foo.bar.baz" );

        assertSame( fooRealm, mainRealm.locateSourceRealm( "foo.Goober" ) );
        assertSame( fooRealm, mainRealm.locateSourceRealm( "foo.cheese.Goober" ) );
        assertSame( fooBarRealm, mainRealm.locateSourceRealm( "foo.bar.Goober" ) );
        assertSame( fooBarRealm, mainRealm.locateSourceRealm( "foo.bar.cheese.Goober" ) );
        assertSame( fooBarBazRealm, mainRealm.locateSourceRealm( "foo.bar.baz.Goober" ) );
        assertSame( fooBarBazRealm, mainRealm.locateSourceRealm( "foo.bar.baz.cheese.Goober" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "java.lang.Object" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "NoviceProgrammerClass" ) );
    }

    public void testLocateSourceRealm_Hierachy_Reverse()
        throws Exception
    {
        ClassRealm fooBarBazRealm = this.world.newRealm( "fooBarBaz" );
        ClassRealm fooBarRealm = this.world.newRealm( "fooBar" );
        ClassRealm fooRealm = this.world.newRealm( "foo" );
        ClassRealm mainRealm = (ClassRealm) this.world.newRealm( "main" );

        mainRealm.importFrom( "fooBarBaz", "foo.bar.baz" );
        mainRealm.importFrom( "fooBar", "foo.bar" );
        mainRealm.importFrom( "foo", "foo" );

        assertSame( fooRealm, mainRealm.locateSourceRealm( "foo.Goober" ) );
        assertSame( fooRealm, mainRealm.locateSourceRealm( "foo.cheese.Goober" ) );
        assertSame( fooBarRealm, mainRealm.locateSourceRealm( "foo.bar.Goober" ) );
        assertSame( fooBarRealm, mainRealm.locateSourceRealm( "foo.bar.cheese.Goober" ) );
        assertSame( fooBarBazRealm, mainRealm.locateSourceRealm( "foo.bar.baz.Goober" ) );
        assertSame( fooBarBazRealm, mainRealm.locateSourceRealm( "foo.bar.baz.cheese.Goober" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "java.lang.Object" ) );
        assertSame( mainRealm, mainRealm.locateSourceRealm( "NoviceProgrammerClass" ) );
    }

    public void testLoadClass_SystemClass()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );
        Class cls = mainRealm.loadClass( "java.lang.Object" );
        assertNotNull( cls );
    }

    public void testLoadClass_NonSystemClass()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );

        try
        {
            Class c = mainRealm.loadClass( "com.werken.projectz.UberThing" );
            fail( "A ClassNotFoundException should be thrown!" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }
    }

    public void testLoadClass_ClassWorldsClass()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );
        Class cls = mainRealm.loadClass( "org.codehaus.plexus.classworlds.ClassWorld" );
        assertNotNull( cls );
        assertSame( ClassWorld.class, cls );
    }

    public void testLoadClass_Local()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );
        try
        {
            mainRealm.loadClass( "a.A" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }

        mainRealm.addURL( getJarUrl( "a.jar" ) );
        Class classA = mainRealm.loadClass( "a.A" );
        assertNotNull( classA );
        ClassRealm otherRealm = this.world.newRealm( "other" );

        try
        {
            otherRealm.loadClass( "a.A" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }
    }

    public void testLoadClass_Imported()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );
        ClassRealm realmA = this.world.newRealm( "realmA" );
        
        try
        {
            realmA.loadClass( "a.A" );         
            fail( "realmA.loadClass(a.A) should have thrown a ClassNotFoundException" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }

        realmA.addURL( getJarUrl( "a.jar" ) );

        try
        {
            mainRealm.loadClass( "a.A" );
            fail( "mainRealm.loadClass(a.A) should have thrown a ClassNotFoundException" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }

        mainRealm.importFrom( "realmA", "a" );
        Class classA = realmA.loadClass( "a.A" );
        assertNotNull( classA );
        assertEquals( realmA, classA.getClassLoader() );
        Class classMain = mainRealm.loadClass( "a.A" );
        assertNotNull( classMain );
        assertEquals( realmA, classMain.getClassLoader() );
        assertSame( classA, classMain );
    }

    public void testLoadClass_Package()
        throws Exception
    {
        ClassRealm realmA = this.world.newRealm( "realmA" );
        realmA.addURL( getJarUrl( "a.jar" ) );

        Class clazz = realmA.loadClass( "a.A" );
        assertNotNull( clazz );
        assertEquals( "a.A", clazz.getName() );

        Package p = clazz.getPackage();
        assertNotNull( p );
        assertEquals( "p.getName()", "a", p.getName() );
    }

    public void testLoadClass_Complex()
        throws Exception
    {
        ClassRealm realmA = this.world.newRealm( "realmA" );
        ClassRealm realmB = this.world.newRealm( "realmB" );
        ClassRealm realmC = this.world.newRealm( "realmC" );

        realmA.addURL( getJarUrl( "a.jar" ) );
        realmB.addURL( getJarUrl( "b.jar" ) );
        realmC.addURL( getJarUrl( "c.jar" ) );

        realmC.importFrom( "realmA", "a" );

        realmC.importFrom( "realmB", "b" );

        realmA.importFrom( "realmC", "c" );

        Class classA_A = realmA.loadClass( "a.A" );
        Class classB_B = realmB.loadClass( "b.B" );
        Class classC_C = realmC.loadClass( "c.C" );

        assertNotNull( classA_A );
        assertNotNull( classB_B );
        assertNotNull( classC_C );

        assertEquals( realmA, classA_A.getClassLoader() );
        assertEquals( realmB, classB_B.getClassLoader() );
        assertEquals( realmC, classC_C.getClassLoader() );

        // load from C

        Class classA_C = realmC.loadClass( "a.A" );
        assertNotNull( classA_C );
        assertSame( classA_A, classA_C );
        assertEquals( realmA, classA_C.getClassLoader() );
        Class classB_C = realmC.loadClass( "b.B" );
        assertNotNull( classB_C );
        assertSame( classB_B, classB_C );
        assertEquals( realmB, classB_C.getClassLoader() );

        // load from A

        Class classC_A = realmA.loadClass( "c.C" );
        assertNotNull( classC_A );
        assertSame( classC_C, classC_A );
        assertEquals( realmC, classC_A.getClassLoader() );

        try
        {
            realmA.loadClass( "b.B" );
            fail( "throw ClassNotFoundException" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }

        // load from B

        try
        {
            realmB.loadClass( "a.A" );
            fail( "throw ClassNotFoundException" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }

        try
        {
            realmB.loadClass( "c.C" );
            fail( "throw ClassNotFoundException" );
        }
        catch ( ClassNotFoundException e )
        {
            // expected and correct
        }
    }

    protected URL getJarUrl( String jarName )
        throws MalformedURLException
    {
        return TestUtil.getTestResourceUrl( jarName );
    }

    public void testLoadClass_ClassWorldsClassRepeatedly()
        throws Exception
    {
        ClassRealm mainRealm = this.world.newRealm( "main" );

        for ( int i = 0; i < 100; i++ )
        {
            Class cls = mainRealm.loadClass( "org.codehaus.plexus.classworlds.ClassWorld" );
            assertNotNull( cls );
            assertSame( ClassWorld.class, cls );
        }
    }

    // 

    public void testP()
        throws Exception
    {
        ClassRealm realmA = this.world.newRealm( "realmA" );
        ClassRealm realmB = this.world.newRealm( "realmB" );
        ClassRealm realmC = this.world.newRealm( "realmC" );

        realmA.addURL( getJarUrl( "a.jar" ) );
        realmB.addURL( getJarUrl( "b.jar" ) );
        realmC.addURL( getJarUrl( "c.jar" ) );

        realmC.importFrom( "realmA", "a" );
        realmC.importFrom( "realmB", "b" );
        realmA.importFrom( "realmC", "c" );

        Class classAFromSelf = realmA.loadClassFromSelf( "a.A" );
        assertNotNull( classAFromSelf );

        Class classAFromImport = realmC.loadClassFromImport( "a.A" );
        assertNotNull( classAFromImport );
    }
    
    // From original ClassRealmImplTest
    
    // ----------------------------------------------------------------------
    // Class testing
    // ----------------------------------------------------------------------

    public void testLoadClassFromRealm()
        throws Exception
    {
        ClassRealm mainRealm = new ClassRealm( new ClassWorld(), "main" );
        mainRealm.addURL( getJarUrl2( "component0-1.0.jar" ) );
        mainRealm.loadClass( "org.codehaus.plexus.Component0" );
    }

    public void testLoadClassFromChildRealmWhereClassIsLocatedInParentRealm()
        throws Exception
    {
        ClassRealm mainRealm = new ClassRealm( new ClassWorld(), "main" );
        mainRealm.addURL( getJarUrl2( "component0-1.0.jar" ) );
        ClassRealm childRealm = mainRealm.createChildRealm( "child" );
        childRealm.loadClass( "org.codehaus.plexus.Component0" );
    }

    public void testLoadClassFromChildRealmWhereClassIsLocatedInGrantParentRealm()
        throws Exception
    {
        ClassRealm mainRealm = new ClassRealm( new ClassWorld(), "main" );
        mainRealm.addURL( getJarUrl2( "component0-1.0.jar" ) );
        ClassRealm childRealm = mainRealm.createChildRealm( "child" );
        ClassRealm grandchildRealm = childRealm.createChildRealm( "grandchild" );
        grandchildRealm.loadClass( "org.codehaus.plexus.Component0" );
    }

    public void testLoadNonExistentClass()
        throws Exception
    {
        ClassRealm mainRealm = new ClassRealm( new ClassWorld(), "main" );
        mainRealm.addURL( getJarUrl2( "component0-1.0.jar" ) );

        try
        {
            mainRealm.loadClass( "org.foo.bar.NonExistentClass" );
            fail( "A ClassNotFoundException should have been thrown!" );
        }
        catch ( ClassNotFoundException e )
        {
        }
    }

    public void testImport()
        throws Exception
    {
        ClassWorld world = new ClassWorld();
        ClassRealm r0 = world.newRealm( "r0" );
        ClassRealm r1 = world.newRealm( "r1" );
        
        r0.addURL( getJarUrl2( "component0-1.0.jar" ) );
        r1.importFrom( "r0", "org.codehaus.plexus" );
        r1.loadClass( "org.codehaus.plexus.Component0" );
    }

    // ----------------------------------------------------------------------
    // Resource testing
    // ----------------------------------------------------------------------

    public void testResource()
        throws Exception
    {
        ClassRealm mainRealm = new ClassRealm( new ClassWorld(), "main" );
        mainRealm.addURL( getJarUrl2( "component0-1.0.jar" ) );
        URL resource = mainRealm.getResource( "META-INF/plexus/components.xml" );
        assertNotNull( resource );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected URL getJarUrl2( String jarName )
        throws Exception
    {
        File jarFile = new File( System.getProperty( "basedir" ), "src/test-jars/" + jarName );
        return jarFile.toURI().toURL();
    }    
}
