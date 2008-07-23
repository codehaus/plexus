/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.meridian;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class MeridianInterpolationHandler
    implements InterpolationHandler
{
    private Map variables;

    public MeridianInterpolationHandler()
    {
        variables = new HashMap();

        variables.put( "value", "beep" );

        variables.put( "this", "jason" );
    }

    public void interpolate( String key, Writer out )
        throws IOException
    {
        if ( key.startsWith( ":" ) )
        {
            DefaultMeridian.processTemplate( "bottom.txt", out );
        }
        else
        {
            interpolateContextValue( key, out );
        }
    }

    protected void interpolateContextValue( String key, Writer out )
        throws IOException
    {
        Object o = variables.get( key );

        if ( o == null )
        {
            out.write( key );
        }
        else
        {
            out.write( o.toString() );
        }
    }
}
