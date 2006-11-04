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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Currently the role this class plays is set the value of the <code>JobDataMap</code>
 * in the job so that the convenience methods for accessing the logger, context,
 * service broker and configuration will work as expected.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Make this a component using the lifecycle interfaces.
 */
public class DefaultJobListener
     implements JobListener
{
    /**
     * <p>
     *
     * Get the name of the <code>JobListener</code>.</p>
     */
    public String getName()
    {
        return "DefaultJobLister";
    }

    /**
     * <p>
     *
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Job}</code>
     * is about to be executed (an associated <code>{@link org.quartz.Trigger}</code> has
     * occured).</p>
     */
    public void jobToBeExecuted(JobExecutionContext context)
    {
        Job job = context.getJobInstance();
                
        // Only attempt to set the ServiceBroker when we are dealing
        // with subclasses AbstractJob.
        if (job instanceof AbstractJob)
        {
            ((AbstractJob)job).setJobDataMap(context.getJobDetail().getJobDataMap());
        }
    }

    public void jobExecutionVetoed( JobExecutionContext jobExecutionContext )
    {
    }

    /**
     * <p>
     *
     * Called by the <code>{@link Scheduler}</code> after a <code>{@link Job}</code>
     * has been executed, and be for the associated <code>Trigger</code>'s
     * <code>triggered(xx)</code> method has been called.</p>
     */
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException)
    {
        Job job = context.getJobInstance();
        
        // Only attempt to null the ServiceBroker when we are dealing
        // with subclasses AbstractJob.
        if (job instanceof AbstractJob)
        {
        }
    }
}
