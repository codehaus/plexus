package org.codehaus.plexus.spe.apps.client;

import org.codehaus.plexus.spe.ProcessService;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnection;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnector;
import org.codehaus.plexus.spe.rmi.client.ProcessEngineClient;

import java.util.Collection;
import java.util.HashMap;
import java.rmi.registry.Registry;
import java.io.File;
import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class TestClient
{
    public static void main( String[] args )
        throws Exception
    {
        ProcessEngineClient client = new ProcessEngineClient( "localhost", Registry.REGISTRY_PORT );

        ProcessEngineConnector connector = client.getConnector();

        ProcessEngineConnection connectionServer = connector.getConnection();

        ProcessService processService = connectionServer.getProcessService();

        Collection<? extends ProcessInstance> processes = processService.getActiveProcesses();

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        System.out.println( "Active processes:" );

        for ( ProcessInstance instance : processes )
        {
            System.out.println( "instance.getId() = " + instance.getId() );
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        File file = new File( "src/test/resources/process-1.xml" );

        System.err.println( "1" );
        ProcessDescriptor descriptor = processService.loadProcess( file.toURL() );
        System.out.println( "descriptor.getId() = " + descriptor.getId() );
        System.err.println( "2" );

        String instanceId = processService.executeProcess( descriptor.getId(), new HashMap<String, Serializable>() );

        ProcessInstance instance = processService.getProcessInstance( instanceId );

        System.out.println( "instance.getCreatedTime() = " + instance.getCreatedTime() );
    }
}
