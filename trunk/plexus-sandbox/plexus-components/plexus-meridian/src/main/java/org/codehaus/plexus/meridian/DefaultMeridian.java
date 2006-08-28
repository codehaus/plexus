/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.meridian;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultMeridian
    implements Meridian
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public void render( String template, Writer writer )
        throws IOException
    {
        processTemplate( template, writer );

        writer.flush();

        writer.close();
    }

    public static void processTemplate( String template, Writer writer )
        throws IOException
    {
        InterpolationHandler h = new MeridianInterpolationHandler();

        InterpolationFilterWriter out = new InterpolationFilterWriter( writer, h, "${", "}" );

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( template );

        Reader reader = new InputStreamReader( is );

        copy( reader, out );
    }

    public static void copy( Reader input, Writer output )
        throws IOException
    {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];

        int n = 0;

        while ( -1 != ( n = input.read( buffer ) ) )
        {
            output.write( buffer, 0, n );
        }
    }
}
