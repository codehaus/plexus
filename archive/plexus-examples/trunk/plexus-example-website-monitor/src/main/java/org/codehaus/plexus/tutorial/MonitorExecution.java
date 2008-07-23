/**
 * ========================================================================
 * 
 * Copyright 2006 Rahul Thakur.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.plexus.tutorial;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple POJO to hold information about {@link WebsiteMonitor}'s execution or a 'run'.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class MonitorExecution
{

    /**
     * Time captured when the website monitor started execution.
     */
    private long startTime;

    /**
     * Time captured when the website monitor finished its run.
     */
    private long endTime;

    /**
     * List of results (basically list of {@link MonitorExecutionResult}) 
     * instances that are used to capture monitoring results.
     */
    private List results = new ArrayList();

    public MonitorExecution()
    {
        setStartTime( System.currentTimeMillis() );
    }

    /**
     * @return the endTime
     */
    public long getEndTime()
    {
        return endTime;
    }

    /**
     *  Sets the end time for this execution.<p>
     *  
     * @param endTime timestamp when this execution finished.
     */
    public void setEndTime( long endTime )
    {
        this.endTime = endTime;
    }

    /**
     * List of results as a result of this executions.
     * 
     * @return the results
     */
    public List getResults()
    {
        return results;
    }

    /**
     * <em>Not visible to public</em>.<p>
     * 
     * @param results the results to set
     */
    protected void setResults( List results )
    {
        this.results = results;
    }

    /**
     * Add a {@link MonitorExecutionResult} to the list of results.
     * 
     * @param result {@link MonitorExecutionResult} to add.
     */
    public void addResult( MonitorExecutionResult result )
    {
        if ( null == this.results )
            this.results = new ArrayList();
        this.results.add( result );
    }

    /**
     * Returns the start time for this execution.
     * 
     * @return timestamp when this execution started. 
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * <em>Not visible to public</em>.<p>
     * Sets the start time for this execution.<p>
     * This happens when the execution instance is created.
     * 
     * @param startTime the startTime to set
     */
    protected void setStartTime( long startTime )
    {
        this.startTime = startTime;
    }

}
