package org.codehaus.plexus.application.deploy;

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

import org.codehaus.plexus.application.event.ApplicationListener;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.application.ApplicationServerException;

/**
 * @author Peter Donald
 */
public interface ApplicationDeployer
{
    String ROLE = ApplicationDeployer.class.getName();

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    void deploy( String name, String location )
        throws ApplicationServerException;

    void redeploy( String name, String location )
        throws ApplicationServerException;

    void undeploy( String name )
        throws ApplicationServerException;

    // ----------------------------------------------------------------------
    // Listeners
    // ----------------------------------------------------------------------

    void addApplicationListener( ApplicationListener listener );

    void removeApplicationListener( ApplicationListener listener );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    ApplicationRuntimeProfile getApplicationRuntimeProfile( String applicationName )
        throws ApplicationServerException;
}
