package org.codehaus.plexus.spe.rmi;

import org.codehaus.plexus.spe.ProcessService;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessEngineConnectionClient
    implements Serializable, ProcessEngineConnection
{
    private RemoteInvocationHandler processEngineInvocationHandler;

    public ProcessEngineConnectionClient( RemoteInvocationHandler processEngineInvocationHandler )
    {
        this.processEngineInvocationHandler = processEngineInvocationHandler;
    }

    public ProcessService getProcessService()
    {
        return (ProcessService) Proxy.newProxyInstance( this.getClass().getClassLoader(),
                                                        new Class<?>[]{ProcessService.class},
                                                        new ProcessServiceInvocationHandler() );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private class ProcessServiceInvocationHandler
        implements InvocationHandler
    {
        // -----------------------------------------------------------------------
        // InvocationHandler Implementation
        // -----------------------------------------------------------------------

        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            return processEngineInvocationHandler.invoke( method.getName(), method.getParameterTypes(), args );
        }
    }
}
