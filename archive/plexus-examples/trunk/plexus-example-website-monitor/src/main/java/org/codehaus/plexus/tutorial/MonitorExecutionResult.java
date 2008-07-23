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

/**
 * Simple POJO to hold result data from {@link WebsiteMonitor}'s particular execution.
 * 
 * @deprecated <em>Experimental</em>.
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class MonitorExecutionResult
{
    /**
     * Result is keyed against the website name.
     */
    private String website;

    /**
     * Message (optional) set by monitor.
     */
    private String message;

    /**
     * Result (Exception or other) set by the monitor instance.
     */
    private Object result;

    /**
     * Marks the state for this result.
     */
    private boolean isError;

    /**
     * @return <code>true</code> if this result was in error.
     */
    public boolean isError()
    {
        return isError;
    }

    /**
     * Sets a state for this result. 
     * 
     * @param isError <code>true</code> implies the result is in error.
     */
    public void setError( boolean isError )
    {
        this.isError = isError;
    }

    /**
     * Any message to be displayed to the client.
     * 
     * @return the message.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message to be displayed to the client.
     * 
     * @param message the message to set
     */
    public void setMessage( String message )
    {
        this.message = message;
    }

    /**
     * @return the result
     */
    public Object getResult()
    {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult( Object result )
    {
        this.result = result;
    }

    /**
     * Website which was monitored that led to this result.
     *  
     * @return the website that was monitored.
     */
    public String getWebsite()
    {
        return website;
    }

    /**
     * Sets the name of the website that was monitored and resulted this result.
     * 
     * @param website the website that was monitored.
     */
    public void setWebsite( String website )
    {
        this.website = website;
    }

}
