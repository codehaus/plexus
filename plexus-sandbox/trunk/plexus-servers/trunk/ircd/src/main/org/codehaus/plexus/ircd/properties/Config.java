/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.properties;

import java.util.ResourceBundle;

public class Config
{

    private static ResourceBundle allResources;

    static
    {
        init();
    }

    /**
     * to get a specific value from the config
     * @param key the key of the value to get from the configuration
     * @return the requested value
     */
    public static String getValue( String key )
    {
        return getValue( key, null );
    }

    /**
     * to get a specific value from the config
     * @param key the key of the element to get from the configuration
     * @param defaultValue the default value given
     * @return the requested value
     */
    public static String getValue( String key, String defaultValue )
    {
        try
        {
            return allResources.getString( key );
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }

    /**
     * to initialize the resource bundle
     */
    private static void init()
    {
        allResources = ResourceBundle.getBundle( "org.codehaus.plexus.ircd.properties.irc" );
    }
}
