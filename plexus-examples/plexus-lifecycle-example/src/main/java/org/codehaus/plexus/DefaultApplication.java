package org.codehaus.plexus;

public class DefaultApplication
    implements Application
{
    private Populator populator;

    private boolean workDone;

    // ----------------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------------

    public Populator getPopulator()
    {
        return populator;
    }

    public void setPopulator( Populator populator )
    {
        this.populator = populator;
    }

    public boolean isWorkDone()
    {
        return workDone;
    }

    // ----------------------------------------------------------------------------
    // Interface
    // ----------------------------------------------------------------------------

    public void doWork()
    {
        workDone = true;
    }
}
