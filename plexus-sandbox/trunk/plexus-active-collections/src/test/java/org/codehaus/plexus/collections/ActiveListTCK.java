package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public abstract class ActiveListTCK
    extends PlexusTestCase
{

    private ActiveList list;

    protected abstract String getTestListRoleHint();

    protected abstract ActiveList constructKnownBadActiveList();

    public void setUp()
        throws Exception
    {
        super.setUp();

        list = (ActiveList) lookup( ActiveList.ROLE, getTestListRoleHint() );
    }

    //    public boolean add( Object arg0 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testAdd_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.add( "test" );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public void add( int arg0, Object arg1 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testAdd_WithIndex_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.add( 0, "test" );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public boolean addAll( Collection arg0 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testAddAll_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.addAll( Collections.singletonList( "test" ) );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public boolean addAll( int arg0, Collection arg1 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testAddAll_WithIndex_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.addAll( 0, Collections.singletonList( "test" ) );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public void clear()
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testClear_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.clear();

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public boolean remove( Object o )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testRemove_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.remove( "test" );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public Object remove( int index )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testRemove_WithIndex_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.remove( 0 );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public boolean removeAll( Collection arg0 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testRemoveAll_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.removeAll( Collections.singletonList( "test" ) );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public boolean retainAll( Collection arg0 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testRetainAll_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.retainAll( Collections.singletonList( "test" ) );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //    public Object set( int arg0, Object arg1 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testSet_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            list.set( 0, "test" );

            fail( "Should throw UnsupportedOperationException; ActiveList impls are not directly mutable." );
        }
        catch ( UnsupportedOperationException e )
        {
            // expected.
        }
    }

    //boolean contains( Object value );
    public void testContains()
    {
        assertTrue( list.contains( new TestComponent( "first" ) ) );
        assertFalse( list.contains( new TestComponent( "one thousandth" ) ) );
    }

    //boolean containsAll( Collection collection );
    public void testContainsAll()
    {
        Set containsTest = new HashSet( 2 );
        containsTest.add( new TestComponent( "first" ) );
        containsTest.add( new TestComponent( "second" ) );
        
        Set doesntContainTest = Collections.singleton( new TestComponent( "one thousandth" ) );
        
        assertTrue( list.containsAll( containsTest ) );
        assertFalse( list.containsAll( doesntContainTest ) );
    }

    //Object get( int index );
    public void testGet()
    {
        assertEquals( new TestComponent( "first" ), list.get( 0 ) );
    }

    //int indexOf( Object value );
    public void testIndexOf()
    {
        assertEquals( 0, list.indexOf( new TestComponent( "first" ) ) );
    }

    //Iterator iterator();
    public void testIterator()
    {
        Iterator it = list.iterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
    }

    //int lastIndexOf( Object value );
    public void testLastIndexOf()
    {
        assertEquals( list.size() - 1, list.lastIndexOf( new TestComponent( "first" ) ) );
    }

    //ListIterator listIterator();
    public void testListIterator()
    {
        ListIterator it = list.listIterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "second" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertFalse( it.hasPrevious() );
    }

    //ListIterator listIterator( int index );
    public void testListIterator_WithIndex()
    {
        ListIterator it = list.listIterator( 1 );
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "second" ), it.previous() );
        
        // should allow iterating back PREVIOUS to the initial index in the list order...
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertFalse( it.hasPrevious() );
    }

    //List subList( int fromIndex, int toIndex );
    public void testSubList()
    {
        assertEquals( new TestComponent( "first" ), list.subList( 0, 1 ).get( 0 ) );
        assertEquals( new TestComponent( "second" ), list.subList( 1, 2 ).get( 0 ) );
        assertEquals( new TestComponent( "first" ), list.subList( 2, 3 ).get( 0 ) );
    }

    //Object[] toArray();
    public void testToArray()
    {
        Object[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
            new TestComponent( "first" )
        };
        
        assertTrue( Arrays.equals( test, list.toArray() ) );
    }

    //Object[] toArray( Object[] array );
    public void testToArray_WithInputArray()
    {
        TestComponent[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
            new TestComponent( "first" )
        };
        
        assertTrue( Arrays.equals( test, (TestComponent[]) list.toArray( new TestComponent[]{} ) ) );
    }

    //boolean checkedContains( Object value )
    //  throws ComponentLookupException;
    public void testCheckedContains() throws ComponentLookupException
    {
        assertTrue( list.checkedContains( new TestComponent( "first" ) ) );
        assertFalse( list.checkedContains( new TestComponent( "one thousandth" ) ) );
    }
    
    public void testCheckedContains_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedContains( TestBadComponent.newTestInstance() );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //boolean checkedContainsAll( Collection collection )
    //  throws ComponentLookupException;
    public void testCheckedContainsAll() throws ComponentLookupException
    {
        Set containsTest = new HashSet( 2 );
        containsTest.add( new TestComponent( "first" ) );
        containsTest.add( new TestComponent( "second" ) );
        
        Set doesntContainTest = Collections.singleton( new TestComponent( "one thousandth" ) );
        
        assertTrue( list.checkedContainsAll( containsTest ) );
        assertFalse( list.checkedContainsAll( doesntContainTest ) );
    }
    
    public void testCheckedContainsAll_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedContainsAll( Collections.singletonList( TestBadComponent.newTestInstance() ) );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //Object checkedGet( int index )
    //  throws ComponentLookupException;
    public void testCheckedGet() throws ComponentLookupException
    {
        assertEquals( new TestComponent( "first" ), list.checkedGet( 0 ) );
    }
    
    public void testCheckedGet_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedGet( 0 );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //int checkedIndexOf( Object value )
    //  throws ComponentLookupException;
    public void testCheckedIndexOf() throws ComponentLookupException
    {
        assertEquals( 0, list.checkedIndexOf( new TestComponent( "first" ) ) );
    }
    
    public void testCheckedIndexOf_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedIndexOf( TestBadComponent.newTestInstance() );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //Iterator checkedIterator()
    //  throws ComponentLookupException;
    public void testCheckedIterator() throws ComponentLookupException
    {
        Iterator it = list.checkedIterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
    }
    
    public void testCheckedIterator_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedIterator();
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //int checkedLastIndexOf( Object value )
    //  throws ComponentLookupException;
    public void testCheckedLastIndexOf() throws ComponentLookupException
    {
        assertEquals( list.size() - 1, list.checkedLastIndexOf( new TestComponent( "first" ) ) );
    }
    
    public void testCheckedLastIndexOf_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedLastIndexOf( new TestComponent( "first" ) );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //ListIterator checkedListIterator()
    //  throws ComponentLookupException;
    public void testCheckedListIterator() throws ComponentLookupException
    {
        ListIterator it = list.checkedListIterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "second" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertFalse( it.hasPrevious() );
    }
    
    public void testCheckedListIterator_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedListIterator();
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //ListIterator checkedListIterator( int index )
    //  throws ComponentLookupException;
    public void testCheckedListIterator_WithIndex() throws ComponentLookupException
    {
        ListIterator it = list.checkedListIterator( 1 );
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertFalse( it.hasNext() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "second" ), it.previous() );
        
        // should allow iterating back PREVIOUS to the initial index in the list order...
        assertTrue( it.hasPrevious() );
        assertEquals( new TestComponent( "first" ), it.previous() );
        assertFalse( it.hasPrevious() );
    }
    
    public void testCheckedListIterator_WithIndex_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedListIterator( 1 );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //List checkedSubList( int fromIndex, int toIndex )
    //  throws ComponentLookupException;
    public void testCheckedSubList() throws ComponentLookupException
    {
        assertEquals( new TestComponent( "first" ), list.checkedSubList( 0, 1 ).get( 0 ) );
        assertEquals( new TestComponent( "second" ), list.checkedSubList( 1, 2 ).get( 0 ) );
        assertEquals( new TestComponent( "first" ), list.checkedSubList( 2, 3 ).get( 0 ) );
    }
    
    public void testCheckedSubList_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedSubList( 1, 2 );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //Object[] checkedToArray()
    //  throws ComponentLookupException;
    public void testCheckedToArray() throws ComponentLookupException
    {
        Object[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
            new TestComponent( "first" )
        };
        
        assertTrue( Arrays.equals( test, list.checkedToArray() ) );
    }
    
    public void testCheckedToArray_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedToArray();
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

    //Object[] checkedToArray( Object[] array )
    //  throws ComponentLookupException;
    public void testCheckedToArray_WithInputArray() throws ComponentLookupException
    {
        TestComponent[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
            new TestComponent( "first" )
        };
        
        assertTrue( Arrays.equals( test, (TestComponent[]) list.checkedToArray( new TestComponent[]{} ) ) );
    }
    
    public void testCheckedToArray_WithInputArray_FailOnBadComponentInit()
    {
        ActiveList list = constructKnownBadActiveList();
        
        try
        {
            list.checkedToArray( new TestComponent[]{} );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

}
