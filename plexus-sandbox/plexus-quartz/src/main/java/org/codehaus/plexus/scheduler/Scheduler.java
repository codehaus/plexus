package org.codehaus.plexus.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.SchedulerException;

public interface Scheduler
{
    public static String ROLE = Scheduler.class.getName();

    void scheduleJob( JobDetail jobDetail, Trigger trigger )
        throws SchedulerException;
}
