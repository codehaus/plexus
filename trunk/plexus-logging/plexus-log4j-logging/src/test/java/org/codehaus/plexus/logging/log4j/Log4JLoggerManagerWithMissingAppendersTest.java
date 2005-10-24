package org.codehaus.plexus.logging.log4j;

import java.util.Properties;

import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;

public class Log4JLoggerManagerWithMissingAppendersTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager()
        throws Exception
    {
        return (LoggerManager)lookup( LoggerManager.ROLE );
    }

    public void testLog4JMissingAppenders()
        throws Exception
    {
        Log4JLoggerManager loggerManager = (Log4JLoggerManager)createLoggerManager();

        Properties p = loggerManager.getLog4JProperties();

        assertEquals( "org.apache.log4j.ConsoleAppender", p.getProperty( "log4j.appender.anonymous" ) );
        assertEquals( "FATAL", p.getProperty( "log4j.appender.anonymous.threshold" ) );
        assertEquals( "org.apache.log4j.PatternLayout", p.getProperty( "log4j.appender.anonymous.layout" ) );
        assertEquals( "%-4r [%t] %-5p %c %x - %m%n", p.getProperty( "log4j.appender.anonymous.layout.conversionPattern" ) );
        assertEquals( "FATAL,anonymous", p.getProperty( "log4j.rootLogger" ) );
    }
}
