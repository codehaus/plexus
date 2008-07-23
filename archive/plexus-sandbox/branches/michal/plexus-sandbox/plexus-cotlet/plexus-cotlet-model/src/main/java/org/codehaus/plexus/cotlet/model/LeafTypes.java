/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.cotlet.model;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class LeafTypes
{
    private static Set typeSet = new HashSet();

    static
    {
        typeSet.add( int.class.getName() );

        typeSet.add( Integer.class.getName() );

        typeSet.add( float.class.getName() );

        typeSet.add( Float.class.getName() );

        typeSet.add( double.class.getName() );

        typeSet.add( Double.class.getName() );

        typeSet.add( String.class.getName() );

        typeSet.add( Properties.class.getName() );

        typeSet.add( boolean.class.getName() );

        typeSet.add( Boolean.class.getName() );
    }


    public static boolean contains( String type )
    {
        boolean retValue = typeSet.contains( type );

        return retValue;
    }

}
