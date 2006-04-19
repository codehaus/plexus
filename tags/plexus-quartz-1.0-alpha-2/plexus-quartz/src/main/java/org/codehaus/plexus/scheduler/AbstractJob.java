/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.scheduler;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.InterruptableJob;
import org.quartz.UnableToInterruptJobException;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.Context;

/**
 * Base class from which all <code>Job</code>s running in the
 * scheduler should be derived from if they want access to the
 * ServiceBroker.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Make this a component using the lifecycle interfaces.
 * @todo Each job execution should be logged to a separate target.
 */
public abstract class AbstractJob
    implements InterruptableJob
{
    /** JobDataMap tag for the job's logger. */
    public static final String LOGGER = "JOB_LOGGER";
    
    /** JobDataMap tag for the job's context. */
    public static final String CONTEXT = "JOB_CONTEXT";

    /** JobDataMap tag for the job's service broker. */
    public static final String SERVICE_MANAGER = "JOB_SERVICE_MANAGER";
    
    /** JobDataMap tag for the job's configuration. */
    public static final String EXECUTION_CONFIGURATION = "JOB_EXECUTION_CONFIGURATION";

    /** Job Data Map */
    private JobDataMap jobDataMap;

    private boolean interrupted;

    /** Set Job Data Map */
    public void setJobDataMap(JobDataMap jobDataMap)
    {
        this.jobDataMap = jobDataMap;
    }
    
    /** Get Job Data Map */
    public JobDataMap getJobDataMap()
    {
        return jobDataMap;
    }        
    
    /** Get the Logger. */
    public Logger getLogger()
    {
        return (Logger) getJobDataMap().get(LOGGER);
    }        

    /** Get the Context. */
    public Context getContext()
    {
        return (Context) getJobDataMap().get(CONTEXT);
    }        

    /** Get the Configuration. */
    public PlexusConfiguration getConfiguration()
    {
        return (PlexusConfiguration) getJobDataMap().get(EXECUTION_CONFIGURATION);
    }        

    /** Execute the Job. */
    public abstract void execute(JobExecutionContext context)
        throws JobExecutionException;

    public boolean isInterrupted()
    {
        return interrupted;
    }

    public void interrupt()
        throws UnableToInterruptJobException
    {
        interrupted = true;
    }
}
