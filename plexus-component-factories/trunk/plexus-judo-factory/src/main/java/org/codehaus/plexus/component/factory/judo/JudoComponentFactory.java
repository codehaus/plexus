package org.codehaus.plexus.component.factory.judo;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.judo.JudoBSFInvoker;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/** @author eredmond */
public class JudoComponentFactory
    extends AbstractComponentFactory
{
    protected boolean debug;
    protected String[] libraryPaths;
    protected String[] requires;
    protected Map mapInputs;

    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        componentDescriptor.setComponentComposer( "map-oriented" );

        JudoBSFInvoker invoker = new JudoBSFInvoker( componentDescriptor, classRealm );

        invoker.setDebug( debug );

        if( libraryPaths != null )
        {
            for( int i=0; i<libraryPaths.length; i++ )
            {
                invoker.addLibPath( libraryPaths[i] );
            }
        }

        if( requires != null )
        {
            for( int i=0; i<requires.length; i++ )
            {
                invoker.addReqLib( requires[i] );
            }
        }

        if( mapInputs != null )
        {
            for( Iterator iter = mapInputs.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)iter.next();

                invoker.inputValue( (String)entry.getKey(), entry.getValue() );
            }
        }

        return invoker;
    }
}
