package org.codehaus.plexus.spe.apps.client;

import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.spe.ProcessService;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.rmi.ProcessEngineConnection;
import org.codehaus.plexus.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SpeCli
{
    private SpeClient client;

    private ProcessService processService;

    public static void main( String[] args )
        throws Exception
    {
        new SpeCli().work();
    }

    public SpeCli()
        throws Exception
    {
        client = new SpeClient();

        ProcessEngineConnection connection = client.getConnection();

        processService = connection.getProcessService();
    }

    private void work()
        throws Exception
    {
        while( true )
        {
            showMenu();

            int selection = getInteger( "select" );

            switch( selection )
            {
                case 0:
                    return;
                case 1:
                    showActiveProcesses();
                    break;
                case 2:
                    showAllProcesses();
                    break;
                case 3:
                    runProcess();
                    break;
                case 4:
                    showProcess();
                    break;
            }
        }
    }

    private void showActiveProcesses()
        throws ProcessException
    {
        System.out.println( "Active processes: " );

        for ( ProcessInstance instance : processService.getActiveProcesses() )
        {
            System.out.println( "o " + instance.getId() + ": " + instance.getProcessId() );
        }

        System.out.println( "-----" );
    }

    private void showAllProcesses()
        throws ProcessException
    {
        System.out.println( "Processes: " );

        for ( ProcessInstance instance : processService.getProcesses() )
        {
            System.out.println( "o " + instance.getId() + ": " + instance.getProcessId() );
        }

        System.out.println( "-----" );
    }

    private void runProcess()
        throws Exception
    {
        String processId = client.getPrompter().prompt( "Process id: " );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        String instanceId = processService.executeProcess( processId, context );

        System.out.println( "Started process, instance id: " + instanceId );
    }

    private void showProcess()
        throws Exception
    {
        String instanceId = client.getPrompter().prompt( "Instance id: " );

        ProcessInstance instance = processService.getProcessInstance( instanceId );

        System.out.println( "Start time: " + instance.getCreatedTime() );
        System.out.println( "End time: " + instance.getCreatedTime() );
        System.out.println( "Error message: " + StringUtils.clean( instance.getErrorMessage() ) );
        Map<String, Serializable> context = instance.getContext();
        System.out.println( "Context: " + new TreeMap<String, Serializable>( context ).toString().replace( '.', '\n') );
    }

    private int getInteger( String prompt )
        throws Exception
    {
        while ( true )
        {
            String selection = client.getPrompter().prompt( prompt );

            if ( selection == null )
            {
                return 0;
            }

            try
            {
                return Integer.parseInt( selection );
            }
            catch ( NumberFormatException e )
            {
                System.out.println( "The response has to be an integer." );
            }
        }
    }

    private void showMenu()
    {
        System.err.println( "[0]: exit" );
        System.err.println( "[1]: list active processes" );
        System.err.println( "[2]: list all processes" );
        System.err.println( "[3]: run process" );
        System.err.println( "[4]: show process instance" );
    }
}
