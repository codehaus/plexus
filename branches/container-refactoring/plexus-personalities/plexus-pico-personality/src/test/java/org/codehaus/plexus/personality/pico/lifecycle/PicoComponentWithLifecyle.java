package org.codehaus.plexus.personality.pico.lifecycle;

import org.picocontainer.Startable;
import org.picocontainer.Disposable;


public class PicoComponentWithLifecyle implements Startable, Disposable
{
    private boolean started;

    private boolean stopped;

    public boolean isStarted()
    {
        return started;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public boolean isDisposed()
    {
        return disposed;
    }

    private boolean disposed;

    public void start()
    {
        System.out.println( "Start" );

        started  = true;
    }

    public void stop()
    {
        System.out.println( "Stop" );

        stopped = true;
    }

    public void dispose()
    {
        System.out.println( "Dispose" );
        
        disposed = true;
    }
}
