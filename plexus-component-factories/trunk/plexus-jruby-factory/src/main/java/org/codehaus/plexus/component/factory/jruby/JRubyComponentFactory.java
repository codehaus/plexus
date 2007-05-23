package org.codehaus.plexus.component.factory.jruby;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.jruby.JRubyRuntimeInvoker;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/** @author eredmond */
public class JRubyComponentFactory
    extends AbstractComponentFactory
{
    protected boolean assumeLoop;
    protected boolean assumePrintLoop;
    protected boolean autoSplit;
    protected int warning;
    protected boolean debug;
    protected boolean processLineEnds;
    protected String[] libraryPaths;
    protected String[] requires;
    protected Map inputs;

    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        JRubyRuntimeInvoker invoker = new JRubyRuntimeInvoker( componentDescriptor, classRealm );

        invoker.setAssumeLoop( assumeLoop );
        invoker.setAssumePrintLoop( assumePrintLoop );
        invoker.setAutoSplit( autoSplit );
        invoker.setWarning( warning );
        invoker.setDebug( debug );
        invoker.setProcessLineEnds( processLineEnds );
        invoker.setLibraryPaths( libraryPaths );
        invoker.setRequires( requires );

        if( inputs != null )
        {
            for( Iterator iter = inputs.entrySet().iterator(); iter.hasNext(); )
            {
                Map.Entry entry = (Map.Entry)iter.next();

                invoker.inputValue( (String)entry.getKey(), entry.getValue() );
            }
        }

        return invoker;
    }
}
