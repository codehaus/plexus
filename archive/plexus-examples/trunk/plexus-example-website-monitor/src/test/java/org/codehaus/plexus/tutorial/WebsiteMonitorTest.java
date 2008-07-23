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

import org.codehaus.plexus.PlexusTestCase;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class WebsiteMonitorTest
    extends PlexusTestCase
{

    /**
     * sets up a Plexus container instance for running test.
     */
    protected void setUp()
        throws Exception
    {
        // call this to enable super class to setup a Plexus container test
        // instance and enable component lookup.
        super.setUp();
    }

    /**
     * Test if we are able to lookup and obtain our component instance from the
     * container.
     * 
     * @throws Exception
     */
    public void testBasic()
        throws Exception
    {
        WebsiteMonitor component = (WebsiteMonitor) lookup( WebsiteMonitor.ROLE );
        assertNotNull( component );
    }

    /**
     * Test an unkown host.
     * 
     * @throws Exception
     */
    public void testUnknownHost()
        throws Exception
    {
        WebsiteMonitor component = (WebsiteMonitor) lookup( WebsiteMonitor.ROLE );
        assertNotNull( component );
        // the component should have been initialized with list of websites 
        // from our test components.xml
        assertTrue( component.isInitialized() );
        try
        {
            component.monitor();
            fail( "Expected UnknownHostException" );
        }
        catch ( UnknownHostException e )
        {
            // do nothing
        }

    }

    /**
     * Test a page that does not exists.
     * 
     * @throws Exception
     */
    public void testNotOKCode()
        throws Exception
    {
        WebsiteMonitor component = (WebsiteMonitor) lookup( WebsiteMonitor.ROLE );
        assertNotNull( component );
        List websites = new ArrayList();
        websites.add( "http://maven.apache.org/non-existent.html" );
        component.addWebsites( websites );
        assertTrue( component.isInitialized() );
        try
        {
            component.monitor();
            fail( "Excepted Exception!" );
        }
        catch ( Exception e )
        {
            // do nothing.
        }
    }

    protected InputStream getCustomConfiguration()
        throws Exception
    {
        InputStream is = this.getClass().getClassLoader()
            .getResourceAsStream( "org/codehaus/plexus/PlexusTestContainerConfig.xml" );
        return is;
    }

}
