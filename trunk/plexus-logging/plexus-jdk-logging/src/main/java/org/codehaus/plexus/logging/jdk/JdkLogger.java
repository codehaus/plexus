package org.codehaus.plexus.logging.jdk;

/*
 * Copyright (C) The JContainer Group. All rights reserved.
 *
 * This software is published under the terms of the JContainer
 * Software License version 1.1, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 */

import java.util.logging.Level;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

/**
 * A logger for those who want to use the java 1.4+ logging facilities.
 * <p/>
 * The mapping of the logging levels:
 * <ul>
 * <li>LEVEL_DEBUG &lt;==&gt; Level.CONFIG</li>
 * <li>LEVEL_DEBUG &lt;==&gt; Level.ALL</li>
 * <li>LEVEL_DEBUG &lt;==&gt; Level.FINEST</li>
 * <li>LEVEL_DEBUG &lt;==&gt; Level.FINER</li>
 * <li>LEVEL_DEBUG &lt;==&gt; Level.FINE</li>
 * <li>LEVEL_INFO &lt;==&gt; Level.INFO</li>
 * <li>LEVEL_WARN &lt;==&gt; Level.WARNING</li>
 * <li>LEVEL_ERROR &lt;==&gt; Level.SEVERE</li>
 * <li>LEVEL_OFF &lt;==&gt; Level.OFF</li>
 * </ul>
 * I don't know if debug is the right level for config -- Trygve.
 *
 * @author <a href="www.jcontainer.org">The JContainer Group</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Revision$ $Date$
 */
public class JdkLogger
    extends AbstractLogger
{
    private java.util.logging.Logger logger;

    /**
     *
     * @param logger
     * @throws IllegalArgumentException
     */
    public JdkLogger( int threshold, java.util.logging.Logger logger )
    {
        super( threshold, logger.getName() );

        if ( null == logger )
        {
            throw new IllegalArgumentException( "Logger cannot be null" );
        }

        this.logger = logger;
    }

    public void trace( String message )
    {
        logger.log( Level.FINEST, message );
    }

    public void trace( String message, Throwable throwable )
    {
        logger.log( Level.FINEST, message, throwable );
    }

    public void debug( String message )
    {
        logger.log( Level.FINE, message );
    }

    public void debug( String message, Throwable throwable )
    {
        logger.log( Level.FINE, message, throwable );
    }

    public void info( String message )
    {
        logger.log( Level.INFO, message );
    }

    public void info( String message, Throwable throwable )
    {
        logger.log( Level.INFO, message, throwable );
    }

    public void warn( String message )
    {
        logger.log( Level.WARNING, message );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.log( Level.WARNING, message, throwable );
    }

    public void fatalError( String message )
    {
        error( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        error( message, throwable );
    }

    public void error( String message )
    {
        logger.log( Level.SEVERE, message );
    }

    public void error( String message, Throwable throwable )
    {
        logger.log( Level.SEVERE, message, throwable );
    }

    public Logger getChildLogger( String name )
    {
        String childName = logger.getName() + "." + name;

        return new JdkLogger( getThreshold(),  java.util.logging.Logger.getLogger( childName ) );
    }
}
