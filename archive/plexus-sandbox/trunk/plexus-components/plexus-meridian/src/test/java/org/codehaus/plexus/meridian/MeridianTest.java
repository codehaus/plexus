/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.meridian;

import junit.framework.TestCase;

import java.io.FileWriter;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class MeridianTest
    extends TestCase
{
    public void testMeridian()
        throws Exception
    {
        Meridian m = new DefaultMeridian();

        FileWriter w = new FileWriter( "output.txt" );

        m.render( "front.txt", w );
    }
}
