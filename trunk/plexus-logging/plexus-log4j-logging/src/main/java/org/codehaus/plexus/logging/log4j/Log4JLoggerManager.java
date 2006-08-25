package org.codehaus.plexus.logging.log4j;

import org.apache.log4j.PropertyConfigurator;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Logger Manager for Log4j logging system which is configurable
 * by plexus services.
 *
 * A simple configuration:
 * 
 * <pre>
 * 
 *  &lt;configuration&gt;
 *    &lt;threshold&gt;DEBUG&lt;/threshold&gt;
 *    &lt;default-appender&gt;file&lt;/default-appender&gt;
 *    &lt;appenders&gt;
 *      &lt;appender&gt;
 *        &lt;id&gt;file&lt;/id&gt;
 *        &lt;threshold&gt;INFO&lt;/threshold&gt;
 *        &lt;type&gt;org.apache.log4j.FileAppender&lt;/type&gt;
 *        &lt;conversion-pattern&gt;%-4r [%t] %-5p %c %x - %m%n&lt;/conversion-pattern&gt;
 * 
 *        &lt;properties&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;file&lt;/key&gt;
 *            &lt;value&gt;${plexus.home}/logs/plexus.log&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;append&lt;/key&gt;
 *            &lt;value&gt;true&lt;/value&gt;
 *          &lt;/property&gt;
 *        &lt;/properties&gt;
 *      &lt;/appender&gt;
 * 
 *      &lt;appender&gt;
 *        &lt;id&gt;console&lt;/id&gt;
 *        &lt;type&gt;org.apache.log4j.ConsoleAppender&lt;/type&gt;
 *        &lt;conversion-pattern&gt;%-4r [%t] %-5p %c %x - %m%n&lt;/conversion-pattern&gt;
 *      &lt;/appender&gt;
 * 
 *      &lt;appender&gt;
 *        &lt;id&gt;rolling&lt;/id&gt;
 *        &lt;threshold&gt;DEBUG&lt;/threshold&gt;
 *        &lt;type&gt;org.apache.log4j.RollingFileAppender&lt;/type&gt;
 *        &lt;conversion-pattern&gt;%-4r [%t] %-5p %c %x - %m%n&lt;/conversion-pattern&gt;
 * 
 *        &lt;properties&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;file&lt;/key&gt;
 *            &lt;value&gt;${plexus.home}/logs/plexus-rolling.log&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;append&lt;/key&gt;
 *            &lt;value&gt;true&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;maxBackupIndex&lt;/key&gt;
 *            &lt;value&gt;10&lt;/value&gt;
 *          &lt;/property&gt;
 *          &lt;property&gt;
 *            &lt;key&gt;maxFileSize&lt;/key&gt;
 *            &lt;value&gt;20&lt;/value&gt;
 *          &lt;/property&gt;
 *        &lt;/properties&gt;
 *      &lt;/appender&gt;
 *    &lt;/appenders&gt;
 *  &lt;/configuration&gt;
 *  
 * </pre>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Log4JLoggerManager
    extends AbstractLog4JLoggerManager
    implements Startable
{
    /**
     * The threshold.
     *
     * @default DEBUG
     * @todo this fields resides in the class which is other module ...
     *        what shoud we do in such cases?
     */
    //private String threshold;

    /**
     * The default appender id.
     * 
     * @todo it is not that obvious what's deafult appender
     * as deafult appender is null and we can change it
     * "anonymous" under certain circumstances.
     * That's probably good example which highlights
     * what can be supported by CDC.
     *
     *
     * @default  null/anonymous
     *
     */
    private String defaultAppender;

    /**
     * @default
     */
    private List appenders;

    /**
     * @default
     */
    private List levels;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------


    /** Log4j properties used to init log4j. */
    private Properties log4JProperties;


    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
        super.initialize();

        log4JProperties = new Properties();

        Map configuredAppenders = new HashMap();

        if ( appenders == null || appenders.size() == 0 )
        {
            if ( defaultAppender != null )
            {
                throw new IllegalArgumentException( "A default appender cant be specified without any appenders configured." );
            }

            defaultAppender = "anonymous";

            log4JProperties.setProperty( "log4j.appender.anonymous", "org.apache.log4j.ConsoleAppender" );

            log4JProperties.setProperty( "log4j.appender.anonymous.threshold", getThresholdAsString() );

            log4JProperties.setProperty( "log4j.appender.anonymous.layout", "org.apache.log4j.PatternLayout" );

            log4JProperties.setProperty( "log4j.appender.anonymous.layout.conversionPattern", "%-4r [%t] %-5p %c %x - %m%n" );
        }
        else
        {
            for ( int i = 0; i < appenders.size(); i++ )
            {
                Appender appender = (Appender) appenders.get( i );

                String id = appender.getId();

                if ( configuredAppenders.containsKey( id ) )
                {
                    throw new IllegalArgumentException( "There already exists a appender with the id '" + id + "'." );
                }

                if ( id == null )
                {
                    throw new IllegalArgumentException( "The appender must have a id." );
                }

                if ( appender.getThreshold() == null )
                {
                    appender.setThreshold( getThresholdAsString() );
                }

                if ( appender.getConversionPattern() == null )
                {
                    throw new IllegalArgumentException( "The appender must have a conversion pattern." );
                }

                if ( appender.getType() == null )
                {
                    throw new IllegalArgumentException( "The appender must have a type." );
                }

                try
                {
                    Class.forName( appender.getType() );
                }
                catch ( ClassNotFoundException ex )
                {
                    IllegalArgumentException e = new IllegalArgumentException( "Could not find the appender class: " + appender.getType() );
                    e.initCause( ex );
                    throw e;
                }
                catch ( LinkageError ex )
                {
                    IllegalArgumentException e = new IllegalArgumentException( "Could load the appender class: " + appender.getType() );
                    e.initCause( ex );
                    throw e;
                }

                String base = "log4j.appender." + id;

                log4JProperties.setProperty( base, appender.getType() );

                log4JProperties.setProperty( base + ".threshold", appender.getThreshold() );

                log4JProperties.setProperty( base + ".layout", "org.apache.log4j.PatternLayout" );

                log4JProperties.setProperty( base + ".layout.conversionPattern", appender.getConversionPattern() );

                Enumeration e = appender.getProperties().keys();

                while ( e.hasMoreElements() )
                {
                    String key = e.nextElement().toString();

                    log4JProperties.setProperty( base + "." + key, appender.getProperty( key ) );

                    if ( "file".equals( key ) )
                    {
                        File logFile = new File( appender.getProperty( key ) );
                        File logDir = logFile.getParentFile();

                        if ( ! logDir.exists() )
                        {
                            logDir.mkdirs();
                        }
                    }
                }

                configuredAppenders.put( id, appender );
            }

            if ( defaultAppender == null )
            {
                if ( configuredAppenders.size() == 1 )
                {
                    defaultAppender = ((Appender) appenders.get( 0 )).getId();
                }
                else
                {
                    throw new IllegalArgumentException( "A default appender must be specified when having several appenders." );
                }
            }
            else
            {
                StringTokenizer tokenizer = new StringTokenizer( defaultAppender, "," );

                while ( tokenizer.hasMoreTokens() )
                {
                    String appender = tokenizer.nextToken();

                    if ( !configuredAppenders.containsKey( appender ) )
                    {
                        throw new IllegalArgumentException( "Could not find the default appender: '" + defaultAppender + "'." );
                    }
                }
            }
        }

        if ( levels != null && levels.size() > 0 )
        {
            for ( Iterator it = levels.iterator(); it.hasNext(); )
            {
                Level level = (Level) it.next();

                log4JProperties.put( "log4j.logger." + level.getHierarchy(), level.getLevel() );
            }
        }

        if ( getThresholdAsString() == null )
        {
            throw new IllegalArgumentException( "INTERNAL ERROR: The threshold must be set." );
        }
        if ( defaultAppender == null )
        {
            throw new IllegalArgumentException( "INTERNAL ERROR: The default appender must be set." );
        }

        log4JProperties.setProperty( "log4j.rootLogger", getThresholdAsString() + "," + defaultAppender );

    }

    public void start()
        throws StartingException
    {
        PropertyConfigurator.configure( log4JProperties );
    }

    public void stop()
        throws StoppingException
    {
    }

    // useful for testing
    public Properties getLog4JProperties()
    {
        return log4JProperties;
    }
   
}
