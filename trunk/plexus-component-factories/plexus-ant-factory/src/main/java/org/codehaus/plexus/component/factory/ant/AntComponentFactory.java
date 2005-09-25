package org.codehaus.plexus.component.factory.ant;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.RealmDelegatingClassLoader;

public class AntComponentFactory
    extends AbstractComponentFactory
{

    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        return new AntScriptInvoker( componentDescriptor, new RealmDelegatingClassLoader( classRealm ) );
    }

}
