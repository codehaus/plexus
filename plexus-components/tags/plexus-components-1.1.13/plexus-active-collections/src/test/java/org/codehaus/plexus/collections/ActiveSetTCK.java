package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class ActiveSetTCK
    extends PlexusTestCase
{

    private ActiveSet set;

    protected abstract String getTestSetRoleHint();

    protected abstract ActiveSet constructKnownBadActiveSet();

    public void setUp()
        throws Exception
    {
        super.setUp();

        set = (ActiveSet) lookup( ActiveSet.ROLE, getTestSetRoleHint() );
    }

    //    public boolean add( Object arg0 )
    //    {
    //        throw new UnsupportedOperationException( "ActiveList implementations are not mutable." );
    //    }
    public void testAdd_ShouldThrowUnsupportedOperationException()
    {
        try
        {
            set.add( "test" );

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
            set.addAll( Collections.singletonList( "test" ) );

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
            set.clear();

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
            set.remove( "test" );

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
            set.removeAll( Collections.singletonList( "test" ) );

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
            set.retainAll( Collections.singletonList( "test" ) );

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
        assertTrue( set.contains( new TestComponent( "first" ) ) );
        assertFalse( set.contains( new TestComponent( "one thousandth" ) ) );
    }

    //boolean containsAll( Collection collection );
    public void testContainsAll()
    {
        Set containsTest = new HashSet( 2 );
        containsTest.add( new TestComponent( "first" ) );
        containsTest.add( new TestComponent( "second" ) );
        
        Set doesntContainTest = Collections.singleton( new TestComponent( "one thousandth" ) );
        
        assertTrue( set.containsAll( containsTest ) );
        assertFalse( set.containsAll( doesntContainTest ) );
    }
    
    //    public Iterator iterator();
    public void testIterator()
    {
        Iterator it = set.iterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertFalse( it.hasNext() );
    }

    //Object[] toArray();
    public void testToArray()
    {
        Object[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
        };
        
        assertTrue( Arrays.equals( test, set.toArray() ) );
    }

    //Object[] toArray( Object[] array );
    public void testToArray_WithInputArray()
    {
        TestComponent[] test = {
            new TestComponent( "first" ),
            new TestComponent( "second" ),
        };
        
        assertTrue( Arrays.equals( test, (TestComponent[]) set.toArray( new TestComponent[]{} ) ) );
    }

    //boolean checkedContains( Object value )
    //  throws ComponentLookupException;
    public void testCheckedContains() throws ComponentLookupException
    {
        assertTrue( set.checkedContains( new TestComponent( "first" ) ) );
        assertFalse( set.checkedContains( new TestComponent( "one thousandth" ) ) );
    }
    
    public void testCheckedContains_FailOnBadComponentInit()
    {
        ActiveSet list = constructKnownBadActiveSet();
        
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
        
        assertTrue( set.checkedContainsAll( containsTest ) );
        assertFalse( set.checkedContainsAll( doesntContainTest ) );
    }
    
    public void testCheckedContainsAll_FailOnBadComponentInit()
    {
        ActiveSet set = constructKnownBadActiveSet();
        
        try
        {
            set.checkedContainsAll( Collections.singletonList( TestBadComponent.newTestInstance() ) );
            
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
        Iterator it = set.checkedIterator();
        
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "first" ), it.next() );
        assertTrue( it.hasNext() );
        assertEquals( new TestComponent( "second" ), it.next() );
        assertFalse( it.hasNext() );
    }
    
    public void testCheckedIterator_FailOnBadComponentInit()
    {
        ActiveSet set = constructKnownBadActiveSet();
        
        try
        {
            set.checkedIterator();
            
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
        };
        
        assertTrue( Arrays.equals( test, set.checkedToArray() ) );
    }
    
    public void testCheckedToArray_FailOnBadComponentInit()
    {
        ActiveSet list = constructKnownBadActiveSet();
        
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
        };
        
        assertTrue( Arrays.equals( test, (TestComponent[]) set.checkedToArray( new TestComponent[]{} ) ) );
    }
    
    public void testCheckedToArray_WithInputArray_FailOnBadComponentInit()
    {
        ActiveSet set = constructKnownBadActiveSet();
        
        try
        {
            set.checkedToArray( new TestComponent[]{} );
            
            fail( "Should throw ComponentLookupException when one or more components fails to load." );
        }
        catch ( ComponentLookupException e )
        {
            // expected
        }
    }

}
