package org.codehaus.plexus.hibernate.persister;

import junit.framework.TestCase;

/**
 * PersisterTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PersisterTest
    extends TestCase
{
	public class PersistableA
        extends AbstractPersistable
    {
    }
    
    public void testEquivalence()
    {
        PersistableA a1 = new PersistableA();
        a1.setId(10);
        
        PersistableA a2 = new PersistableA();
        a2.setId(10);
        
        PersistableA a3 = new PersistableA();
        a3.setId(13);
        
        assertEquals(a1.hashCode(), a2.hashCode());
        assertEquals(a1, a2);
        assertEquals(a2, a1);
        
        assertNotSame( a1, a3 );
        assertNotSame( a3, a1 );
        
        assertTrue( a1.hashCode() != a3.hashCode() );
    }
}
