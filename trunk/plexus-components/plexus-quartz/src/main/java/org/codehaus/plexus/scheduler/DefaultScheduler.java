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
        if ( jobDetail == null || jobDetail.getName() == null )
        {
            throw new SchedulerException( "No job or no job name - cannot schedule this job" );
        }

        if ( jobExists( jobDetail.getName(), jobDetail.getGroup() ) )
        {
            getLogger().warn( "Will not schedule this job as a job {" + jobDetail.getName() + ":" +
                              jobDetail.getGroup() + "} already exists." );

            return;
        }

        try
        {
            scheduler.scheduleJob( jobDetail, trigger );
        }
        catch ( SchedulerException e )
        {
            throw new SchedulerException( "Error scheduling job.", e );
        }
        catch ( Exception e )
        {
            throw new SchedulerException( "Error scheduling job (Verify your cron expression).", e );
        }
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

    public void unscheduleJob( String jobName, String groupName )
        throws SchedulerException
    {
        if ( jobName == null ) 
        {
            throw new SchedulerException( "Job name null - cannot unschedule job" );
        }

        try
        {
            if ( jobExists( jobName, groupName ) )
            {
                scheduler.deleteJob( jobName, groupName );
            }
        }
        catch ( SchedulerException e )
        {
            throw new SchedulerException( "Error unscheduling job.", e );
        }
    }

    public boolean interruptSchedule( String jobName, String groupName )
        throws SchedulerException
    {
        try
        {
            return scheduler.interrupt( jobName, groupName );
        }
        catch ( Exception e )
        {
            throw new SchedulerException( "Can't interrup job \"" + jobName + "\".", e );
        }
    }

    private boolean jobExists( String jobName, String jobGroup )
        throws SchedulerException
    {
        String[] jobNames;

        try
        {
            jobNames = scheduler.getJobNames( jobGroup );
        }
        catch ( SchedulerException e )
        {
            throw new SchedulerException( "Error getting job.", e );
        }

        for ( int i = 0; i < jobNames.length; i++ )
        {
            String name = jobNames[i];

            if ( jobName.equals( name ) )
            {
                return true;
            }
        }

        return false;
    }

    public StdScheduler getScheduler()
    {
        return scheduler;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    public Properties getProperties()
    {
        return properties;
    }
}
