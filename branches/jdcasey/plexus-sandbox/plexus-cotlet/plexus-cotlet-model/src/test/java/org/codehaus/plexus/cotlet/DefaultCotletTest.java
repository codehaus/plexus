/*
 * $RCSfile$
 *
 * Copyright 2000 by Informatique-MTF, SA,
 * CH-1762 Givisiez/Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 */
package org.codehaus.plexus.cotlet;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.cotlet.model.CotletModel;
import org.codehaus.plexus.cotlet.writer.CotletModelWriter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

public class DefaultCotletTest extends PlexusTestCase
{
    private Cotlet cotlet;

    private CotletModelWriter writer;

    public DefaultCotletTest( String testname )
    {
        super( testname );

    }


    protected void setUp() throws Exception
    {
        super.setUp();

        cotlet = ( Cotlet ) lookup( Cotlet.ROLE );

        writer = ( CotletModelWriter ) lookup( CotletModelWriter.ROLE );
    }

    public void testCotlet() throws Exception
    {

        File[] directories = new File[ 1 ];

        File src = new File( "target/test-classes/test-project" );

        System.out.println( "src: " + src );

        directories[ 0 ] = src;

        CotletModel cotletModel = cotlet.buildModel( directories );

        String out = writer.writeModel( cotletModel, "templates/components-docs.vm" );

        File dest = new File( "target/generated-xdocs/components-docs.xml" );

        dest.getParentFile().mkdirs();

        FileUtils.fileWrite( dest.getPath(), out );

    }

}
