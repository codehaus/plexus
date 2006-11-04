package org.codehaus.plexus.xfire.soap;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
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
//   - methods to expose in the soap
//   - generation and use of any DTOs used to proxy objects in the datamodel
// most convenient way to expose a component as a set of web services
// take configuration information taken from annotation sources and have an
//   external services.xml file which can be used an an override

/**
 * @author Jason van Zyl
 */
public class DefaultSoapServiceCreator
    extends AbstractLogEnabled
    implements SoapServiceCreator,
    Initializable,
    Contextualizable
{
    private XFire xfire;

    private ServiceFactory serviceFactory;

    private PlexusServiceWsdlConfigurator serviceWsdlConfigurator;

    private SoapServiceConfigurationSource soapServiceConfigurationSource;

    private PlexusContainer container;

    // ----------------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------------

    public Service createService( String role )
        throws SoapServiceCreationException
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

        SoapServiceConfiguration config = null;

        try
        {
            config = soapServiceConfigurationSource.getConfiguration();
        }
        catch ( SoapServiceConfigurationSourceRetrievalException e )
        {
            throw new SoapServiceCreationException( "Cannot retrieve SOAP service configuration.", e );
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

        ((PlexusServiceFactory)serviceFactory).setStyle( style );

        ((PlexusServiceFactory)serviceFactory).setUse( use );

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

    // ----------------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        ( (PlexusServiceFactory) serviceFactory ).getServiceConfigurations().add( serviceWsdlConfigurator );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    // ----------------------------------------------------------------------------
    // Factory
    // ----------------------------------------------------------------------------

    class PlexusFactory
        implements Factory
    {
        private String role;

        public PlexusFactory( String role )
        {
            this.role = role;
        }

        public Object create()
            throws Throwable
        {
            return container.lookup( role );
        }
    }
}
