/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import java.util.List;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DefaultClasspathTest
    extends MockObjectTestCase
{

    public void testShouldConstructWithNoArgs() {
        DefaultClasspath cp = new DefaultClasspath();
    }
    
    public void testShouldAddOneEntryAndRetrieveEntryListOfSizeOne() {
        DefaultClasspath cp = new DefaultClasspath();
        
        Mock cpeMock = mock(ClasspathEntry.class);
        ClasspathEntry cpe = (ClasspathEntry)cpeMock.proxy();
        
        cp.addEntry(cpe);
        
        List entries = cp.getEntries();
        assertNotNull("entries is null", entries);
        assertEquals("entries size is not 1", 1, entries.size());
        assertTrue("entries doesn't contain added entry", entries.contains(cpe));
    }
    
}
