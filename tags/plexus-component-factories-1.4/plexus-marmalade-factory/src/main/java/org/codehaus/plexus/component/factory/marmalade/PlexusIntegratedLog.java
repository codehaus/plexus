package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.marmalade.monitor.log.CommonLogLevels;
import org.codehaus.marmalade.monitor.log.LogSupport;
import org.codehaus.marmalade.monitor.log.MarmaladeLog;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.util.Arrays;
import java.util.List;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

public class PlexusIntegratedLog
    extends AbstractLogEnabled
    implements MarmaladeLog
{

    protected boolean enabled( String level )
    {
        Logger logger = getLogger();

        if ( CommonLogLevels.DEBUG.equals( level ) )
        {
            return logger.isDebugEnabled();
        }
        else if ( CommonLogLevels.ERROR.equals( level ) )
        {
            return logger.isErrorEnabled();
        }
        else if ( CommonLogLevels.INFO.equals( level ) )
        {
            return logger.isInfoEnabled();
        }
        else if ( CommonLogLevels.WARN.equals( level ) )
        {
            return logger.isWarnEnabled();
        }
        else
        {
            // if it doesn't match one of these, then I guess we'll let it through on INFO.
            return logger.isInfoEnabled();
        }
    }

    public void log( String level, CharSequence content )
    {
        Logger logger = getLogger();

        if ( CommonLogLevels.DEBUG.equals( level ) )
        {
            logger.debug( content.toString() );
        }
        else if ( CommonLogLevels.ERROR.equals( level ) )
        {
            logger.error( content.toString() );
        }
        else if ( CommonLogLevels.INFO.equals( level ) )
        {
            logger.info( content.toString() );
        }
        else if ( CommonLogLevels.WARN.equals( level ) )
        {
            logger.warn( content.toString() );
        }
        else
        {
            logger.info( content.toString() );
        }
    }

    public void log( String level, CharSequence content, Throwable error )
    {
        List entries = Arrays.asList( new Object[] { content, error } );

        log( level, entries );
    }

    public void log( String level, Throwable error )
    {
        Logger logger = getLogger();

        if ( CommonLogLevels.DEBUG.equals( level ) )
        {
            logger.debug( LogSupport.formatThrowable( error ).toString() );
        }
        else if ( CommonLogLevels.ERROR.equals( level ) )
        {
            logger.error( LogSupport.formatThrowable( error ).toString() );
        }
        else if ( CommonLogLevels.INFO.equals( level ) )
        {
            logger.info( LogSupport.formatThrowable( error ).toString() );
        }
        else if ( CommonLogLevels.WARN.equals( level ) )
        {
            logger.warn( LogSupport.formatThrowable( error ).toString() );
        }
        else
        {
            logger.info( LogSupport.formatThrowable( error ).toString() );
        }
    }

    public void log( String level, List contentList )
    {
        Logger logger = getLogger();

        if ( CommonLogLevels.DEBUG.equals( level ) )
        {
            logger.debug( LogSupport.formatEntryList( contentList ).toString() );
        }
        else if ( CommonLogLevels.ERROR.equals( level ) )
        {
            logger.error( LogSupport.formatEntryList( contentList ).toString() );
        }
        else if ( CommonLogLevels.INFO.equals( level ) )
        {
            logger.info( LogSupport.formatEntryList( contentList ).toString() );
        }
        else if ( CommonLogLevels.WARN.equals( level ) )
        {
            logger.warn( LogSupport.formatEntryList( contentList ).toString() );
        }
        else
        {
            logger.info( LogSupport.formatEntryList( contentList ).toString() );
        }
    }

}
