package org.codehaus.plexus.jetty;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.mortbay.util.LogSink;
import org.mortbay.util.Frame;
import org.mortbay.util.Log;

/**
 * Jetty Log redirection
 *
 *
 * @see <a href="http://jetty.mortbay.com/">Jetty Project Page</a>
 *
 * @author  Bruno Dumon & Paul Hammant
 * @version 1.0
 */
public class PlexusLogSink
    extends AbstractLogEnabled
    implements LogSink
{
    public void stop()
        throws InterruptedException
    {
    }

    public boolean isStarted()
    {
        return true;
    }

    public void setOptions(String s)
    {
    }

    public String getOptions()
    {
        return "";
    }

    public void log(String type, Object message, Frame frame, long time)
    {
        if (type.equals(Log.DEBUG))
        {
            getLogger().info( message + "" );
        }
        else if (type.equals(Log.FAIL))
        {
            getLogger().error( message + "" );
        }
        else if (type.equals(Log.WARN))
        {
            getLogger().warn( message + "" );
        }
        else if (type.equals(Log.ASSERT))
        {
            getLogger().info( message + "" );
        }
        else
        {
            getLogger().info( message + "" );
        }
    }

    public void log(String message)
    {
        getLogger().info(message);
    }

    public void start() throws Exception
    {
    }
}
