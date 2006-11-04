package org.codehaus.plexus.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.SchedulerException;
import org.quartz.JobListener;
import org.quartz.TriggerListener;
import org.quartz.impl.StdScheduler;

import java.util.Properties;

public interface Scheduler
{
    public static String ROLE = Scheduler.class.getName();

    void scheduleJob( JobDetail jobDetail, Trigger trigger )
        throws SchedulerException;

    void addGlobalJobListener( JobListener listener );

    void addGlobalTriggerListener( TriggerListener listener );

    void unscheduleJob( String jobName, String groupName )
        throws SchedulerException;

    boolean interruptSchedule( String jobName, String groupName )
        throws SchedulerException;

    public void setProperties( Properties properties );

    public Properties getProperties();

}
