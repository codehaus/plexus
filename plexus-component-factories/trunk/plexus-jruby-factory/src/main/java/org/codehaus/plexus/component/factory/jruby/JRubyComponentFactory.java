package org.codehaus.plexus.component.factory.jruby;

import java.util.Iterator;
import java.util.Map;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.jruby.JRubyBSFInvoker;
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
    protected Map mapInputs;

    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        JRubyBSFInvoker invoker = new JRubyBSFInvoker( componentDescriptor, classRealm );

        invoker.setAssumeLoop( assumeLoop );
        invoker.setAssumePrintLoop( assumePrintLoop );
        invoker.setAutoSplit( autoSplit );
        invoker.setWarning( warning );
        invoker.setDebug( debug );
        invoker.setProcessLineEnds( processLineEnds );

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
