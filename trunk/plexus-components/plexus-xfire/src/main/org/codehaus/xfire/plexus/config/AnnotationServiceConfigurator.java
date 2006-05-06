package org.codehaus.xfire.plexus.config;

import java.lang.reflect.Constructor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.annotations.WebAnnotations;
import org.codehaus.xfire.annotations.commons.CommonsWebAttributes;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.TransportManager;

public class AnnotationServiceConfigurator
    extends ObjectServiceConfigurator
{

    public ObjectServiceFactory getServiceFactory(PlexusConfiguration config)
        throws Exception
    {
        Class annotsClz = null;
        Class clz = null;
        String annots = config.getChild("annotations").getValue();
        String factoryClass = config.getChild("serviceFactory").getValue();
        
        if (annots == null)
        {
            annotsClz = CommonsWebAttributes.class;
        }
        else
        {
            annotsClz = loadClass(annots);
        }
        
        if (factoryClass == null)
        {
            clz = AnnotationServiceFactory.class;
        }
        else
        {
            clz = loadClass(factoryClass);
        }
        
        Constructor con = 
            clz.getConstructor( new Class[] {WebAnnotations.class, TransportManager.class, BindingProvider.class} );
        
        return (ObjectServiceFactory) 
            con.newInstance(new Object[] {annotsClz.newInstance(), 
                    getXFire().getTransportManager(),
                    getBindingProvider(config) });
    }
}
