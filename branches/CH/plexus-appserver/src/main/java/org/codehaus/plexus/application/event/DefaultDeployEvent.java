package org.codehaus.plexus.application.event;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.application.deploy.ApplicationDeployer;

/**
 * Signals an (un/re)deployment event;
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jul 17, 2004
 */
public class DefaultDeployEvent
    implements DeployEvent
{
    private String applicationName;

    private ApplicationDeployer sender;

    /**
     * @return Returns the applicationName.
     */
    public String getApplicationName()
    {
        return applicationName;
    }

    /**
     * @param applicationName The applicationName to set.
     */
    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    /**
     * @return Returns the sender.
     */
    public ApplicationDeployer getSender()
    {
        return sender;
    }

    /**
     * @param sender The sender to set.
     */
    public void setSender( ApplicationDeployer sender )
    {
        this.sender = sender;
    }
}
