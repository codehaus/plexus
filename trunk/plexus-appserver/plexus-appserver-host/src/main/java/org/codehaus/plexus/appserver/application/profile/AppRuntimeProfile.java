package org.codehaus.plexus.appserver.application.profile;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AppRuntimeProfile
{
    private String name;

    private File home;

    private File lib;

    private DefaultPlexusContainer applicationContainer;

    private PlexusContainer applicationServerContainer;

    private PlexusConfiguration applicationConfiguration;

    /**
     * @deprecated
     */
    private List services;

    /**
     * @deprecated
     */
    private List serviceConfigurations;
    
    private Map serviceMap;
    
    private Map plexusConfigurationMap;
    
    private Map serviceConfigurationMap;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public AppRuntimeProfile( String name, File home, File lib, DefaultPlexusContainer applicationContainer,
                              PlexusContainer applicationServerContainer, PlexusConfiguration applicationConfiguration )
    {
        this.name = name;

        this.home = home;

        this.lib = lib;

        this.applicationContainer = applicationContainer;

        this.applicationServerContainer = applicationServerContainer;

        this.applicationConfiguration = applicationConfiguration;

        this.services = new ArrayList();

        this.serviceConfigurations = new ArrayList();
        
        serviceMap = new HashMap();
        
        plexusConfigurationMap = new HashMap();
        
        serviceConfigurationMap = new HashMap();
    }

    public String getName()
    {
        return name;
    }

    public File getHome()
    {
        return home;
    }

    public File getLib()
    {
        return lib;
    }

    public DefaultPlexusContainer getApplicationContainer()
    {
        return applicationContainer;
    }

    public PlexusContainer getApplicationServerContainer()
    {
        return applicationServerContainer;
    }

    public PlexusConfiguration getApplicationConfiguration()
    {
        return applicationConfiguration;
    }

    public List getServices()
    {
        return Collections.unmodifiableList( new ArrayList( plexusConfigurationMap.keySet() ) );
    }

    public List getServiceConfigurations()
    {
        return Collections.unmodifiableList( new ArrayList ( plexusConfigurationMap.values() ) );
    }
    
    public PlexusService getService( String id )
    {
        return (PlexusService) serviceMap.get( id );
    }
    
    public PlexusConfiguration getPlexusConfiguration( PlexusService service )
    {
        return (PlexusConfiguration) plexusConfigurationMap.get( service );
    }
    
    public void addService( String id, PlexusService service, PlexusConfiguration configuration )
    {
        plexusConfigurationMap.put( service, configuration );
        
        serviceMap.put( id, service );
    }
    
    public Object getServiceConfiguration( PlexusService service )
    {
        return serviceConfigurationMap.get( service );
    }
    
    public void addServiceConfiguration( PlexusService service, Object configuration )
    {
        serviceConfigurationMap.put( service, configuration );    
    }
}
