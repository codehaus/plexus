package org.codehaus.plexus.scheduler;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.context.Context;

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
    implements Job
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

    /** Get the ServiceBroker. */
    public ServiceManager getServiceBroker()
    {
        return (ServiceManager) getJobDataMap().get(SERVICE_MANAGER);
    }        

    /** Get the Context. */
    public Context getContext()
    {
        return (Context) getJobDataMap().get(CONTEXT);
    }        

    /** Get the Configuration. */
    public Configuration getConfiguration()
    {
        return (Configuration) getJobDataMap().get(EXECUTION_CONFIGURATION);
    }        

    /** Execute the Job. */
    public abstract void execute(JobExecutionContext context)
        throws JobExecutionException;
}
