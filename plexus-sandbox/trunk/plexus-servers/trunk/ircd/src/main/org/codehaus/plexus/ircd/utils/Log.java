/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.utils;

import org.codehaus.plexus.ircd.properties.Config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{

    private static Logger log;

    static
    {
        init();
    }

    /**
     * to initialize the logger
     */
    private static void init()
    {
        log = Logger.getLogger( Config.getValue( "log.logger.name" ) );
        log.setLevel( Level.parse( Config.getValue( "log.level" ) ) );
        try
        {
            log.addHandler( new FileHandler( Config.getValue( "log.file.pathname" ) ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    /**
     * to add a message in the log
     * @param level the log's level
     * @param sourceClass the name of the source class
     * @param sourceMethod the name of the source method
     * @param msg the message to insert
     */
    public static void log( Level level, String sourceClass, String sourceMethod, String msg )
    {
        log.logp( level, sourceClass, sourceMethod, msg );
    }

    /**
     * to add a message in the log with a specific exception
     * @param level the log's level
     * @param sourceClass the name of the source class
     * @param sourceMethod the name of the source method
     * @param msg the message to insert
     * @param thrown the corresponding exception
     */
    public static void log( Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown )
    {
        log.logp( level, sourceClass, sourceMethod, msg, thrown );
    }
}
