package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActiveMapTCK
    extends PlexusTestCase
{

    private ActiveMap map;

    protected abstract String getTestMapRoleHint();
    
    protected abstract ActiveMap constructKnownBadActiveMap();

    public void setUp()
        throws Exception
    {
        super.setUp();

        map = (ActiveMap) lookup( ActiveMap.ROLE, getTestMapRoleHint() );
    }

//    public void clear()
//    {
//        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
//    }
    public void testClear_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            map.clear();
            
            fail( "Should fail with UnsupportedOperationException; ActiveMap impls are not directly mutable." );
        }
        catch( UnsupportedOperationException e )
        {
            // expected
        }
    }

//    public Object put( Object arg0, Object arg1 )
//    {
//        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
//    }
    public void testPut_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            map.put( "test", "test" );
            
            fail( "Should fail with UnsupportedOperationException; ActiveMap impls are not directly mutable." );
        }
        catch( UnsupportedOperationException e )
        {
            // expected
        }
    }

//    public void putAll( Map arg0 )
//    {
//        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
//    }
    public void testPutAll_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            map.putAll( Collections.singletonMap( "test", "test" ) );
            
            fail( "Should fail with UnsupportedOperationException; ActiveMap impls are not directly mutable." );
        }
        catch( UnsupportedOperationException e )
        {
            // expected
        }
    }

