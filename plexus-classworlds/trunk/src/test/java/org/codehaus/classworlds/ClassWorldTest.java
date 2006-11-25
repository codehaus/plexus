package org.codehaus.classworlds;

/*
 $Id$

 Copyright 2002 (C) The Werken Company. All Rights Reserved.

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. The name "classworlds" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Werken Company.  For written permission,
    please contact bob@werken.com.

 4. Products derived from this Software may not be called "classworlds"
    nor may "classworlds" appear in their names without prior written
    permission of The Werken Company. "classworlds" is a registered
    trademark of The Werken Company.

 5. Due credit should be given to The Werken Company.
    (http://classworlds.werken.com/).

 THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE WERKEN COMPANY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 */

import junit.framework.TestCase;

public class ClassWorldTest extends TestCase
{
    private ClassWorld world;

    public ClassWorldTest( String name )
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

    public void testEmpty()
    {
        assertTrue( this.world.getRealms().isEmpty() );
    }

    public void testNewRealm() throws Exception
    {
        ClassRealm realm = (ClassRealm) this.world.newRealm( "foo" );

        assertNotNull( realm );
    }

    public void testGetRealm() throws Exception
    {
        ClassRealm realm = (ClassRealm) this.world.newRealm( "foo" );

        assertSame( realm,
                    this.world.getRealm( "foo" ) );
    }

    public void testNewRealm_Duplicate() throws Exception
    {
        try
        {
            this.world.newRealm( "foo" );
            this.world.newRealm( "foo" );

            fail( "throw DuplicateRealmException" );
        }
        catch ( DuplicateRealmException e )
        {
            // expected and correct

            assertSame( this.world,
                        e.getWorld() );

            assertEquals( "foo",
                          e.getId() );
        }
    }

    public void testGetRealm_NoSuch() throws Exception
    {
        try
        {
            this.world.getRealm( "foo" );
            fail( "throw NoSuchRealmException" );
        }
        catch ( NoSuchRealmException e )
        {
            // expected and correct

            assertSame( this.world,
                        e.getWorld() );

            assertEquals( "foo",
                          e.getId() );
        }
    }

    public void testGetRealms() throws Exception
    {
        assertTrue( this.world.getRealms().isEmpty() );

        ClassRealm foo = (ClassRealm) this.world.newRealm( "foo" );

        assertEquals( 1,
                      this.world.getRealms().size() );

        assertTrue( this.world.getRealms().contains( foo ) );

        ClassRealm bar = (ClassRealm) this.world.newRealm( "bar" );

        assertEquals( 2,
                      this.world.getRealms().size() );

        assertTrue( this.world.getRealms().contains( bar ) );
    }
}
