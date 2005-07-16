package org.codehaus.plexus.scheduler;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.JobListener;
import org.quartz.TriggerListener;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

public class DefaultScheduler
    extends AbstractLogEnabled
    implements Scheduler, Initializable, Startable
{
    private Properties properties;

    private StdScheduler scheduler;

    public void scheduleJob( JobDetail jobDetail, Trigger trigger )
        throws SchedulerException
    {
        scheduler.scheduleJob( jobDetail, trigger );
    }

    public void addGlobalJobListener( JobListener listener )
    {
        scheduler.addGlobalJobListener( listener );
    }

    public void addGlobalTriggerListener( TriggerListener listener )
    {
        scheduler.addGlobalTriggerListener( listener );
    }

    public void initialize()
        throws InitializationException
    {
        try
        {
            SchedulerFactory factory = new StdSchedulerFactory( properties );

            scheduler = (StdScheduler) factory.getScheduler();
        }
        catch ( SchedulerException e )
        {
            throw new InitializationException( "Cannot create scheduler.", e );
        }
    }

    public void start()
        throws StartingException
    {
        try
        {
            scheduler.start();
        }
        catch ( SchedulerException e )
        {
            throw new StartingException( "Cannot start scheduler.", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        scheduler.shutdown();
    }
}
