package org.codehaus.plexus.logging.log4j;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * A simple configuration:
 * <pre>
 * <configuration>
 *   <threshold>DEBUG</threshold>
 *   <default-appender>file</default-appender>
 *   <appenders>
 *     <appender>
 *       <id>file</id>
 *       <threshold>INFO</threshold>
 *       <type>org.apache.log4j.FileAppender</type>
 *       <conversion-pattern>%-4r [%t] %-5p %c %x - %m%n</conversion-pattern>
 *
 *       <properties>
 *         <property>
 *           <key>file</key>
 *           <value>${plexus.home}/logs/plexus.log</value>
 *         </property>
 *         <property>
 *           <key>append</key>
 *           <value>true</value>
 *         </property>
 *       </properties>
 *     </appender>
 *
 *     <appender>
 *       <id>console</id>
 *       <type>org.apache.log4j.ConsoleAppender</type>
 *       <conversion-pattern>%-4r [%t] %-5p %c %x - %m%n</conversion-pattern>
 *     </appender>
 *
 *     <appender>
 *       <id>rolling</id>
 *       <threshold>DEBUG</threshold>
 *       <type>org.apache.log4j.RollingFileAppender</type>
 *       <conversion-pattern>%-4r [%t] %-5p %c %x - %m%n</conversion-pattern>
 *
 *       <properties>
 *         <property>
 *           <key>file</key>
 *           <value>${plexus.home}/logs/plexus-rolling.log</value>
 *         </property>
 *         <property>
 *           <key>append</key>
 *           <value>true</value>
 *         </property>
 *         <property>
 *           <key>maxBackupIndex</key>
 *           <value>10</value>
 *         </property>
 *         <property>
 *           <key>maxFileSize</key>
 *           <value>20</value>
 *         </property>
 *       </properties>
 *     </appender>
 *   </appenders>
 * </configuration>
 * </pre>
 */
