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
        DefaultMain main = new DefaultMain();
    }

    public void testShouldSetAndRetrieveMainClass()
    {
        DefaultMain main = new DefaultMain( );
        
        main.setMainClass(DefaultMain.class.getName());

        assertEquals( DefaultMain.class.getName(), main.getMainClass() );
    }

    public void testShouldSetAndRetrieveMainMethod()
    {
        DefaultMain main = new DefaultMain();
        
        main.setMainMethod("getMainMethod");

        assertEquals( "getMainMethod", main.getMainMethod() );
    }

}