//    public Object remove( Object key )
//    {
//        throw new UnsupportedOperationException( "ActiveMap implementations are not mutable." );
//    }
    public void testRemove_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            map.remove( "test" );
            
            fail( "Should fail with UnsupportedOperationException; ActiveMap impls are not directly mutable." );
        }
        catch( UnsupportedOperationException e )
        {
            // expected
        }
    }

    public void testGet()
    {
        TestComponent comp1 = (TestComponent) map.get( "one" );
        assertEquals( "first", comp1.getValue() );

        TestComponent comp2 = (TestComponent) map.get( "two" );
        assertEquals( "second", comp2.getValue() );
    }

    public void testContainsKey()
    {
        assertTrue( map.containsKey( "one" ) );
        assertFalse( map.containsKey( "one thousand" ) );
    }

    public void testContainsValue()
    {
        TestComponent oneTest = new TestComponent( "first" );
        TestComponent oneThousandTest = new TestComponent( "one thousandth" );

        assertTrue( map.containsValue( oneTest ) );
        assertFalse( map.containsValue( oneThousandTest ) );
    }

    //    Set entrySet();
    public void testEntrySet()
    {
        TestComponent first = new TestComponent( "first" );
        TestComponent second = new TestComponent( "second" );

        Set entries = map.entrySet();
        assertEquals( 3, entries.size() );
        for ( Iterator it = entries.iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            assertTrue( entry.getKey().equals( "one" ) || entry.getKey().equals( "two" ) || entry.getKey().equals( "three" ) );
            
            // NOTE: The third entry is a copy of the first, mostly for testing lists
            assertTrue( entry.getValue().equals( first ) || entry.getValue().equals( second ) );
        }
    }

    //    Set keySet();
    public void testKeySet()
    {
        Set keysTest = new HashSet( 3 );
        keysTest.add( "one" );
        keysTest.add( "two" );
        keysTest.add( "three" );

        assertEquals( keysTest, map.keySet() );
    }

    //    Collection values();
    public void testValues()
    {
        TestComponent first = new TestComponent( "first" );
        TestComponent second = new TestComponent( "second" );

        // NOTE: The third entry is a copy of the first, mostly for testing lists
        TestComponent third = new TestComponent( "first" );

        List valuesTest = new ArrayList( 3 );
        valuesTest.add( first );
        valuesTest.add( second );
        valuesTest.add( third );

        Collection values = map.values();

        assertEquals( valuesTest.size(), values.size() );
        assertTrue( values.containsAll( valuesTest ) );
    }

    //    boolean checkedContainsKey( Object key )
    //        throws ComponentLookupException;
    public void testCheckedContainsKey_ShouldFindKey()
        throws ComponentLookupException
    {
        assertTrue( map.checkedContainsKey( "one" ) );
        assertFalse( map.checkedContainsKey( "one thousand" ) );
    }

    public void testCheckedContainsKey_ShouldThrowExceptionWhenWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedContainsKey( "bad" );

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //    boolean checkedContainsValue( Object value )
    //        throws ComponentLookupException;
    public void testCheckedContainsValue_ShouldFindValue()
        throws ComponentLookupException
    {
        assertTrue( map.checkedContainsValue( new TestComponent( "first" ) ) );
        assertFalse( map.checkedContainsValue( new TestComponent( "one thousandth" ) ) );
    }

    public void testCheckedContainsValue_ShouldThrowExceptionWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedContainsKey( TestBadComponent.newTestInstance() );

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //    Set checkedEntrySet()
    //        throws ComponentLookupException;
    public void testCheckedEntrySet_ShouldContainTwoElementsAndNotAThird()
        throws ComponentLookupException
    {
        TestComponent first = new TestComponent( "first" );
        TestComponent second = new TestComponent( "second" );
        TestComponent thousandth = new TestComponent( "one thousandth" );

        Set entries = map.checkedEntrySet();
        assertEquals( 3, entries.size() );
        for ( Iterator it = entries.iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            
            assertTrue( !entry.getKey().equals( "one thousandth" )
                && ( entry.getKey().equals( "one" ) || entry.getKey().equals( "two" ) || entry.getKey().equals( "three" ) ) );
            
            // NOTE: The third entry is a copy of the first, mostly for testing lists
            assertTrue( !entry.getValue().equals( thousandth )
                && ( entry.getValue().equals( first ) || entry.getValue().equals( second ) ) );
        }
    }

    public void testCheckedEntrySet_ShouldThrowExceptionWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedEntrySet();

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }
    
    //    Object checkedGet( Object key )
    //        throws ComponentLookupException;
    public void testCheckedGet() throws ComponentLookupException
    {
        TestComponent comp1 = (TestComponent) map.checkedGet( "one" );
        assertEquals( "first", comp1.getValue() );

        TestComponent comp2 = (TestComponent) map.checkedGet( "two" );
        assertEquals( "second", comp2.getValue() );
    }

    public void testCheckedGet_ShouldThrowExceptionWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedGet( TestBadComponent.newTestInstance() );

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //    Set checkedKeySet()
    //        throws ComponentLookupException;
    public void testCheckedKeySet_ShouldFindTwoComponents() throws ComponentLookupException
    {
        Set keysTest = new HashSet( 2 );
        keysTest.add( "one" );
        keysTest.add( "two" );
        keysTest.add( "three" );

        Set keySet = map.checkedKeySet();
        
        assertEquals( 3, keySet.size() );
        assertEquals( keysTest, keySet );
    }
    
    public void testCheckedKeySet_ShouldThrowExceptionWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedKeySet();

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //    Collection checkedValues()
    //        throws ComponentLookupException;
    public void testCheckedValues_ShouldFindTwoComponents() throws ComponentLookupException
    {
        TestComponent first = new TestComponent( "first" );
        TestComponent second = new TestComponent( "second" );
        
        // NOTE: The third entry is a copy of the first, mostly for testing lists
        TestComponent third = new TestComponent( "first" );

        List valuesTest = new ArrayList( 3 );
        valuesTest.add( first );
        valuesTest.add( second );
        valuesTest.add( third );

        Collection values = map.checkedValues();

        assertEquals( valuesTest.size(), values.size() );
        assertTrue( values.containsAll( valuesTest ) );
    }

    public void testCheckedValues_ShouldThrowExceptionWhenComponentsCannotBeCreated()
    {
        ActiveMap map = constructKnownBadActiveMap();

        try
        {
            map.checkedValues();

            fail( "Should throw ComponentLookupException when mapped components cannot be created." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

}
