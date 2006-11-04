package org.codehaus.plexus.spe.model;

import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;
import javax.jdo.listener.AttachLifecycleListener;
import javax.jdo.listener.DetachLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Figure out if there is a way to figure out if a field is detahced to prevent the silly try/catch
 * clauses all over the place.
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessInstanceLifecycleListener
    implements AttachLifecycleListener, DetachLifecycleListener, StoreLifecycleListener
{
    // ----------------------------------------------------------------------
    // AttachLifecycleListener Implementation
    // ----------------------------------------------------------------------

    public void preAttach( InstanceLifecycleEvent event )
    {
        ProcessInstance source = (ProcessInstance) event.getSource();

        try
        {
            source.setContext( serializableToContextValue( source.getContext() ) );
        }
        catch ( JDODetachedFieldAccessException e )
        {
            // ignore
        }
    }

    public void postAttach( InstanceLifecycleEvent event )
    {
    }

    // ----------------------------------------------------------------------
    // DetachLifecycleListener Implementation
    // ----------------------------------------------------------------------

    public void preDetach( InstanceLifecycleEvent event )
    {
    }

    public void postDetach( InstanceLifecycleEvent event )
    {
        ProcessInstance source = (ProcessInstance) event.getSource();

        try
        {
            source.setContext( contextValueToSerializable( source.getContext() ) );
        }
        catch ( JDODetachedFieldAccessException e )
        {
            // ignore
        }
    }

    // ----------------------------------------------------------------------
    // StoreLifecycleListener Implementation
    // ----------------------------------------------------------------------

    public void preStore( InstanceLifecycleEvent event )
    {
        ProcessInstance source = (ProcessInstance) event.getSource();
        if ( JDOHelper.isNew( source ) )
        {
            try
            {
                source.setContext( serializableToContextValue( source.getContext() ) );
            }
            catch ( JDODetachedFieldAccessException e )
            {
                // ignore
            }
        }
    }

    public void postStore( InstanceLifecycleEvent event )
    {
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private Map<String, Serializable> contextValueToSerializable( Map processContext )
    {
        try
        {
            Map context = new HashMap();

            Iterator iterator = processContext.entrySet().iterator();

            while ( iterator.hasNext() )
            {
                Map.Entry entry = (Map.Entry) iterator.next();

                if ( ! ( entry.getValue() instanceof ContextValue ) )
                {
                    throw new RuntimeException( "All entires has to be ContextValues" );
                }

                ByteArrayInputStream data = new ByteArrayInputStream( ((ContextValue)entry.getValue()).getValue() );
                ObjectInputStream inputStream = new ObjectInputStream( data );
                context.put( entry.getKey(), inputStream.readObject() );
            }

            return context;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Could not un-serialize the context.", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( "Could not un-serialize the context.", e );
        }
    }

    private Map serializableToContextValue( Map context )
    {
        try
        {
            Map processContext = new HashMap();

            Iterator iterator = context.entrySet().iterator();
            while( iterator.hasNext() )
            {
                Map.Entry entry = (Map.Entry) iterator.next();

                if ( entry.getValue() instanceof ContextValue )
                {
                    throw new RuntimeException( "All entires has to be Serializables and not ContextValues: " + entry.getKey() + "=" + entry.getValue().getClass() );
                }

                ByteArrayOutputStream data = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream( data );

                objectOutputStream.writeObject( entry.getValue() );

                ContextValue contextValue = new ContextValue();
                contextValue.setValue( data.toByteArray() );
                processContext.put( entry.getKey(), contextValue );
            }

            return processContext;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Could not serialize the context.", e );
        }
    }
}
