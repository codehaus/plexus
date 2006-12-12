package org.codehaus.plexus.component.factory.ant;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.io.IOException;

public class AntComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassLoader classLoader, PlexusContainer container )
        throws ComponentInstantiationException
    {
        try
        {
            return new AntScriptInvoker( componentDescriptor, classLoader );
        }
        catch ( IOException e )
        {
            throw new ComponentInstantiationException( "Failed to extract Ant script for: " + componentDescriptor.getHumanReadableKey(), e );
        }
    }

}
