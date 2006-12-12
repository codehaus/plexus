package org.codehaus.plexus.component.factory.jruby;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.jruby.JRubyInvoker;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/** @author eredmond */
public class JRubyComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassLoader classLoader,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        return new JRubyInvoker( componentDescriptor, classLoader );
    }
}
