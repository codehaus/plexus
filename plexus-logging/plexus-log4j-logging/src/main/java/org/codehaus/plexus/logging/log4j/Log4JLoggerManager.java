package org.codehaus.plexus.logging.log4j;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.PropertyConfigurator;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
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
 */
public class Log4JLoggerManager
    extends AbstractLog4JLoggerManager
    implements Initializable, Startable
{
    /**
     * The threshold.
     * 
     * @default DEBUG
     */
    private String threshold;

    /**
     * The default appender id
     * 
     * @default console
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

    public void initialize() throws Exception
    {
        debug( "Initializing." );

        log4JProperties = new Properties();

        Map configuredAppenders = new HashMap();

        if ( appenders == null || appenders.size() == 0 )
        {
            if ( defaultAppender != null )
            {
                throw new PlexusConfigurationException( "A default appender cant be specified without any appenders configured." );
            }

            defaultAppender = "anonymous";

            if ( threshold == null )
            {
                threshold = "DEBUG";
            }

            debug( "No appenders configured, creating default console appender." );

            log4JProperties.setProperty( "log4j.appender.anonymous", "org.apache.log4j.ConsoleAppender" );

            log4JProperties.setProperty( "log4j.appender.anonymous.threshold", threshold );

            log4JProperties.setProperty( "log4j.appender.anonymous.layout", "org.apache.log4j.PatternLayout" );

            log4JProperties.setProperty( "log4j.appender.anonymous.layout.conversionPattern", "%-4r [%t] %-5p %c %x - %m%n" );
        }
        else
        {
            debug( " Configuring " + appenders.size() + " appenders." );

            for ( int i = 0; i < appenders.size(); i++ )
            {
                Appender appender = (Appender) appenders.get( i );

                String id = appender.getId();

                if ( configuredAppenders.containsKey( id ) )
                {
                    throw new PlexusConfigurationException( "There already exists a appender with the id '" + id + "'." );
                }

                if ( id == null )
                {
                    throw new PlexusConfigurationException( "The appender must have a id." );
                }

                if ( appender.getThreshold() == null )
                {
                    appender.setThreshold( threshold );
                }

                if ( appender.getConversionPattern() == null )
                {
                    throw new PlexusConfigurationException( "The appender must have a conversion pattern." );
                }

                if ( appender.getType() == null )
                {
                    throw new PlexusConfigurationException( "The appender must have a type." );
                }

                try
                {
                    Class.forName( appender.getType() );
                }
                catch ( ClassNotFoundException ex )
                {
                    throw new PlexusConfigurationException( "Could not find the appender class: " + appender.getType(), ex );
                }
                catch ( LinkageError ex )
                {
                    throw new PlexusConfigurationException( "Could load the appender class: " + appender.getType(), ex );
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
                }

                configuredAppenders.put( id, appender );

                debug( " Logger #" + i + ": " + appender.getType() );
            }

            if ( defaultAppender == null )
            {
                if ( configuredAppenders.size() == 1 )
                {
                    defaultAppender = ((Appender) appenders.get( 0 )).getId();
                }
                else
                {
                    throw new PlexusConfigurationException( "A default appender must be specified when having several appenders." );
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
                        throw new PlexusConfigurationException( "Could not find the default appender: '" + defaultAppender + "'." );
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

        if ( threshold == null )
        {
            throw new PlexusConfigurationException( "INTERNAL ERROR: The threshold must be set." );
        }
        if ( defaultAppender == null )
        {
            throw new PlexusConfigurationException( "INTERNAL ERROR: The default appender must be set." );
        }

        log4JProperties.setProperty( "log4j.rootLogger", threshold + "," + defaultAppender );

        debug( "Initialized." );
    }

    public void start()
        throws Exception
    {
        debug( "Starting." );

        PropertyConfigurator.configure( log4JProperties );

        debug( "Started." );
    }

    public void stop()
        throws Exception
    {
    }

    // useful for testing
    public Properties getLog4JProperties()
    {
        return log4JProperties;
    }

}
