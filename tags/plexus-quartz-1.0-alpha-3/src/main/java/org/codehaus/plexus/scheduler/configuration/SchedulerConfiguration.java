package org.codehaus.plexus.scheduler.configuration;

import org.codehaus.plexus.scheduler.Scheduler;
import org.quartz.spi.JobStore;
import org.quartz.spi.ThreadPool;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.SchedulingContext;
import org.quartz.utils.DBConnectionManager;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * Class to represent the configuration file for the proxy
 *
 * @author John Tolentino
 * @plexus.component role="org.codehaus.plexus.scheduler.configuration.SchedulerConfiguration"
 */
public class SchedulerConfiguration
{
    public static final String ROLE = SchedulerConfiguration.class.getName();

    /*
     * @plexus.required
     */
    private Scheduler plexusScheduler;

    public String getInstanceName()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME );
    }

    public void setInstanceName( String instanceName )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, instanceName );
    }

    public String getInstanceId()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_INSTANCE_ID );
    }

    public void setInstanceId( String InstanceId )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_INSTANCE_ID, InstanceId );
    }

    public String getThreadName()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_THREAD_NAME );
    }

    public void setThreadName( String threadName )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_THREAD_NAME, threadName );
    }

    public String getIdleWaitTime()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_IDLE_WAIT_TIME );
    }

    public void setIdleWaitTime( String idleWaitTime )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_IDLE_WAIT_TIME, idleWaitTime );
    }

    public String getDbFailureRetryInterval()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_DB_FAILURE_RETRY_INTERVAL );
    }

    public void setDbFailureRetryInterval( String dbFailureRetryInterval )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_DB_FAILURE_RETRY_INTERVAL, dbFailureRetryInterval );
    }

    public String getClassLoadHelper()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_CLASS_LOAD_HELPER_CLASS );
    }

    public void setClassLoadHelper( String classLoadHelper )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_CLASS_LOAD_HELPER_CLASS, classLoadHelper );
    }

    public String getContextKey()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_CONTEXT_PREFIX );
    }

    public void setContextKey( String contextKey )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_CONTEXT_PREFIX, contextKey );
    }

    public String getUserTransactionURL()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_USER_TX_URL );
    }

    public void setUserTransactionURL( String userTransactionURL )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_USER_TX_URL, userTransactionURL );
    }

    public String getWrapJobExecutionInUserTransaction()
    {
        return plexusScheduler.getProperties().getProperty( StdSchedulerFactory.PROP_SCHED_WRAP_JOB_IN_USER_TX );
    }

    public void setWrapJobExecutionInUserTransaction( String wrapJobExecutionInUserTransaction )
    {
        plexusScheduler.getProperties().setProperty( StdSchedulerFactory.PROP_SCHED_WRAP_JOB_IN_USER_TX, wrapJobExecutionInUserTransaction );
    }

}
