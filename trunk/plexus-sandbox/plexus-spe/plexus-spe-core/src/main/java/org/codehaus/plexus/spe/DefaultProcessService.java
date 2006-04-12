package org.codehaus.plexus.spe;

import org.codehaus.plexus.spe.execution.ProcessExecutor;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.io.xpp3.ProcessDescriptorXpp3Reader;
import org.codehaus.plexus.spe.store.ProcessInstanceStore;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultProcessService
    extends AbstractLogEnabled
    implements ProcessService, Initializable, Startable
{
    /**
     * @plexus.requirement
     */
    private ProcessInstanceStore store;

    /**
     * @plexus.requirement
     */
    private ProcessExecutor executor;

    private Map<String, ProcessDescriptor> processes = new HashMap<String, ProcessDescriptor>( );

    // ----------------------------------------------------------------------
    // ProcessService Implementation
    // ----------------------------------------------------------------------

    public ProcessDescriptor loadProcess( URL url )
        throws ProcessException
    {
        ProcessDescriptorXpp3Reader reader = new ProcessDescriptorXpp3Reader();

        ProcessDescriptor process;

        try
        {
            process = reader.read( new InputStreamReader( url.openStream() ) );
        }
        catch ( IOException e )
        {
            throw new ProcessException( "Error while reading process descriptor.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new ProcessException( "Error while reading process descriptor.", e );
        }

        validateProcess( process );

        processes.put( process.getId(), process );

        return process;
    }

    public int executeProcess( String processId, Map<String, Serializable> context )
        throws ProcessException
    {
        ProcessDescriptor process = processes.get( processId );

        if ( process == null )
        {
            throw new ProcessException( "No such process: " + processId );
        }

        ProcessInstance processState = store.createInstance( process, context );

        executor.startProcess( process, processState );

        return processState.getInstanceId();
    }

    public boolean hasCompleted( int instanceId )
        throws ProcessException
    {
        ProcessInstance processInstance = store.getInstance( instanceId, false );

        return processInstance.getEndTime() != 0;
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private void validateProcess( ProcessDescriptor process )
        throws ProcessException
    {
        String id = process.getId();

        if ( StringUtils.isEmpty( id ) )
        {
            throw new ProcessException( "Invalid process descriptor: Missing id." );
        }

        String defaultExecutorId = process.getDefaultExecutorId();

        int i = 0;

        for ( StepDescriptor stepDescriptor : (List<StepDescriptor>) process.getSteps() )
        {
            if ( StringUtils.isEmpty( stepDescriptor.getExecutorId() ) )
            {
                stepDescriptor.setExecutorId( defaultExecutorId );

                if ( StringUtils.isEmpty( stepDescriptor.getExecutorId() ) )
                {
                    throw new ProcessException( "Invalid process descriptor: Step #" + i + " is missing 'executor id'." );
                }
            }

            if ( stepDescriptor.getExecutorConfiguration() == null )
            {
                stepDescriptor.setExecutorConfiguration( new Xpp3Dom( "executorConfiguration" ) );
            }

            if ( stepDescriptor.getConfiguration() == null )
            {
                stepDescriptor.setConfiguration( new Xpp3Dom( "configuration" ) );
            }

            i++;
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }

    public void start()
        throws StartingException
    {
        try
        {
            getLogger().info( "Active processes:" );
            for ( ProcessInstance instance : store.getActiveInstances() )
            {
                getLogger().info( " Instance: " + instance.getInstanceId() + " of process: " + instance.getProcessId() );
            }
        }
        catch ( ProcessException e )
        {
            throw new StartingException( "Error while loading processes.", e );
        }

    }

    public void stop()
        throws StoppingException
    {
    }
}
