package org.codehaus.plexus.logging.log4j;

/**
 * Logger which delgates to logger from Log4j framwork.
 *
 * @author Jason van Zyl
 */
class EmbeddedLog4JLogger
    extends org.apache.log4j.Logger
    implements org.codehaus.plexus.logging.Logger
{
    public EmbeddedLog4JLogger( String string )
    {
        super( string );
    }

    public void debug( String message )
    {
        super.debug( message );
    }

    public void debug( String message, Throwable throwable )
    {
        super.debug( message, throwable );
    }

    public void info( String message )
    {
        super.info( message );
    }

    public void info( String message, Throwable throwable )
    {
        super.info( message, throwable );
    }

    public boolean isWarnEnabled()
    {
        return false;
    }

    public void warn( String message )
    {
        super.warn( message );
    }

    public void warn( String message, Throwable throwable )
    {
        super.warn( message, throwable );
    }

    public boolean isErrorEnabled()
    {
        return false;
    }

    public void error( String message )
    {
        super.error( message );
    }

    public void error( String message, Throwable throwable )
    {
        super.error( message, throwable );
    }

    public boolean isFatalErrorEnabled()
    {
        return false;
    }
    
    public void fatalError( String message )
    {
        super.fatal( message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        super.fatal( message, throwable );
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public org.codehaus.plexus.logging.Logger getChildLogger( String s )
    {
        return null;
    }

    public int getThreshold()
    {
        return 0;
    }

    public void setThreshold( int threshold )
    {
       throw new UnsupportedOperationException( );
    }
}
