package org.codehaus.plexus.prompter.console;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

public class LoggerConsoleWriter
    extends AbstractLogEnabled
    implements ConsoleWriter
{
    
    private int logLevel = Logger.LEVEL_INFO;

    public void write( String message )
    {
        writeViaLogger( message );
    }

    public void writeLine( String message )
    {
        writeViaLogger( message + "\n" );
    }

    private void writeViaLogger( String message )
    {
        switch ( logLevel )
        {
            case Logger.LEVEL_INFO:
                getLogger().info( message );
                break;

            case Logger.LEVEL_DEBUG:
                getLogger().debug( message );
                break;

            case Logger.LEVEL_ERROR:
                getLogger().error( message );
                break;

            case Logger.LEVEL_FATAL:
                getLogger().fatalError( message );
                break;

            case Logger.LEVEL_WARN:
                getLogger().warn( message );
                break;

            default:
                getLogger().info( message );
                break;
        }
    }

}
