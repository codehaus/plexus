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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.application.event.ApplicationListener;


/**
 * A Deployer is responsible for taking a URL (ie a jar/war/ear) and deploying
 * it to a particular "location". "location" means different things for
 * different containers. For a servlet container it may mean the place to mount
 * servlet (ie /myapp --> /myapp/Cocoon.xml is mapping cocoon servlet to /myapp
 * context).
 *
 * @author Peter Donald
 */
public interface ApplicationDeployer
{
    public final static String ROLE = ApplicationDeployer.class.getName();

    /**
     * Deploy an installation.
     *
     * @param name     the name of deployment
     * @param location the installation to deploy
     * @throws LoomException if an error occurs
     */
    boolean deploy( String name, String location )
        throws Exception;

    /**
     * Undeploy and deploy an installation.
     *
     * @param name     the name of deployment
     * @param location the installation to redeploy
     * @throws LoomException if an error occurs
     */
    boolean redeploy( String name, String location )
        throws Exception;

    /**
     * Undeploy a resource from a location.
     *
     * @param name the name of deployment
     * @throws LoomException if an error occurs
     */
    boolean undeploy( String name )
        throws Exception;

    void addApplicationListener( ApplicationListener listener );

    void removeApplicationListener( ApplicationListener listener );

    /**
     * TODO don't expose via XML-RPC
     *
     * @param string
     * @return
     */
    PlexusContainer getApplication( String string );
}
