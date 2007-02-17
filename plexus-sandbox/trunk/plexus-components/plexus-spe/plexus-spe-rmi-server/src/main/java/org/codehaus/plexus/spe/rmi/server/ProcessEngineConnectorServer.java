package org.codehaus.plexus.spe.rmi.server;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.ProcessService;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnection;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnectionClient;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnector;
import org.codehaus.plexus.spe.rmi.RemoteInvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessEngineConnectorServer
    extends AbstractLogEnabled
    implements ProcessEngineConnector, RemoteInvocationHandler
{
    private ProcessService processService;

    public ProcessEngineConnectorServer( ProcessService processService, Logger logger )
    {
        this.processService = processService;

        enableLogging( logger );
    }

    // -----------------------------------------------------------------------
    // ProcessEngineConnector Implementation
    // -----------------------------------------------------------------------

    public ProcessEngineConnection getConnection()
        throws RemoteException, ProcessException
    {
        System.out.println( "ProcessEngineConnectorServer.getConnection" );

        try
        {
            getLogger().info( "Got connection from " + RemoteServer.getClientHost() );
        }
        catch ( ServerNotActiveException e )
        {
            throw new ProcessException( "Could not get hostname of client." );
        }

        return new ProcessEngineConnectionClient( this );
    }

    // -----------------------------------------------------------------------
    // RemoteInvocationHandler Implementation
    // -----------------------------------------------------------------------

    public Object invoke( String methodName, Class[] types, Object[] args )
        throws RemoteException
    {
        if ( args == null )
        {
            args = new Object[ 0 ];
        }

//        Class[] argClasses = new Class[ args.length ];
//        for ( int i = 0; i < args.length; i++ )
//        {
//            argClasses[ i ] = args[i].getClass();
//        }

        try
        {
            Method method = findMethod( methodName, types );
            getLogger().info( "Invoking " + method.toString() );
            return method.invoke( processService, args );
        }
        catch ( IllegalAccessException e )
        {
            throw new RemoteException( "Can't access method: " + methodName + " with the argument types " +
                argsToString( types ), e );
        }
        catch ( InvocationTargetException e )
        {
            throw new RemoteException( "Error while invoking method: " + methodName + " with the argument types " +
                argsToString( types ), e );
        }
    }

    private Method findMethod( String methodName, Class[] argClasses )
        throws RemoteException
    {
        try
        {
            return ProcessService.class.getMethod( methodName, argClasses );
        }
        catch ( NoSuchMethodException e )
        {
            throw new RemoteException( "No such method: " + methodName + " with the argument types " +
                argsToString( argClasses ), e );
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private String argsToString( Class[] argClasses )
    {
        String a = null;

        for ( Class klass : argClasses )
        {
            if ( a == null )
            {
                a = klass.getName();
            }
            else
            {
                a += ", " + klass.getName();
            }
        }

        return "[" + a + "]";
    }
}