public class Log4JLoggerManager
    extends AbstractLoggerManager
    implements Initializable, Startable
{
    // configuration

    /** The threshold */
    private String threshold;

    /** The default appender id */
    private String defaultAppender;

    /** */
    private List appenders;

    // other private variables
    /** */
    private Map loggerCache;

    /** Log4j properties used to init log4j. */
    private Properties log4JProperties;

    /** */
    private int currentThreshold;

    /**
     * The configuration. This field is set by the container.
     */
    private PlexusConfiguration logging;

    public Log4JLoggerManager()
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle

    public void initialize()
        throws Exception
    {
    	Object obj;
    	Appender appender;
    	String id;
    	Map configuredAppenders;
    	String base;
    	Enumeration e;

        debug( "Initializing." );

        log4JProperties = new Properties();
        configuredAppenders = new HashMap();
        loggerCache = new HashMap();

        if( appenders == null || appenders.size() == 0 )
        {
            if( defaultAppender != null )
                throw new PlexusConfigurationException( "A default appender cant be specified without any appenders configured." );

            defaultAppender = "anonymous";

            if( threshold == null )
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

	        for( int i = 0; i < appenders.size(); i++)
	        {
	        	appender = (Appender)appenders.get( i );

	        	id = appender.getId();

		        if( configuredAppenders.containsKey( id ) )
	        		throw new PlexusConfigurationException( "There already exists a appender with the id '" + id + "'." );

	        	if( id == null )
	        		throw new PlexusConfigurationException( "The appender must have a id." );
	
	        	if( appender.getThreshold() == null)
	        		appender.setThreshold( threshold );
	
	        	if( appender.getConversionPattern() == null )
	        		throw new PlexusConfigurationException( "The appender must have a conversion pattern." );
	
	        	if( appender.getType() == null )
	        		throw new PlexusConfigurationException( "The appender must have a type." );
	
	        	try {
	        		Class.forName( appender.getType() );
	        	}
	        	catch(ClassNotFoundException ex)
				{
	        		throw new PlexusConfigurationException( "Could not find the appender class: " + appender.getType(), ex );
	        	}
	        	catch(LinkageError ex)
				{
	        		throw new PlexusConfigurationException( "Could load the appender class: " + appender.getType(), ex );
	        	}

	        	base = "log4j.appender." + id;
	        	log4JProperties.setProperty( base, appender.getType() );
	        	log4JProperties.setProperty( base + ".threshold", appender.getThreshold() );
	        	log4JProperties.setProperty( base + ".layout", "org.apache.log4j.PatternLayout" );
	        	log4JProperties.setProperty( base + ".layout.conversionPattern", appender.getConversionPattern() );

	        	e = appender.getProperties().keys();
	        	while( e.hasMoreElements() )
	        	{
	        		String key = e.nextElement().toString();

	        		log4JProperties.setProperty( base + "." + key, appender.getProperty( key ) );
	        	}

	        	configuredAppenders.put( id, appender );
	
	        	debug(" Logger #" + i + ": " + appender.getType() );
	        }

            if( defaultAppender == null )
                if( configuredAppenders.size() == 1 )
                    defaultAppender = ((Appender)appenders.get( 0 )).getId();
                else
                    throw new PlexusConfigurationException( "A default appender must be specified when having several appenders." );
	        else if( !configuredAppenders.containsKey( defaultAppender ) )
	        	throw new PlexusConfigurationException( "Could not find the default appender: '" + defaultAppender + "'." );

        }

        if( threshold == null )
            throw new PlexusConfigurationException( "INTERNAL ERROR: The threshold must be set." );
        if( defaultAppender == null )
            throw new PlexusConfigurationException( "INTERNAL ERROR: The default appender must be set." );

        log4JProperties.setProperty( "log4j.rootLogger", threshold + "," + defaultAppender);

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
        debug("stopping");
        debug("stopped");
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoggerManager implementation

    /**
     * Sets the threshold for all new loggers. It will NOT affect the existing loggers.
     *
     * This is usually only set once while the logger manager is configured.
     * 
     * @param threshold The new threshold.
     */
    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    /**
     * Returns the current threshold for all new loggers.
     *
     * @return Returns the current threshold for all new loggers.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    public void setThreshold( String role, String roleHint, int threshold ) {
        Log4JLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Log4JLogger)loggerCache.get( name );

        if(logger == null) {
            debug( "Trying to set the threshold of a unknown logger '" + name + "'." );
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint ) {
        Log4JLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Log4JLogger)loggerCache.get( name );

        if(logger == null) {
            debug( "Trying to get the threshold of a unknown logger '" + name + "'." );
            return Logger.LEVEL_DEBUG; // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger getLoggerForComponent( String role, String roleHint ) 
    {
        Logger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Logger)loggerCache.get( name );

        if ( logger != null )
            return logger;

        debug( "Creating logger '" + name + "' " + this.hashCode() + "." );
        logger = new Log4JLogger( getThreshold(), org.apache.log4j.Logger.getLogger( name ) );
        loggerCache.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        Object obj;
        String name;

        name = toMapKey( role, roleHint );
        obj = loggerCache.remove( name );

        if ( obj == null )
            System.err.println( "There was no such logger '" + name + "' " + this.hashCode() + ".");
        else
            debug( "Removed logger '" + name + "' " + this.hashCode() + ".");
    }

    public int getActiveLoggerCount()
    {
        return loggerCache.size();
    }

    // useful for testing
    public Properties getLog4JProperties()
    {
        return log4JProperties;
    }

    private Properties createRollingFileAppender( String base, String file, String append, 
            String maxBackupIndex, String maxFileSize )
    {
        Properties p = new Properties();

        p.setProperty( base, "org.apache.log4j.RollingFileAppender" );
        p.setProperty( base + ".file", file );
        p.setProperty( base + ".append", append );
        p.setProperty( base + ".MaxBackupIndex", maxBackupIndex );
        p.setProperty( base + ".MaxFileSize", maxFileSize );

        return p;
    }

    private Properties createConsoleAppender( String base )
    {
        Properties p = new Properties();

        p.setProperty( base, "org.apache.log4j.ConsoleAppender" );

        return p;
    }

    private Properties createFileAppender( String base, String file, String append )
    {
        Properties p = new Properties();

        p.setProperty( base, "org.apache.log4j.FileAppender" );
        p.setProperty( base + ".file", file );
        p.setProperty( base + ".append", append );

        return p;
    }

    /**
     * Remove this method and all references when this code is verified.
     *
     * @param msg
     */
    private void debug( String msg )
    {
//        System.out.println( "[Log4j] " + msg );
    }
}
