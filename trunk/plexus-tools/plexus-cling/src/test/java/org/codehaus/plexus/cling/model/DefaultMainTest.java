/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DefaultMainTest
    extends TestCase
{

    public void testShouldConstructWithMainClassAndMainMethodStrings()
    {
        DefaultMain main = new DefaultMain( DefaultMain.class.getName(), "getMainMethod" );
    }

    public void testShouldRetrieveMainClass()
    {
        DefaultMain main = new DefaultMain( DefaultMain.class.getName(), "getMainMethod" );

        assertEquals( DefaultMain.class.getName(), main.getMainClass() );
    }

    public void testShouldRetrieveMainMethod()
    {
        DefaultMain main = new DefaultMain( DefaultMain.class.getName(), "getMainMethod" );

        assertEquals( "getMainMethod", main.getMainMethod() );
    }

}