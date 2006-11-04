/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class LocalClasspathEntryTest
    extends TestCase
{

    public void testShouldConstructWithURL() throws MalformedURLException {
        LocalClasspathEntry entry = new LocalClasspathEntry(new URL("http://www.google.com"));
    }
    
    public void testShouldRetrieveURLFromConstructor() throws MalformedURLException {
        URL url = new URL("http://www.google.com");
        
        LocalClasspathEntry entry = new LocalClasspathEntry(url);
        
        assertEquals(url, entry.getURL());
    }
    
}
