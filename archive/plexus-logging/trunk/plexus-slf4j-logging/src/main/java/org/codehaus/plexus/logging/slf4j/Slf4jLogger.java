package org.codehaus.plexus.logging.slf4j;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slf4jLogger 
 * 
 * A logger for those who want to use the <a href="http://www.slf4j.org/">slf4j</a> logging facilities.
 * <p/>
 * The mapping of the logging levels:
 * <ul>
 * <li>Plexus DEBUG &lt;==&gt; Slf4j DEBUG</li>
 * <li>Plexus INFO &lt;==&gt; Slf4j INFO</li>
 * <li>Plexus WARN &lt;==&gt; Slf4j WARN</li>
 * <li>Plexus ERROR &lt;==&gt; Slf4j ERROR</li>
 * <li>Plexus FATAL_ERROR &lt;==&gt; Slf4j ERROR</li>
 * </ul>
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class Slf4jLogger
    extends AbstractLogger
{
    private org.slf4j.Logger logger;

    public Slf4jLogger( int threshold, org.slf4j.Logger logger )
    {
        super( threshold, logger.getName() );
        
        if ( null == logger )
        {
            throw new IllegalArgumentException( "Logger cannot be null" );
        }
        
        this.logger = logger;
    }

    public void debug( String message, Throwable throwable )
    {
        logger.debug( message, throwable );
    }

    public void error( String message, Throwable throwable )
    {
        logger.error( message, throwable );

    }

    public void fatalError( String message, Throwable throwable )
    {
        logger.error( message, throwable );
    }

    public Logger getChildLogger( String name )
    {
        String childName = logger.getName() + "." + name;

        return new Slf4jLogger( getThreshold(), LoggerFactory.getLogger( childName ) );
    }

    public void info( String message, Throwable throwable )
    {
        logger.info( message, throwable );
    }

    public void warn( String message, Throwable throwable )
    {
        logger.warn( message, throwable );
    }
}
