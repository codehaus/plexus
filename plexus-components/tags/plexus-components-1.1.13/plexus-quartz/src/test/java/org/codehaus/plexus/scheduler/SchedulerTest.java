package org.codehaus.plexus.scheduler;

import org.codehaus.plexus.PlexusTestCase;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerListener;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class SchedulerTest
    extends PlexusTestCase
    implements TriggerListener
{
    private boolean triggerFired;

    public void testCreation()
        throws Exception
    {
        Scheduler scheduler = (Scheduler) lookup( Scheduler.ROLE );

        assertNotNull( scheduler );

        JobDataMap dataMap = new JobDataMap();

        dataMap.put( "project", "continuum" );

        JobDetail jobDetail = new JobDetail( "job", "group", JobOne.class );

        jobDetail.setJobDataMap( dataMap );

        Trigger trigger = new SimpleTrigger( "trigger", "group" );

        scheduler.addGlobalTriggerListener( this );

        scheduler.scheduleJob( jobDetail, trigger );

        while ( ! triggerFired )
        {
        }
    }

    public void triggerComplete( Trigger trigger, JobExecutionContext context, int triggerInstructionCode )
    {
    }

    public boolean vetoJobExecution( Trigger trigger, JobExecutionContext context )
    {
        return false;
    }

    public void triggerFired( Trigger trigger, JobExecutionContext context )
    {
        System.out.println( "Trigger fired!" );

        triggerFired = true;
    }

    public void triggerMisfired( Trigger trigger )
    {
    }
}

