package org.codehaus.plexus.logging.log4j;

import java.util.Properties;

import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;

public class Log4JLoggerManagerWithFullConfigTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager)lookup( LoggerManager.ROLE );
    }

    public void testLog4JLoggerManagerWithFullConfig()
        throws Exception
    {
        Log4JLoggerManager loggerManager = (Log4JLoggerManager)createLoggerManager();

        Properties p = loggerManager.getLog4JProperties();

        assertEquals( "DEBUG,file", p.getProperty( "log4j.rootLogger" ) );

        // test the file appender config
        assertEquals( "org.apache.log4j.FileAppender", p.getProperty( "log4j.appender.file" ) );
        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.file.layout" ) );
        assertEquals( "true", p.getProperty( "log4j.appender.file.append" ) );
        assertEquals( getContainer().getContext().get("plexus.home") + "/logs/plexus.log", p.getProperty( "log4j.appender.file.file" ) );
        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.file.layout" ) );
        assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.file.layout.conversionPattern" ) );
        assertEquals( "INFO", p.getProperty( "log4j.appender.file.threshold" ) );

        // testing the console appender config
        assertEquals( "org.apache.log4j.ConsoleAppender", p.getProperty( "log4j.appender.console" ) );
        assertEquals( "DEBUG", p.getProperty( "log4j.appender.console.threshold" ) );
        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.console.layout" ) );
        assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.console.layout.conversionPattern" ) );

        // testing the rolling file appender config
		assertEquals( "org.apache.log4j.RollingFileAppender", p.getProperty( "log4j.appender.rolling" ) );
		assertEquals( "true", p.getProperty( "log4j.appender.rolling.append" ) );
		assertEquals( getContainer().getContext().get("plexus.home") + "/logs/plexus-rolling.log", p.getProperty( "log4j.appender.rolling.file" ) );
		assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.rolling.layout" ) );
		assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.rolling.layout.conversionPattern" ) );
		assertEquals( "10", p.getProperty( "log4j.appender.rolling.maxBackupIndex" ) );
		assertEquals( "20", p.getProperty( "log4j.appender.rolling.maxFileSize" ) );
		assertEquals( "DEBUG", p.getProperty( "log4j.appender.rolling.threshold" ) );
    }
}
