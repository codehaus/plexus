package org.codehaus.xfire.plexus.config;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.aegis.type.Type;
import org.codehaus.xfire.aegis.type.TypeMapping;
import org.codehaus.xfire.aegis.type.TypeMappingRegistry;
import org.codehaus.xfire.handler.Handler;
import org.codehaus.xfire.plexus.PlexusXFireComponent;
import org.codehaus.xfire.plexus.ServiceLocatorFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.service.invoker.ScopePolicy;
import org.codehaus.xfire.service.invoker.ScopePolicyEditor;
import org.codehaus.xfire.soap.Soap11;
import org.codehaus.xfire.soap.Soap12;
import org.codehaus.xfire.soap.SoapVersion;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.util.ClassLoaderUtils;

/**
 * Creates and configures services.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Sep 20, 2004
 */
public class ObjectServiceConfigurator
        extends PlexusXFireComponent
        implements Configurator
{
    public Service createService(PlexusConfiguration config)
            throws Exception
    {
        ObjectServiceFactory builder = getServiceFactory(config);

        String name = config.getChild("name").getValue("");
        String namespace = config.getChild("namespace").getValue("");
        String use = config.getChild("use").getValue("literal");
        String style = config.getChild("style").getValue("wrapped");
        String serviceClass = config.getChild("serviceClass").getValue();
        String role = config.getChild("role").getValue(serviceClass);
        String soapVersion = config.getChild("soapVersion").getValue("1.1");
        String wsdlUrl = config.getChild("wsdl").getValue("");


        Class clazz = getClass().getClassLoader().loadClass(serviceClass);
        Service service;
        SoapVersion version = null;
        if (soapVersion.equals("1.1"))
        {
            version = Soap11.getInstance();
        }
        else if (soapVersion.equals("1.2"))
        {
            version = Soap12.getInstance();
        }

        builder.setStyle(style);
        builder.setUse(use);

        if (name.length() == 0 && namespace.length() == 0)
        {
            service = builder.create(clazz, (Map) null);
        }
        else
        {
            service = builder.create(clazz, name, namespace, null);
        }

        PlexusConfiguration[] types = config.getChild("types").getChildren("type");
        for (int i = 0; i < types.length; i++)
        {
            initializeType(types[i],
                           ((AegisBindingProvider) builder.getBindingProvider()).getTypeMapping(service));
        }


        // Setup the Invoker
        final String scope = config.getChild("scope").getValue("application");
        final ScopePolicy policy = ScopePolicyEditor.toScopePolicy(scope);
        if (getServiceLocator().hasComponent(role))
        {
            final Invoker invoker = new FactoryInvoker(
              new ServiceLocatorFactory(role, getServiceLocator()), policy);
            
            service.setInvoker(invoker);
        }
        else
        {
          //final String scope = config.getChild("scope").getValue("application");
          final ObjectInvoker oinvoker = new ObjectInvoker(policy);
            
            String implClass = config.getChild("implementationClass").getValue("");
            
            if (implClass.length() > 0)
            {
                service.setProperty(ObjectInvoker.SERVICE_IMPL_CLASS, loadClass(implClass));
            }

            service.setInvoker(oinvoker);
        }

        // Setup pipelines
        if (service.getInHandlers() == null) service.setInHandlers(new ArrayList());
        if (service.getOutHandlers() == null) service.setOutHandlers(new ArrayList());
        if (service.getFaultHandlers() == null) service.setFaultHandlers(new ArrayList());
        
        createHandlers(config.getChild("inHandlers"), service.getInHandlers());
        createHandlers(config.getChild("outHandlers"), service.getOutHandlers());
        createHandlers(config.getChild("faultHandlers"), service.getFaultHandlers());

        getLogger().info("Registered service " + service.getSimpleName());

        getServiceRegistry().register(service);

        return service;
    }

    private void createHandlers(PlexusConfiguration child, List handlerList)
            throws Exception
    {
        if (child == null)
            return;

        PlexusConfiguration[] handlers = child.getChildren("handler");
        if (handlers.length == 0)
            return;

        for (int i = 0; i < handlers.length; i++)
        {
            handlerList.add(getHandler(handlers[i].getValue()));
        }
    }

    public ObjectServiceFactory getServiceFactory(PlexusConfiguration config)
            throws Exception
    {
        String factoryClass = config.getChild("serviceFactory").getValue("");
        BindingProvider binding = getBindingProvider(config);

        return getServiceFactory(factoryClass, binding);
    }

    protected BindingProvider getBindingProvider(PlexusConfiguration config)
            throws InstantiationException, IllegalAccessException, Exception
    {
        String bindingClass = config.getChild("bindingProvider").getValue("");
        BindingProvider binding = null;
        if (bindingClass.length() > 0)
        {
            binding = (BindingProvider) loadClass(bindingClass).newInstance();
        }
        else
        {
            binding = new AegisBindingProvider(getTypeMappingRegistry());
        }
        return binding;
    }

    /**
     * @return
     * @throws PlexusConfigurationException
     */
    protected ObjectServiceFactory getServiceFactory(String builderClass, BindingProvider bindingProvider)
            throws Exception
    {
        if (builderClass.length() == 0)
        {
            return new ObjectServiceFactory(getXFire().getTransportManager(), null);
        }
        else
        {
            Class clz = loadClass(builderClass);
            Constructor con =
                    clz.getConstructor(new Class[]{TransportManager.class, Class.class});

            return (ObjectServiceFactory)
                    con.newInstance(new Object[]{getXFire().getTransportManager(), bindingProvider});
        }
    }

    private void initializeType(PlexusConfiguration configuration,
                                TypeMapping tm)
            throws Exception
    {
        try
        {
            String ns = configuration.getAttribute("namespace", tm.getEncodingStyleURI());
            String name = configuration.getAttribute("name");

            Type type = (Type) loadClass(configuration.getAttribute("type")).newInstance();

            tm.register(loadClass(configuration.getAttribute("class")),
                        new QName(ns, name),
                        type);
        }
        catch (Exception e)
        {
            if (e instanceof PlexusConfigurationException)
                throw (PlexusConfigurationException) e;

            throw new PlexusConfigurationException("Could not configure type.", e);
        }
    }

    protected Handler getHandler(String name)
            throws Exception
    {
        try
        {
            return (Handler) getServiceLocator().lookup(Handler.ROLE, name);
        }
        catch (ComponentLookupException e)
        {
            return (Handler) loadClass(name).newInstance();
        }
    }

    public XFire getXFire()
    {
        XFire xfire = null;

        try
        {
            xfire = (XFire) getServiceLocator().lookup(XFire.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the XFire engine!", e);
        }

        return xfire;
    }

    public TypeMappingRegistry getTypeMappingRegistry()
    {
        TypeMappingRegistry registry = null;

        try
        {
            registry = (TypeMappingRegistry) getServiceLocator().lookup(TypeMappingRegistry.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the TypeMappingRegistry!", e);
        }

        return registry;
    }

    protected TransportManager getTransportManager()
    {
        TransportManager transMan = null;

        try
        {
            transMan = (TransportManager) getServiceLocator().lookup(TransportManager.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the TransportManager!", e);
        }

        return transMan;
    }

    /**
     * Load a class from the class loader.
     *
     * @param className The name of the class.
     * @return The class.
     * @throws Exception
     */
    protected Class loadClass(String className)
            throws Exception
    {
        // Handle array'd types.
        if (className.endsWith("[]"))
        {
            className = "[L" + className.substring(0, className.length() - 2) + ";";
        }

        return ClassLoaderUtils.loadClass(className, getClass());
    }

    protected ServiceRegistry getServiceRegistry()
    {
        ServiceRegistry registry = null;

        try
        {
            registry = (ServiceRegistry) getServiceLocator().lookup(ServiceRegistry.ROLE);
        }
        catch (ComponentLookupException e)
        {
            throw new RuntimeException("Couldn't find the ServiceRegistry!", e);
        }

        return registry;
    }
}
