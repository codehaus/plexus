package org.codehaus.plexus.logging;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLogger
        implements Logger
{
    private int threshold;

    private String name;

    public AbstractLogger( int threshold, String name )
    {
        if ( !isValidThreshold( threshold ) )
        {
            throw new RuntimeException( "threshold" );
        }

        this.threshold = threshold;
        this.name = name;
    }

    public int getThreshold()
    {
        return threshold;
    }

    public void setThreshold( int threshold )
    {
        this.threshold = threshold;
    }

    public String getName()
    {
        return name;
    }

    public void debug( String message )
    {
        debug( message, null );
    }

    public boolean isDebugEnabled()
    {
        return threshold <= LEVEL_DEBUG;
    }

    public void info( String message )
    {
        info( message, null );
    }

    public boolean isInfoEnabled()
    {
        return threshold <= LEVEL_INFO;
    }

    public void warn( String message )
    {
        warn( message, null );
    }

    public boolean isWarnEnabled()
    {
        return threshold <= LEVEL_WARN;
    }

    public void error( String message )
    {
        error( message, null );
    }

    public boolean isErrorEnabled()
    {
        return threshold <= LEVEL_ERROR;
    }

    public void fatalError( String message )
    {
        fatalError( message, null );
    }

    public boolean isFatalErrorEnabled()
    {
        return threshold <= LEVEL_FATAL;
    }

    protected boolean isValidThreshold( int threshold )
    {
        if ( threshold == LEVEL_DEBUG )
        {
            return true;
        }
        if ( threshold == LEVEL_INFO )
        {
            return true;
        }
        if ( threshold == LEVEL_WARN )
        {
            return true;
        }
        if ( threshold == LEVEL_ERROR )
        {
            return true;
        }
        if ( threshold == LEVEL_FATAL )
        {
            return true;
        }
        if ( threshold == LEVEL_DISABLED )
        {
            return true;
        }

        return false;
    }
}
