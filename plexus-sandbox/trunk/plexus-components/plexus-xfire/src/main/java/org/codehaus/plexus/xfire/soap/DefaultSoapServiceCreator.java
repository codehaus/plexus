package org.codehaus.plexus.xfire.soap;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.xfire.config.PlexusXFireService;
import org.codehaus.plexus.xfire.config.PlexusXFireServiceModel;
import org.codehaus.plexus.xfire.config.io.stax.PlexusXFireServiceModelStaxReader;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.service.invoker.ScopePolicy;
import org.codehaus.xfire.service.invoker.ScopePolicyEditor;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.util.factory.Factory;

// use continuum/MRM as a use cases
// creating services
// using qdox/annotations to drive
// - methods to expose in the soap
// - generation and use of any DTOs used to proxy objects in the datamodel
// most convenient way to expose a component as a set of web services
// take configuration information taken from annotation sources and have an
// external services.xml file which can be used an an override

/**
 * @author Jason van Zyl
 * 
 * @plexus.component role="org.codehaus.plexus.xfire.soap.SoapServiceCreator" role-hint="default"
 */
public class DefaultSoapServiceCreator extends AbstractLogEnabled
    implements SoapServiceCreator, Initializable, Contextualizable
{
    /**
     * @plexus.requirement
     */
    private XFire xfire;

    /**
     * @plexus.requirement
     */
    private ServiceFactory serviceFactory;

    /**
     * @plexus.requirement
     */
    private PlexusServiceWsdlConfigurator serviceWsdlConfigurator;

    private Map configurationSources = new HashMap();

    private PlexusContainer container;

    // ----------------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------------

    public Service createService( String role ) throws SoapServiceCreationException
    {
        Object component;

        try
        {
            component = container.lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new SoapServiceCreationException( "Error looking up component from which to create SOAP service.", e );
        }

        Class clazz = component.getClass();

        // ----------------------------------------------------------------------------
        // Now we need to pull the configuration for the webservice from the specificed
        // source so we can pass those values in to the ServiceFactory.
        // ----------------------------------------------------------------------------

        SoapServiceConfiguration config = (SoapServiceConfiguration) configurationSources.get( role );

        if ( config == null )
        {
            throw new SoapServiceCreationException( "unable to obtain soap service configuration for " + role );
        }

        String name = config.getName();

        String namespace = config.getNamespace();

        String use = config.getUse();

        String style = config.getStyle();

        String soapVersion = config.getSoapVersion();

        String wsdlUrl = config.getWsdlUrl();

        String scope = config.getScope();

        Service service;

        SoapVersion version = null;

        if ( soapVersion.equals( "1.1" ) )
        {
            version = Soap11.getInstance();
        }
        else if ( soapVersion.equals( "1.2" ) )
        {
            version = Soap12.getInstance();
        }

        ( (PlexusServiceFactory) serviceFactory ).setStyle( style );

        ( (PlexusServiceFactory) serviceFactory ).setUse( use );

        if ( name.length() == 0 && namespace.length() == 0 )
        {
            service = serviceFactory.create( clazz, null );
        }
        else
        {
            service = serviceFactory.create( clazz, name, namespace, null );
        }

        final ScopePolicy policy = ScopePolicyEditor.toScopePolicy( scope );

        if ( container.hasComponent( role ) )
        {
            final Invoker invoker = new FactoryInvoker( new PlexusFactory( role ), policy );

            service.setInvoker( invoker );
        }

        getLogger().info( "Registered soap " + service.getSimpleName() );

        xfire.getServiceRegistry().register( service );

        return service;
    }

    private void loadServiceResource( URL serviceResource ) throws Exception
    {
        PlexusXFireServiceModelStaxReader reader = new PlexusXFireServiceModelStaxReader();

        try
        {
            PlexusXFireServiceModel serviceModels = reader.read( new InputStreamReader( serviceResource.openStream() ) );

            for ( Iterator i = serviceModels.getPlexusXFireServices().iterator(); i.hasNext(); )
            {
                PlexusXFireService service = (PlexusXFireService) i.next();

                configurationSources.put( service.getClassName(), service );

                createService( service.getClassName() );
            }

        }
        catch ( MalformedURLException e )
        {
            throw new SoapServiceConfigurationSourceRetrievalException( "error locating", e );
        }
        catch ( IOException e )
        {
            throw new SoapServiceConfigurationSourceRetrievalException( "error reading", e );
        }
        catch ( XMLStreamException e )
        {
            throw new SoapServiceConfigurationSourceRetrievalException( "error parsing", e );
        }
    }

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void initialize() throws InitializationException
    {
        ( (PlexusServiceFactory) serviceFactory ).getServiceConfigurations().add( serviceWsdlConfigurator );

        try
        {
            Enumeration enumerator =
                DefaultSoapServiceCreator.class.getClassLoader().getResources( "META-INF/plexus/plexus-xfire-services.xml" );

            while ( enumerator.hasMoreElements() )
            {
                URL serviceResource = (URL) enumerator.nextElement();

                loadServiceResource( serviceResource );
            }
        }
        catch ( IOException e )
        {
            throw new InitializationException( "unable to initialize, problem with xml loading", e );
        }
        catch ( Exception e )
        {
            throw new InitializationException( "unable to initialize , problem with xml loading", e );
        }
    }

    public void contextualize( Context context ) throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    // ----------------------------------------------------------------------------
    // Factory
    // ----------------------------------------------------------------------------

    class PlexusFactory implements Factory
    {
        private String role;

        public PlexusFactory( String role )
        {
            this.role = role;
        }

        public Object create() throws Throwable
        {
            return container.lookup( role );
        }
    }
}
